# FastDfs环境配置

## fastdfs安装

### 安装libfastcommon

```shell	
# 解压
mkdir -p  /usr/local/fastdfs
tar -zxvf V1.0.43.tar.gz -C /usr/local/fastdfs/
cd /usr/local/fastdfs/libfastcommon-1.0.43/
# 编译 安装
./make.sh 
./make.sh install
# 软链接
ln -s /usr/lib64/libfastcommon.so /usr/local/lib/libfastcommon.so
ln -s /usr/local/lib64/libfdfsclient.so /usr/local/lib/libfdfsclient.so
ln -s /usr/local/lib64/libfdfsclient.so /usr/lib/libfdfsclient.so
```

### 安装fastdfs

```shell	
# 解压
tar zxvf V6.02.tar.gz -C /usr/local/fastdfs/
cd /usr/local/fastdfs/fastdfs-6.02/
# 修改配置文件(集群不要修改)
vim make.sh
    TARGET_PREFIX=$DESTDIR/usr/local
# 编译安装
./make.sh
./make.sh install
```

## Tracker配置

```shell	
cd /etc/fdfs/
cp tracker.conf.sample tracker.conf # 复制配置
vim tracker.conf # 修改配置
	# the base path to store data and log files
	base_path=/fastdfs/tracker
mkdir -p /fastdfs/tracker
    ./fdfs_trackerd start # 启动
./fdfs_trackerd status # 查看状态
./fdfs_trackerd stop #停止
# 修改为开机自启
vim /etc/rc.d/rc.local
	touch /var/lock/subsys/local
	/etc/init.d/fdfs_tracked start
```

## Storage配置

```shell
cd /etc/fdfs/
cp storage.conf.sample storage.conf
# 修改配置
vim storage.conf
    # the base path to store data and log files
    base_path=/fastdfs/storage/base
    # store_path#, based 0, if store_path0 not exists, it's value is base_path
    # the paths must be exist
    # NOTE: the store paths' order is very important, don't mess up.
    store_path0=/fastdfs/storage/store
    #   and the HOST can be dual IPs or hostnames seperated by comma,
    #   the dual IPS must be an intranet IP and an extranet IP.
    #   such as: 192.168.2.100,122.244.141.46
    tracker_server=192.168.9.5:22122
# 建文件夹
mkdir -p /fastdfs/storage/base
mkdir -p /fastdfs/storage/store
# 修改启动文件配置
vim /etc/init.d/fdfs_storaged 
	PRG=/usr/local/bin/fdfs_storaged
/etc/init.d/fdfs_storaged start
/etc/init.d/fdfs_storaged status
```

## Client配置

```shell
cd /etc/fdfs/
cp client.conf.sample client.conf
# 配置
vim client.conf
	# the base path to store log files
    base_path=/fastdfs/client
    # tracker_server can ocur more than once for multi tracker servers.
    # the value format of tracker_server is "HOST:PORT",
    #   the HOST can be hostname or ip address,
    #   and the HOST can be dual IPs or hostnames seperated by comma,
    #   the dual IPS must be an intranet IP and an extranet IP.
    #   such as: 192.168.2.100,122.244.141.46
    tracker_server=192.168.9.3:22122
mkdir -p /fastdfs/client
cd /usr/local/bin
# 测试上传
./fdfs_upload_file /etc/fdfs/client.conf ~/vmware-tools-distrib/
```

## 安装nginx

谷粒商城的nginx在`/mydata/nginx`

### 解压Nginx安装信息

```shell
tar -zxvf nginx-1.16.1.tar.gz -C /usr/local/fastdfs/
```

### 配置nginx安装信息	

```shell
mkdir -p /var/temp/nginx

./configure \
--prefix=/usr/local/nginx \
--pid-path=/var/run/nginx/nginx.pid \
--lock-path=/var/lock/nginx.lock \
--error-log-path=/var/log/nginx/error.log \
--http-log-path=/var/log/nginx/access.log \
--with-http_gzip_static_module \
--http-client-body-temp-path=/var/temp/nginx/client \
--http-proxy-temp-path=/var/temp/nginx/proxy \
--http-fastcgi-temp-path=/var/temp/nginx/fastcgi \
--http-uwsgi-temp-path=/var/temp/nginx/uwsgi \
--http-scgi-temp-path=/var/temp/nginx/scgi \
--add-module=/usr/local/fastdfs/fastdfs-nginx-module-1.22/src
# 安装
make 
make install
```

#### Nginx模块配置

```shell
cd /usr/local/fastdfs/fastdfs-nginx-module-1.22/src
cp mod_fastdfs.conf /etc/fdfs/
vim mod_fastdfs.conf
    # default value is 30s
    connect_timeout=10
    # FastDFS tracker_server can ocur more than once, and tracker_server format is
    #  "host:port", host can be hostname or ip address
    # valid only when load_fdfs_parameters_from_tracker is true
    tracker_server=192.168.9.3:22122
	# store_path#, based 0, if store_path0 not exists, it's value is base_path
    # the paths must be exist
    # must same as storage.conf
    store_path0=/fastdfs/storage/store	
cp /usr/local/fastdfs/fastdfs-6.04/conf/http.conf /etc/fdfs/
cp /usr/local/fastdfs/fastdfs-6.04/conf/mime.types /etc/fdfs/
ln -s /usr/local/lib64/libfdfsclient.so /usr/lib64/libfdfsclient.so
ln -s /fastdfs/storage/store/data/ /fastdfs/storage/store/data/M00
vim nginx.conf
	http {
    include       mime.types;
    default_type  application/octet-stream;

    #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
    #                  '$status $body_bytes_sent "$http_referer" '
    #                  '"$http_user_agent" "$http_x_forwarded_for"';

    #access_log  logs/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    #keepalive_timeout  0;
    keepalive_timeout  65;

    #gzip  on;

    server {
        listen       8888;
        server_name  localhost;
        location ~/group[0-9]/M00{
                ngx_fastdfs_module;
        }
/etc/init.d/fdfs_storaged restart
```





