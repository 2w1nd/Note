# InnoDB记录结构

真实数据在不同存储引擎中存放的格式一般是不同的

InnoDB 采取的方式是：**将数据划分为若干个页，以页作为磁盘和内存之间交互的基本单位，InnoDB中页的大小一般为16KB**

四种行格式： 

`Compact `、 `Redundant` 、

`Dynamic`、  `Compressed`

## 1. 行格式

### 1.1  Compact行格式

