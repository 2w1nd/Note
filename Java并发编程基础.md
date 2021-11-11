# Java并发编程基础

## 1. 线程简介

### 1. 1线程状态

```java
package Thread;

import utils.SleepUtils;

public class ThreadState {
    public static void main(String[] args) {
        new Thread(new TimeWaiting(), "TimeWaitingThread").start();
        new Thread(new Waiting(), "WaitingThread").start();
        // 使用两个Blocked线程，一个获取锁成功，另一个被阻塞
        new Thread(new Blocked(), "BlockedThread-1").start();
        new Thread(new Blocked(), "BlockedThread-2").start();
    }

    // 该线程不断地进行睡眠
    static class  TimeWaiting implements Runnable {
        @Override
        public void run() {
            while (true) {
                SleepUtils.second(100);
            }
        }
    }

    // 该线程在waiting.class实例上等待
    static class Waiting implements Runnable {
        @Override
        public void run() {
            while (true) {
                synchronized (Waiting.class) {
                    try {
                        Waiting.class.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    static class Blocked implements Runnable {
        @Override
        public void run() {
            synchronized (Blocked.class) {
                while (true) {
                    SleepUtils.second(100);
                }
            }
        }
    }
}
```

​	启动程序后，在终端使用`jps`查看进程号

```java
PS D:\java1\javase> jps
23376 Launcher
5168 ThreadState   <-------------该进程
4692 RemoteMavenServer36  
7524
4168 Jps
```

​	使用`jstack 5168`查看线程

```java
"BlockedThread-2" #15 prio=5 os_prio=0 tid=0x000000001e851800 nid=0x1cf4 waiting for monitor entry [0x000000002061f000]
   java.lang.Thread.State: BLOCKED (on object monitor) <--------阻塞
        at Thread.ThreadState$Blocked.run(ThreadState.java:45)
        - waiting to lock <0x000000076bebcea8> (a java.lang.Class for Thread.ThreadState$Blocked)
        at java.lang.Thread.run(Thread.java:748)

"BlockedThread-1" #14 prio=5 os_prio=0 tid=0x000000001e853000 nid=0x4038 waiting on condition [0x000000002051f000]
   java.lang.Thread.State: TIMED_WAITING (sleeping) <---------------获得锁，等待中
        at java.lang.Thread.sleep(Native Method)
        at java.lang.Thread.sleep(Thread.java:340)
        at java.util.concurrent.TimeUnit.sleep(TimeUnit.java:386)
        at utils.SleepUtils.second(SleepUtils.java:8)
        at Thread.ThreadState$Blocked.run(ThreadState.java:45)
        - locked <0x000000076bebcea8> (a java.lang.Class for Thread.ThreadState$Blocked)
        at java.lang.Thread.run(Thread.java:748)

"WaitingThread" #13 prio=5 os_prio=0 tid=0x000000001e850000 nid=0x72c in Object.wait() [0x000000002041e000]
   java.lang.Thread.State: WAITING (on object monitor)  <---------------等待
        at java.lang.Object.wait(Native Method)
        - waiting on <0x000000076bebad20> (a java.lang.Class for Thread.ThreadState$Waiting)
        at java.lang.Object.wait(Object.java:502)
        at Thread.ThreadState$Waiting.run(ThreadState.java:31)
        - locked <0x000000076bebad20> (a java.lang.Class for Thread.ThreadState$Waiting)
        at java.lang.Thread.run(Thread.java:748)

"TimeWaitingThread" #12 prio=5 os_prio=0 tid=0x000000001e82b000 nid=0x5fe4 waiting on condition [0x000000002031f000]
   java.lang.Thread.State: TIMED_WAITING (sleeping) <--------------------超时等待
        at java.lang.Thread.sleep(Native Method)
        at java.lang.Thread.sleep(Thread.java:340)
        at java.util.concurrent.TimeUnit.sleep(TimeUnit.java:386)
        at utils.SleepUtils.second(SleepUtils.java:8)
        at Thread.ThreadState$TimeWaiting.run(ThreadState.java:19)
        at java.lang.Thread.run(Thread.java:748)
```

![image-20211110175552380](image/image-20211110175552380.png)

​	进入等待状态地线程需要依靠其他线程的通知才能够返回到运行状态，而超时等待状态相当于在等待状态的基础上增加了超时限制，也就是超时时间到达时将会回到运行状态。

### 1.2 Daemon线程

​	`Daemon`线程是一种支持型线程，主要被用作程序中后台调度以及支持性工作。当一个java虚拟机中不存在非Daemon线程时，Java虚拟机将会退出。通过`Thread.setDaemon(true)`将线程设置为`Daemon`线程.

```java
package Thread;

import utils.SleepUtils;

public class Daemon {
    public static void main(String[] args) {
        Thread thread = new Thread(new DaemonRunner(), "DaemonRunner");
        thread.setDaemon(true);
        thread.start();
    }

    static class DaemonRunner implements Runnable {
        @Override
        public void run() {
            try {
                SleepUtils.second(10);
            } finally {
                System.out.println("DaemonThread finally run");
            }
        }
    }
}
```

​	无任何输出，守护线程随main方法执行完毕而终止。

## 2. 启动和终止线程

### 2.1 构造线程

```java
// Thread源码中初始化线程的函数
private void init(ThreadGroup g, Runnable target, String name,
                  long stackSize, AccessControlContext acc,
                  boolean inheritThreadLocals) {
    if (name == null) {
        throw new NullPointerException("name cannot be null");
    }

    this.name = name;

    Thread parent = currentThread();
    SecurityManager security = System.getSecurityManager();
    if (g == null) {
        /* Determine if it's an applet or not */

        /* If there is a security manager, ask the security manager
               what to do. */
        if (security != null) {
            g = security.getThreadGroup();
        }

        /* If the security doesn't have a strong opinion of the matter
               use the parent thread group. */
        if (g == null) {
            g = parent.getThreadGroup();
        }
    }

    /* checkAccess regardless of whether or not threadgroup is
           explicitly passed in. */
    g.checkAccess();

    /*
         * Do we have the required permissions?
         */
    if (security != null) {
        if (isCCLOverridden(getClass())) {
            security.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
        }
    }

    g.addUnstarted();

    this.group = g;
    this.daemon = parent.isDaemon();
    this.priority = parent.getPriority();
    if (security == null || isCCLOverridden(parent.getClass()))
        this.contextClassLoader = parent.getContextClassLoader();
    else
        this.contextClassLoader = parent.contextClassLoader;
    this.inheritedAccessControlContext =
        acc != null ? acc : AccessController.getContext();
    this.target = target;
    setPriority(priority);
    if (inheritThreadLocals && parent.inheritableThreadLocals != null)
        this.inheritableThreadLocals =
        ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);
    /* Stash the specified stack size in case the VM cares */
    this.stackSize = stackSize;

    /* Set thread ID */
    tid = nextThreadID();
}
```

​	一个新构造的线程对象是由其parent线程来进行空间分配的，而child线程继承了parent是否为Daemon，`优先级`和`加载资源`的`contextClassLoader`以及可继承的`ThreadLocal`，同时还会分配一个`唯一的ID`来标识这个child线程。

### 2.2 启动线程

​	调用start方法就可以启动线程，start()的含义是：当前线程同步告知Java虚拟机，只要线程规划器空闲，应立即调用`start()`方法的线程。

### 2.3 中断

​	中断表示一个==运行中的线程是否被其他线程进行了中断操作==，可理解为线程的一个标识位属性。

​	线程通过方法`isInterrupted()`来进行判断是否被中断，也可以调用静态方法`Thread.interrupted()`对当前线程的中断标识位进行复位。只要一个线程处于终结状态，在调用该线程对象`isInterrupted()`只会返回`flase`.

```java
package Thread;

import utils.SleepUtils;

import java.util.concurrent.TimeUnit;

public class Interrupted {
    public static void main(String[] args) throws InterruptedException {
        // sleepThread不停的尝试睡眠
        Thread sleepThread = new Thread(new SleepRunner(), "SleepThread");
        sleepThread.setDaemon(true);
        // busyThread不停的运行
        Thread busyRunner = new Thread(new BusyRunner(), "BusyRunner");
        busyRunner.setDaemon(true);
        sleepThread.start();
        busyRunner.start();
        // 休眠5秒，让sleepThread和busyThread充分运行
        TimeUnit.SECONDS.sleep(5);
        sleepThread.interrupt();
        busyRunner.interrupt();
        System.out.println("SleepThread interrupted is " + sleepThread.isInterrupted());
        System.out.println("BusyThread interrupted is " + busyRunner.isInterrupted());
        // 防止sleepThread和busyThread立刻退出
        SleepUtils.second(2);
    }

    static class SleepRunner implements Runnable {
        @Override
        public void run() {
            while (true) {
                SleepUtils.second(10);  // 许多声明爬出InterruptedException方法，抛出之前，会将该线程的中断标识位清除
            }
        }
    }

    static class BusyRunner implements Runnable {
        @Override
        public void run() {
            while (true) {
            }
        }
    }
}
```

​	抛出`InterruptedException`的线程`SleepThread`,其中断标识位被清除了，而一直忙碌运作的线程`BusyThread`,中断标识位没有被清除。

### 2.4 安全终止线程

- 使用中断
- 利用一个`boolean`变量来控制

```java
package Thread;

import java.util.concurrent.TimeUnit;

public class Shutdown {
    public static void main(String[] args) throws InterruptedException {
        Runner one = new Runner();
        Thread countThread = new Thread(one, "CountThread");
        countThread.start();
        // 睡眠1s，main线程对CountThread进行中断，使CountThread能够感知中断而结束
        TimeUnit.SECONDS.sleep(1);
        countThread.interrupt();
        Runner two = new Runner();
        countThread = new Thread(two, "CountThread");
        countThread.start();
        // 睡眠1s，main线程对Runner two进行取消，使CountThread能够感知on为false而结束
        TimeUnit.SECONDS.sleep(1);
        two.cancel();
    }

    private static class Runner implements Runnable {
        private long i;
        private volatile boolean on = true;

        @Override
        public void run() {
            while (on && !Thread.currentThread().isInterrupted()) {
                i ++;
            }
            System.out.println("Count i = " + i);
        }

        public void cancel() {
            on = false;
        }
    }
}
```

## 3. 线程间通信

​	任何一个对象都有一个自己的监视器，当这个对象由同步块或者这个对象的同步方法调用时，执行方法的线程必须先获取到该对象的监视器才能进入同步块或者同步方法，而没有获取到监视器的线程将会阻塞在同步块和同步方法的入口处，进入BLOCKED状态

![image-20211111154041757](image/image-20211111154041757.png)

### 3.1 等待/通知机制

```java
package Thread;

import utils.SleepUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class WaitNotify {
    static boolean flag = true;
    static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread waitThread = new Thread(new Wait(), "WaitThread");
        waitThread.start();
        TimeUnit.SECONDS.sleep(1);
        Thread notifyThread = new Thread(new Notify(), "NotifyThread");
        notifyThread.start();
    }

    static class Wait implements Runnable {
        @Override
        public void run() {
            // 加锁，拥有lock的Monitor
            synchronized (lock) {
                // 当条件不满足时，继续wait，同时释放了lock的锁
                while (flag) {
                    try {
                        System.out.println(Thread.currentThread() + " flag is true. wait @ "
                        + new SimpleDateFormat("HH:mm:ss").format(new Date()));
                        lock.wait(); // 进入等待状态，同时释放了对象的锁
                    } catch (InterruptedException e) {
                    }
                }
                // 条件满足时，完成工作
                System.out.println(Thread.currentThread() + " flag is false. running @ " +
                        new SimpleDateFormat("HH:mm:ss").format(new Date()));
            }
        }
    }

    static class Notify implements Runnable {
        @Override
        public void run() {
            // 加锁，拥有lick的Monitor
            synchronized (lock) {
                // 获取lock的锁，然后进行通知，通知时不会释放lock的锁
                // 直到当前线程释放lock后，WaitThread才能wait方法中返回
                System.out.println(Thread.currentThread() + " hold lock. notify @" +
                        new SimpleDateFormat("HH:mm:ss").format(new Date()));
                lock.notifyAll();
                flag = false;
                SleepUtils.second(5);
            }
            // 再次加锁
            synchronized (lock) {
                System.out.println(Thread.currentThread() + " hold lock again. slepp @" +
                        new SimpleDateFormat("HH:mm:ss").format(new Date()));
                SleepUtils.second(5);
            }
        }
    }
}
```

​	解释上述代码，`WaitThread`首先获取了对象的锁，然后调用对象的`wait()`方法，从而放弃了锁并进入了对象的等待队列`WaitQueue`中，进入等待状态。由于`WaitThread`释放了对象的锁，`NotifyThread`随后获取了对象的锁，并调用了对象的`notify()`方法，将`WaitThread`从**等待队列**移到**同步队列**中，此时`WaitThread`状态变为**阻塞状态**。等待`NotifyThread`释放锁之后，`WaitThread`再次获得锁并从`wait()`方法返回继续执行

**总结**

1. 使用`wait()`,`notify()`和`notifyAll()`时需要先对调用对象加锁
2. 调用`wait()`方法后，线程状态由`RUNNING`变为`WAITING`，并将当前线程放置到该对象的等待队列
3. `notify()`或`notifyAll()`方法调用后，等待线程依旧不会从`wait()`返回，需要`notify()`或`notifyAll()`的线程释放锁之后，等待线程才有机会从`wait()`返回
4. `notify()`方法将等待队列中的一个等待线程从等待队列移到同步队列中，`notifyAll`方法则是将等待队列中所有的线程全部移到同步队列，被移动的线程状态由`WAITING`变为`BLOCKED`
5. 从`wait()`方法返回的前提是获得了调用对象的锁

### 3.2 等待/通知的经典范式

**等待方**遵循如下原则：

1. 获取对象的锁
2. 如果条件不满足，那么调用对象的`wait()`方法，被通知后仍要检查条件
3. 条件满足则执行相应的逻辑

```java
synchronized (对象) {
    while (条件不满足) {
        对象.wait()
    }
    对应处理逻辑
}
```

**通知方**遵循如下原则：

1. 获取对象的锁
2. 改变条件
3. 通知所有等待在对象的线程

```java
synchronized (对象) {
    改变条件
    对象.notifyAll();
}
```

### 3.3 管道输入/输出流

```java
package Thread;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

public class Piped {
    public static void main(String[] args) throws IOException {
        PipedWriter out = new PipedWriter();
        PipedReader in = new PipedReader();
        // 将输出流和输入流进行连接，否则在使用时会抛出IOException
        out.connect(in);;
        Thread printThread = new Thread(new Print(in), "PrintThread");
        printThread.start();
        int receive = 0;
        try {
            while ((receive = System.in.read()) != -1) {
                out.write(receive);
            }
        } finally {
            out.close();
        }
    }

    static class Print implements Runnable {
        private PipedReader in;
        public Print (PipedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            int receive = 0;
            try {
                while ((receive = in.read()) != -1) {
                    System.out.print((char) receive);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
```

​	管道输入/输出主要用于线程之间的数据传输，而传输的媒介为内存

​	如果没有将输入/输出流绑定起来，对于该流的访问将会抛出异常

### 3.4 Thread.join()的使用

`thread.join()`：当前线程A等待`Thread`线程终止之后才从`Thread.join()`返回

```java
package Thread;

import java.util.concurrent.TimeUnit;

public class Join {
    public static void main(String[] args) throws InterruptedException {
        Thread previous = Thread.currentThread();
        for (int i = 0; i < 10; i++) {
            // 每个线程拥有前一个线程的引用，需要等待前一个线程终止，才能从等待中返回
            Thread thread = new Thread(new Domino(previous), String.valueOf(i));
            thread.start();
            previous = thread;
        }
        TimeUnit.SECONDS.sleep(5);
        System.out.println(Thread.currentThread().getName() + " terminate.");
    }

    static class Domino implements Runnable {
        private Thread thread;
        public Domino(Thread thread) {
            this.thread = thread;
        }

        @Override
        public void run() {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
            System.out.println(Thread.currentThread().getName() + " terminate.");
        }
    }
}
```

### 3.5 ThreadLocal的使用

​	ThreadLocal，即线程变量，是一个以ThreadLocal对象为键，任意对象为值的存储结构。

```java
package Thread;

import java.lang.*;
import java.util.concurrent.TimeUnit;

public class Profiler {
    // 第一次调用get()会进行初始化，如果没有使用set方法进行，每个线程会调用一个
    private static final ThreadLocal<Long> TIME_THREADLOCAL = new ThreadLocal<Long>() {
        @Override
        protected Long initialValue() {
            return System.currentTimeMillis();
        }
    };

    public static final void begin() {
        TIME_THREADLOCAL.set(System.currentTimeMillis());
    }

    public static final long end() {
        return System.currentTimeMillis() - TIME_THREADLOCAL.get();
    }

    public static void main(String[] args) throws InterruptedException {
        Profiler.begin();
        TimeUnit.SECONDS.sleep(1);
        System.out.println("Cost：" + Profiler.end() + " mills");
    }
}
```

