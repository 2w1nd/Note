# lab3

## Print a page table

​	为了帮助你学习RISC-V页表，并可能有助于未来的调试，你的第一个任务是编写一个打印页表内容的函数

​	定义一个名为`vmprint()`的函数，应该会带有一个`pagetable_t`参数，并以下面描述的格式打印该页表。在`exec.c`中的`return argc`之前插入`if(p->pid == 1) vmprint(p->pagetable)`，打印第一个进程的页表。

​	现在，当你启动xv6时，应该像这样打印输出，描述第一个进程刚刚完成`exec`时的页表：

```
page table 0x0000000087f6e000
..0: pte 0x0000000021fda801 pa 0x0000000087f6a000
.. ..0: pte 0x0000000021fda401 pa 0x0000000087f69000
.. .. ..0: pte 0x0000000021fdac1f pa 0x0000000087f6b000
.. .. ..1: pte 0x0000000021fda00f pa 0x0000000087f68000
.. .. ..2: pte 0x0000000021fd9c1f pa 0x0000000087f67000
..255: pte 0x0000000021fdb401 pa 0x0000000087f6d000
.. ..511: pte 0x0000000021fdb001 pa 0x0000000087f6c000
.. .. ..510: pte 0x0000000021fdd807 pa 0x0000000087f76000
.. .. ..511: pte 0x0000000020001c0b pa 0x0000000080007000
```

​	第一行显示`vmprint`的参数。之后每个PTE都有一行，包括引入树种更深的页表页面的PTE。每个PTE行都缩进了一些".."，表示它在树中的深度。每个PTE行显示其表页中的PTE索引，PTE位和PTE中提取的物理地址。不要打印无效的PTE。在上面的示例中，顶级页表页具有条目0和255的映射。条目0的下一层仅映射了索引0，而该索引0的底层映射了条目0，1和2.

​	你的代码可能会发出与上面显示的不同的物理地址。条目数和虚拟地址应该相同

**Some hints**：

- 可以将`vmprint()`放在`kernel/vm.c`中
- 使用文件`kernel/risv.h`末尾的宏
- 函数`freewalk`可能会有些用
- 在`kernel/defs.h`中定义`vmprint`的原型，以便可以从`exec.c中调用`
- 在`printf`中调用中使用`%p`打印完整的64位十六进制PTE和地址，如示例所示。

​	从文中根据图 3-4 解释 `vmprint `的输出。第 0 页包含什么？第 2 页是什么？在用户模式下运行时，进程可以读/写第 1 页映射的内存吗？

```c
void pgtblprint(pagetable_t pagetable, int depth)
{
  for (int i = 0; i < 512; i++)
  {
    pte_t pte = pagetable[i];
    if (pte & PTE_V) // 如果该表项有效
    {
      printf("..");
      for (int j = 0; j < depth; j++)
        printf(" ..");
      printf("%d: pte %p pa %p\n", i, pte, PTE2PA(pte));
      if ((pte & (PTE_R | PTE_W | PTE_X)) == 0) // 如果不是叶子节点
      {
        uint64 child = PTE2PA(pte);
        pgtblprint((pagetable_t)child, depth + 1);
      }
    }
  }
}

void vmprint(pagetable_t pagetable)
{
  printf("page table %p\n", pagetable);
  pgtblprint(pagetable, 0);
}

```

## A kernel page table per process

​	XV6有一个内核页表，每当它在内核中执行时都会使用它。内核页表是直接映射到物理地址，所以内核虚拟地址x映射到物理地址x。XV6还为每个进程的用户地址空间提供一个单独的页表，仅包含进程的用户内存的映射，从虚拟地址0开始。因此，当内核需要使用系统调用中传递的用户指针（例如，传递给`write()`的缓冲区指针）时，内核必须首先将指针转换为物理地址、本节和下一节的目标是允许内核直接取消引用用户指针。

​	你的第一个工作是修改内核，以便每个进程在内核中执行时都使用自己的内核页表副本。修改`struct proc`为每个进程维护一个内核页表，修改调度器来切换进程时切换内核页表。对于这一步，每个进程内核页表应该与现有的全局内核页表相同。如果`usertests`运行正确，就通过了该实验的这一部分。

​	阅读本作业开头提到的书籍章节和代码；了解虚拟内存的工作原理后，正确修改虚拟内存代码会更容易。页表设置中的错误可能由于缺少映射而导致陷阱，可能导致加载和存储影响物理内存的意外页面，并可能导致从不正确的内存页面执行指令

**Some hints：**

- 为进程的内核页表向 `struct proc `添加一个字段。
- 为新进程生成内核页表的一种合理方法是实现 `kvminit `的修改版本，该版本生成新页表而不是修改 `kernel_pagetable`。你会想从 `allocproc` 调用这个函数。
- 确保每个进程的内核页表都有该进程的内核堆栈的映射。在未修改的 xv6 中，所有内核堆栈都在` procinit `中设置。您需要将部分或全部功能移至 `allocproc`。
- 修改 `scheduler()` 以将进程的内核页表加载到内核的 `satp` 寄存器中（请参阅 `kvminithart `以获取灵感）。不要忘记在调用 `w_satp()` 之后调用 `sfence_vma()`。
- `scheduler() `应该在没有进程运行时使用 `kernel_pagetable`。
- 在` freeproc `中释放进程的内核页表。
- 您将需要一种方法来释放页表而不同时释放叶物理内存页。
- `vmprint `在调试页表时可能会派上用场。
- 修改xv6的功能或者增加新的功能都可以；您可能至少需要在` kernel/vm.c` 和 `kernel/proc.c` 中执行此操作。 （但是，不要修改 `kernel/vmcopyin.c`、`kernel/stats.c`、`user/usertests.c` 和 `user/stats.c`。）
- 缺少页表映射可能会导致内核遇到页面错误。它将打印一个错误，其中包括 `sepc=0x00000000XXXXXXXX`。可以通过在`kernel/kernel.asm` 中搜索`XXXXXXXX `来找出故障发生的位置。













