# gdb

安装gdb-multiarch

```shell
sudo apt-get install gdb-multiarch
```

在wsl中执行

```shell
echo "add-auto-load-safe-path /mnt/d/xv6-labs-2020/.gdbinit " >> ~/.gdbinit  # 注意后面是.gdbinit的路径
```

打开一个窗口输入

```shell
make qemu-gdb
```

再打开输入

```shell
gdb-multiarch
```

两个窗口都在xv-lab-2020目录下打开

gdb中常用指令

```shell
n # 下一个指令
c # 继续，单步执行
tui enable # 打开调试界面
delete # 删除所有端点
layout split # 得到 c 和汇编窗口
layout source # 得到 c
info breakpoints # 查看所有断点
info reg # 查看寄存器的信息
info frame # 查看栈帧信息
backtree (简写bt) # 查看从当前栈开始所有的S
print $pc # d
step[i]  # i 是指行数，指指向行命令，默认1h
```

gdb使用x命令	**查看内存地址中的值**

格式：`x/<n/f/u> <addr>`

- n:是正整数，表示需要显示的内存单元的个数，即从当前地址向后显示n个内存单元的内容，一个内存单元的大小由第三个参数u定义。



- f:表示addr指向的内存内容的输出格式，s对应输出字符串，此处需特别注意输出整型数据的格式：

​		x 按十六进制格式显示变量。

​		d 按十进制格式显示变量。

​		u 按十六进制格式显示无符号整型。

​		o 按八进制格式显示变量。

​		t 按二进制格式显示变量。

​		a 按十六进制格式显示变量。

​		c 按字符格式显示变量。

​		f 按浮点数格式显示变量。

- u:就是指以多少个字节作为一个内存单元-unit,默认为4。当然u还可以用被一些字符表示，如b=1 byte, h=2 bytes,w=4 bytes,g=8 bytes.

- \<addr>:表示内存地址。

这个命令的诠释：**就是以addr为起始地址，返回n个单元的值，每个单元对应u个字节，输出格式是f。**

`x/ 3uh 0x54320`表示：以地址0x54320为起始地址，返回3个单元的值，每个单元有两个字节，输出格式为无符号十六进制。

也就是说返回了3*2=6个字节的数据，以十六进制输出，这6个字节的数据，每两个字节为一个单元输出，共输出3个单元。

---

tmux

`tmux`进入

先按下`ctrl+b`

再按

```shell
o # 切换窗口
% # 垂直分屏
空格  # 水平分屏
[   # 滚动模式
```

