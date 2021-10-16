# HashSet
![](image/2021-10-16-17-28-54.png)


```java
// 面试题
HashSet set = new HashSet();

set.add("lucy"); // ok
set.add("lucy"); // no
set.add(new Dog("tom")); // ok
set.add(new Dog("tom")); // no
set.add(new String("w1nd")); // ok
set.add(new String("w1nd")); // ok

```
**数组链表模拟**
```java
package com.w1nd.collection;

public class HashSetStructure {
    public static void main(String[] args) {
        Node[] table = new Node[16];

        Node john = new Node("john", null);
        table[2] = john;

        Node jack = new Node("jack", null);
        john.next = jack;
        System.out.println("table=" + table);
    }
}

class Node { // 结点，存储数据，可以指向下一个结点，从而形成链表
    Object item; // 存放数据
    Node next;  // 指向下一个结点

    public Node(Object item, Node next) {
        this.item = item;
        this.next = next;
    }

    @Override
    public String toString() {
        return "Node{" +
                "item=" + item +
                ", next=" + next +
                '}';
    }
}
```