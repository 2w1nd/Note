# Lab1

## Boot xv6

搭建环境

## sleep 

```c
#include "kernel/types.h"
#include "user/user.h"

int main(int argc, char *argv[]) {
	if (argc != 2) {
		write(2, "Usage: sleep time\n", strlen("Usage: sleep time\n"));
		exit(1);
	}
	int time = atoi(argv[1]);
	sleep(time);
	exit(0);
}

```

## pingpong

```c
#include "kernel/types.h"
#include "user.h"
#include "stddef.h"

/*
  管道两端可 分别用描述字fd[0]以及fd[1]来描述，需要注意的是，管道的两端是固定了任务的。
  即一端只能用于读，由描述字fd[0]表示，称其为管道读端；另 一端则只能用于写，由描述字fd[1]来表示，称其为管道写端。
  如果试图从管道写端读取数据，或者向管道读端写入数据都将导致错误发生。
*/
int main(int arc, char *argv[])
{
  int ptoc_fd[2], ctop_fd[2];
  pipe(ptoc_fd);
  pipe(ctop_fd);

  char buf[8];
  if (fork() == 0)
  {
    // child process
    read(ptoc_fd[0], buf, 4);
    printf("%d: received %s\n", getpid(), buf);
    write(ctop_fd[1], "pong", strlen("pong"));
  }
  else
  {
    // parent process
    write(ptoc_fd[1], "ping", strlen("ping"));
    wait(NULL);
    read(ctop_fd[0], buf, 4);
    printf("%d: received %s\n", getpid(), buf);
  }
  exit(0);
}
```

## primes

### 解法1

```c
#include "kernel/types.h"
#include "user/user.h"
#include "stddef.h"

#define MSGSIZE 36
#define ONE '1'
#define ZERO '0'

void prime(int pipe_read, int pipe_write)
{
  char nums[MSGSIZE];
  int val = 0;
  read(pipe_read, nums, MSGSIZE);

  for (int i = 0; i < MSGSIZE; i++)
  {
    if (nums[i] == ONE)
    {
      val = i;
      break;
    }
  }

  // printf("%s\n", nums);
  if (val == 0)
    exit(0);
  printf("prime %d\n", val);
  nums[val] = ZERO;
  // 消除val倍数的数
  for (int i = 0; i < MSGSIZE; i++)
  {
    if (i % val == 0)
    {
      nums[i] = ZERO;
    }
  }
  int pid = fork();
  if (pid > 0)
  {
    write(pipe_write, nums, MSGSIZE);
  }
  if (pid == 0)
  {
    prime(pipe_read, pipe_write);
  }
}

int main()
{
  int fd[2];
  pipe(fd);
  char nums[MSGSIZE];
  for (int i = 2; i < MSGSIZE; i++)
  {
    nums[i] = ONE;
  }

  int pid = fork();
  if (pid > 0)  // 父进程写
  {
    nums[0] = ZERO;
    nums[1] = ZERO;
    write(fd[1], nums, MSGSIZE);
    wait(0);
  }

  if (pid == 0)  // 子进程读
  {
    prime(fd[0], fd[1]);
    wait(0);
  }
  exit(0);
}

```

### 解法2

```c
#include "kernel/types.h"
#include "user/user.h"
#include "stddef.h"

// 这是一个映射函数，可以让p[]对应读/写描述符映射给n，节约资源。
void mapping(int n, int p[])
{
  close(n);
  dup(p[n]);
  close(p[0]);
  close(p[1]);
}

void primes()
{
  int pre, next;
  int fd[2];
  if (read(0, &pre, sizeof(int)))
  {
    // 第一个数一定是素数
    printf("prime %d\n", pre);
    // 创建管道
    pipe(fd);
    if (fork() == 0) //子进程
    {
      mapping(1, fd); // 映射写端
      while (read(0, &next, sizeof(int))) // 从读端读数据
      {
        if ((next % pre != 0))
        {
          write(1, &next, sizeof(int));
        }
      }
    }
    else // 父进程
    {
      wait(NULL);
      mapping(0, fd);
      primes(); // 递归处理
    }
  }
}

int main()
{
  int fd[2];
  pipe(fd);
  if (fork() == 0) // 子进程负责写
  {
    mapping(1, fd);
    for (int i = 2; i < 36; i++)
    {
      write(1, &i, sizeof(int));
    }
  }
  else
  {
    wait(NULL);
    mapping(0, fd);
    primes();
  }
  exit(0);
}
```

## find

```c
struct stat {
  int dev;     // File system's disk device
  uint ino;    // Inode number
  short type;  // Type of file
  short nlink; // Number of links to file
  uint64 size; // Size of file in bytes
};
```

```c
struct dirent {
  ushort inum;
  char name[DIRSIZ];
};
```

```c
#include "kernel/types.h"
#include "kernel/stat.h"
#include "user/user.h"
#include "kernel/fs.h"

char *
fmtname(char *path)
{
  static char buf[DIRSIZ + 1];
  char *p;

  // Find first character after last slash.
  for (p = path + strlen(path); p >= path && *p != '/'; p--)
    ;
  p++;

  // Return blank-padded name.
  if (strlen(p) >= DIRSIZ)
    return p;
  memmove(buf, p, strlen(p));
  memset(buf + strlen(p), 0, DIRSIZ - strlen(p));
  return buf;
}

int norecurse(char *path)
{
  char *buf = fmtname(path);
  if (buf[0] == '.' && buf[1] == 0)
  {
    return 1;
  }
  if (buf[0] == '.' && buf[1] == '.' && buf[2] == 0)
  {
    return 1;
  }
  return 0;
}

void find(char *path, char *target)
{
  char buf[512], *p;
  int fd;
  struct dirent de;
  struct stat st;

  if ((fd = open(path, 0)) < 0)
  {
    fprintf(2, "find: cannot open %s\n", path);
    return;
  }

  if (fstat(fd, &st) < 0)
  {
    fprintf(2, "find: cannot stat %s\n", path);
    close(fd);
    return;
  }

  if (strcmp(fmtname(path), target) == 0)
  {
    printf("%s\n", path);
  }

  switch (st.type)
  {
  case T_FILE:
    break;

  case T_DIR:
    if (strlen(path) + 1 + DIRSIZ + 1 > sizeof buf)
    {
      printf("find: path too long\n");
      break;
    }
    strcpy(buf, path);
    p = buf + strlen(buf);
    *p++ = '/';
    while (read(fd, &de, sizeof(de)) == sizeof(de))
    {
      if (de.inum == 0)
        continue;
      memmove(p, de.name, DIRSIZ);
      p[DIRSIZ] = 0;
      if (stat(buf, &st) < 0)
      {
        printf("find: cannot stat %s\n", buf);
        continue;
      }

      if (norecurse(buf) == 0)
      {
        find(buf, target);
      }
    }
    break;
  }
  close(fd);
}

int main(int argc, char *argv[])
{
  if (argc == 3)
  {
    find(argv[1], argv[2]);
    exit(0);
  }
  else if (argc == 2)
  {
    find(".", argv[1]);
    exit(0);
  }
  else
  {
    fprintf(2, "usage: argc no enough");
    exit(1);
  }
  exit(0);
}

```

## xargs

```c
#include "kernel/types.h"
#include "kernel/stat.h"
#include "user/user.h"
#include "kernel/fs.h"
#include "kernel/param.h"

#define MSGSIZE 16

int main(int argc, char *argv[])
{
  sleep(10);
  // 1、 读取标准化输入
  char buf[MSGSIZE];
  read(0, buf, MSGSIZE);

  // 2、 获取xargs后面的命令行参数
  char *xargv[MAXARG];
  int xargc = 0;
  for (int i = 1; i < argc; i++)
  {
    xargv[xargc++] = argv[i];
  }

  char *p = buf;
  for (int i = 0; i < MSGSIZE; i++)
  {
    if (buf[i] == '\n')
    {
      int pid = fork();
      if (pid > 0)
      { // 父进程
        p = &buf[i + 1];
        wait(0);
      }
      else // 子进程执行命令
      {
        buf[i] = 0;
        xargv[xargc++] = p;
        xargv[xargc++] = 0;
        exec(xargv[0], xargv);
        exit(0);
      }
    }
  }
  wait(0);
  exit(0);
}
```







