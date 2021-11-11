# MySql高级：sql调优，数据库优化

![[尚硅谷MySQL数据库高级，mysql优化，数据库优化_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV1KW411u7vy?p=3&spm_id_from=pageDriver)]

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

