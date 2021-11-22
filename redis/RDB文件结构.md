# RDB文件结构

![image-20211122171716348](https://gitee.com/w1nd1/pic-go-pic/raw/master/blog/image-20211122171716348.png)

`db_version`为4字节，记录RDB文件的版本号

`databases`部分包含0或多个数据库，以及各个数据库的键值对数据

`EOF`标志RDB文件正文内容的结束

`check_sum`是一个8字节长的无符号整数，保存着一个校验和

## databases部分

​	对于该部分每个非空数据库，在RDB中可以保存为下图

![image-20211122172044648](https://gitee.com/w1nd1/pic-go-pic/raw/master/blog/image-20211122172044648.png)

​	`SELECTDB`：长度为1字节，提示下一个读入的将是一个数据库号码

​	`db_number`：保存一个数据库号码，读入后，会调用SELECT命令，切换数据库

​	`key_value_pairs`：保存了数据库中的所有键值对数据，如果有过期时间，那么也会保存在一起

### key_value_pairs部分

​	**不带过期时间的键值对**

​	![image-20211122182425295](https://gitee.com/w1nd1/pic-go-pic/raw/master/blog/image-20211122182425295.png)

​	`Type`：

❑REDIS_RDB_TYPE_STRING

❑REDIS_RDB_TYPE_LIST

❑REDIS_RDB_TYPE_SET

❑REDIS_RDB_TYPE_ZSET

❑REDIS_RDB_TYPE_HASH

❑REDIS_RDB_TYPE_LIST_ZIPLIST

❑REDIS_RDB_TYPE_SET_INTSET

❑REDIS_RDB_TYPE_ZSET_ZIPLIST

❑REDIS_RDB_TYPE_HASH_ZIPLIST

​	决定如何读入和解释value的数据

**带过期时间的键值对**

​	![image-20211122182606404](https://gitee.com/w1nd1/pic-go-pic/raw/master/blog/image-20211122182606404.png)

​	`EXPIERTIME_MS`：长度1字节，告知程序，接下来读入一个毫秒为单位的过期时间

​	`ms`：8字节长的带符号整数，记录着一个以毫秒为单位的UNIX时间戳

#### value

1.**字符串对象**

- 如果TYPE的值为REDIS_RDB_TYPE_STRING，那么value保存的就是一个字符串对象

- 如果字符串对象的编码为REDIS_ENCODING_INT，那么说明对象中保存的是长度不超过32位的整数
- 如果字符串对象的编码为REDIS_ENCODING_RAW，那么说明对象所保存的是一个字符串值，根据字符串长度的不同，有压缩和不压缩两种方法来保存这个字符串：
    - 如果字符串的长度小于等于20字节，那么这个字符串会直接被原样保存
    - 如果字符串的长度大于20字节，那么这个字符串会被压缩之后再保存

**无压缩字符串的保存结构**

![image-20211122183233028](https://gitee.com/w1nd1/pic-go-pic/raw/master/blog/image-20211122183233028.png)

**压缩后字符串的保存结构**

​	![image-20211122183300754](https://gitee.com/w1nd1/pic-go-pic/raw/master/blog/image-20211122183300754.png)

​	`REDIS_RDB_ENC_LZF`常量标志着字符串已经被LZF算法压缩过了。

- compressed_len记录的是字符串被压缩之后的长度

- origin_len记录的是字符串原来的长度

- compressed_string记录的则是被压缩之后的字符串。

2.**列表对象**

​	如果TYPE的值为REDIS_RDB_TYPE_LIST，那么value保存的就是一个REDIS_ENCODING_LINKEDLIST编码的列表对象

​		![image-20211122183746895](https://gitee.com/w1nd1/pic-go-pic/raw/master/blog/image-20211122183746895.png)

- list_length记录了列表的长度，它记录列表保存了多少个项
- item开头的部分代表列表的项，每个列表项都是一个字符串对象

例子

![image-20211122183908529](https://gitee.com/w1nd1/pic-go-pic/raw/master/blog/image-20211122183908529.png)

3.**集合对象**

​	如果TYPE的值为REDIS_RDB_TYPE_SET，那么value保存的就是一个REDIS_ENCODING_HT编码的集合对象

​	![image-20211122184018674](https://gitee.com/w1nd1/pic-go-pic/raw/master/blog/image-20211122184018674.png)

- set_size是集合的大小，它记录集合保存了多少个元素
- elem开头的部分代表集合的元素

4.**哈希表对象**

​	如果TYPE的值为REDIS_RDB_TYPE_HASH，那么value保存的就是一个REDIS_ENCODING_HT编码的集合对象

​	![image-20211122184129487](https://gitee.com/w1nd1/pic-go-pic/raw/master/blog/image-20211122184129487.png)

- hash_size记录了哈希表的大小，也即是这个哈希表保存了多少键值对
- 以key_value_pair开头的部分代表哈希表中的键值对，键值对的键和值都是字符串对象

5.**有序集合对象**

​	如果TYPE的值为REDIS_RDB_TYPE_ZSET，那么value保存的就是一个REDIS_ENCODING_SKIPLIST编码的有序集合对象

![image-20211122185419520](https://gitee.com/w1nd1/pic-go-pic/raw/master/blog/image-20211122185419520.png)

- sorted_set_size记录了有序集合的大小，也即是这个有序集合保存了多少元素
- 以element开头的部分代表有序集合中的元素，每个元素又分为成员（member）和分值（score）两部分，成员是一个字符串对象，分值则是一个double类型的浮点数，程序在保存RDB文件时会先将分值转换成字符串对象，然后再用保存字符串对象的方法将分值保存起来

6.**INTSET编码的集合**

​	如果TYPE的值为REDIS_RDB_TYPE_SET_INTSET，那么value保存的就是一个整数集合对象

​	RDB文件保存这种对象的方法是，先将整数集合转换为字符串对象，然后将这个字符串对象保存到RDB文件里面

7.**ZIPLIST编码的列表，哈希表或者有序集合**

​	如果TYPE的值为REDIS_RDB_TYPE_LIST_ZIPLIST、REDIS_RDB_TYPE_HASH_ZIPLIST或者REDIS_RDB_TYPE_ZSET_ZIPLIST，那么value保存的就是一个压缩列表对象，RDB文件保存这种对象方法是：

- 将压缩列表转换为字符串对象
- 将转换所得到的字符串对象保存到RDB文件

​	如果程序在读入RDB文件的过程中，碰到由压缩列表对象转换成的字符串对象，那么程序会根据TYPE值的指示，执行以下操作：

- 读入字符串对象，并将它转换成原来的压缩列表对象
- 根据TYPE的值，设置压缩列表对象的类型：如果TYPE的值为REDIS_RDB_TYPE_LIST_ZIPLIST，那么压缩列表对象的类型为列表；如果TYPE的值为REDIS_RDB_TYPE_HASH_ZIPLIST，那么压缩列表对象的类型为哈希表；如果TYPE的值为REDIS_RDB_TYPE_ZSET_ZIPLIST，那么压缩列表对象的类型为有序集合。













