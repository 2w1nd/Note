# datalab
## 1. 异或
 ```c
//1
/* 
 * bitXor - x^y using only ~ and & 
 *   Example: bitXor(4, 5) = 1
 *   Legal ops: ~ &
 *   Max ops: 14
 *   Rating: 1
 */
int bitXor(int x, int y) {
  
  return ~(~x&~y)&~(x&y);
}
```
**1. 先通过画真值表发现规律**
![](image/2021-10-29-12-53-55.png)
**2. 使用DeMorgan（狄摩根定律）进行化简**
对结果进行两次非运算，先取出一次非进行进行计算
![](image/2021-10-29-12-58-01.png)
**3. 将结果返回到原始表达式中，继续使用DeMorigan定律**
![](image/2021-10-29-12-59-24.png)
## 2.求整型补码所表示的最小值
```c
/* 
 * tmin - return minimum two's complement integer 
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 4
 *   Rating: 1
 */
int tmin(void) {
  return 0x1 << 31;
}
```
![](image/2021-10-29-13-46-07.png)
## 3.判断x是不是补码表示的最大值


