# List

![](image/2021-10-15-13-17-39.png)

![](image/2021-10-15-13-31-51.png)
## ArrayList
![](image/2021-10-15-13-43-31.png)
![](image/2021-10-15-13-48-11.png)
### 扩容机制源码分析

  idea要将下图勾选去掉才可以跳转到源码  
![](image/2021-10-15-14-24-47.png)
  idea要将下图勾选去掉才可以看不省略的数据
![](image/2021-10-15-14-25-46.png)
#### 以下为过程分析（调用无参构造器）

```java
//调试代码
package com.w1nd.collection;
import java.util.*;
public class Collection_ {
    @SuppressWarnings({"all"})
    public static void main(String[] args) {
        List list = new ArrayList<>();
        for (int i = 1; i <= 10; i ++) {
            list.add(i);
        }
        list.add(150);
    }
}
```

![](image/2021-10-15-14-27-23.png)

![](image/2021-10-15-14-28-29.png)

![](image/2021-10-15-14-29-12.png)

![](image/2021-10-15-14-30-59.png)

![](image/2021-10-15-14-32-18.png)

![](image/2021-10-15-14-34-34.png)

## Vector
![](image/2021-10-15-15-01-34.png)

![](image/2021-10-15-15-04-19.png)
### 扩容机制过程
![](image/2021-10-15-15-06-34.png)

![](image/2021-10-15-15-07-04.png)

![](image/2021-10-15-15-08-02.png)

![](image/2021-10-15-15-10-29.png)

![](image/2021-10-15-15-11-06.png)

![](image/2021-10-15-15-15-17.png)

## LinkedList
![](image/2021-10-15-15-44-09.png)

![](image/2021-10-15-15-44-23.png)


### 源码阅读
#### 增加
![](image/2021-10-16-16-27-41.png)

![](image/2021-10-16-16-28-01.png)

![](image/2021-10-16-16-29-28.png)

![](image/2021-10-16-16-32-30.png)

#### 删除
![](image/2021-10-16-16-34-36.png)

![](image/2021-10-16-16-35-59.png)

![](image/2021-10-16-16-36-13.png)

![](image/2021-10-16-16-42-46.png)

## List选择

![](image/2021-10-16-16-43-25.png)