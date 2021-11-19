# MySql高级：sql调优，数据库优化

[[尚硅谷MySQL数据库高级，mysql优化，数据库优化_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV1KW411u7vy?p=3&spm_id_from=pageDriver)]

![image-20211111131348455](image/image-20211111131348455.png)

linux中第三方软件包放在opt目录下

linux下操作数据库一些linux命令：

```shell
rpm -qa | grep -i mysql # 检查是否安装mysql
rpm -ivh # 安装rpm包
id 用户名 # 是否存在该用户
/use/bin/mysqladmin -u root password 123456 #设置密码
chkconfig mysql on # 设置mysql开机自启
chkconfig --list | grep mysql
cat /etc/inittab
ntsysv  # 查看开启的服务（原始方式）

# 数据库创建的数据库存放位置
/var/lib/mysql
cp /etc/init.d/mysql/my.default.cnf /etc/my.cnf # 拷贝配置文件
```

![image-20211111133213725](image/image-20211111133213725.png)

```shell
show variables like '%char'; # 查看字符集
vim my.cnf # 修改配置文件
```

![image-20211111134151168](image/image-20211111134151168.png)

![image-20211111134258392](image/image-20211111134258392.png)

![image-20211111134337035](image/image-20211111134337035.png)

修改完配置文件需要重启MySql

已经建好的库，修改完字符集对其依然不会生效

## MySql逻辑架构

![image-20211119142445659](image/image-20211119142445659.png)

插件式的存储引擎架构将查询处理和其他系统任务以及数据的存储提取相分离

## 存储引擎简介

![image-20211119144150658](image/image-20211119144150658.png)

![image-20211119144514312](image/image-20211119144514312.png)

## Sql性能下降原因

![image-20211119145251286](image/image-20211119145251286.png)

## Sql执行加载顺序

![image-20211119145513298](image/image-20211119145513298.png)

## 七种JOIN理论

![1](image/1.jpg)

[结合该博文会更好理解]([(73条消息) 图解MySQL 内连接、外连接、左连接、右连接、全连接……太多了_plg17的专栏-CSDN博客_左连接右连接](https://blog.csdn.net/plg17/article/details/78758593))

