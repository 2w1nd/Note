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
```





tmux

`tmux`进入

先按下`ctrl+b`

再按

```shell
o # 切换窗口
% # 垂直分屏
空格  # 水平分屏
```

