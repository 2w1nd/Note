# 背包DP

[279. 完全平方数 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/perfect-squares/)

```cpp
class Solution {
public:
    int numSquares(int n) {
        // dp[i] 表示 能凑出i的完全平方数的最小数量
        vector<int> dp(n + 1);
        for (int i = 1; i <= n; i ++) {
            dp[i] = i;
            for (int j = 1; i - j * j >= 0; j ++)
                dp[i] = min(dp[i], dp[i - j * j] + 1);
        }
        return dp[n];
    }
};
```

[322. 零钱兑换 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/coin-change/submissions/) 

```cpp
class Solution {
public:
    int coinChange(vector<int>& coins, int amount) {
        int n = coins.size();
        vector<int> dp(amount + 1, 1e9);
        dp[0] = 0;
        for (int i = 0; i < n; i ++)
            for (int j = coins[i]; j <= amount; j ++)
                dp[j] = min(dp[j], dp[j - coins[i]] + 1);
        if (dp[amount] == 1e9) return -1;
        return dp[amount];
    }
};
```

[416. 分割等和子集 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/partition-equal-subset-sum/)

```cpp
class Solution {
public:
    bool canPartition(vector<int>& nums) {
        int sum = 0;
        for (auto &x: nums) sum += x;
        int target = sum / 2;
        if (target * 2 != sum) return false;
        vector<int> dp(target + 1, 0);
        // dp[i] 表示 凑出 i 所需的价值数
        for (auto &x: nums)
            for (int j = target; j >= x; j --) {
                dp[j] = max(dp[j], dp[j - x] + x);
            }
        return dp[target] == target;
    }
};
```

[474. 一和零 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/ones-and-zeroes/)

```cpp
class Solution {
public:
    int findMaxForm(vector<string>& strs, int m, int n) {
        int len = strs.size();
        vector<vector<int>> cnt(len, vector<int>(2, 0));
        for (int i = 0; i < len; i ++) {
            int zero = 0, one = 0;
            for (auto &c: strs[i]) {
                if (c == '1') one ++;
                else zero ++;
            }
            cnt[i][0] = zero, cnt[i][1] = one;
        }

        vector<vector<int>> f(m + 1, vector<int>(n + 1, 0));
        for (int k = 0; k < len; k ++) {
            int zero = cnt[k][0], one = cnt[k][1];
            for (int i = m; i >= zero; i --)
                for (int j = n; j >= one; j --) {
                    f[i][j] = max(f[i][j], f[i - zero][j - one] + 1);
                }
        }
        return f[m][n];
    }
};
```

[494. 目标和 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/target-sum/)

```cpp
class Solution {
public:

    const int Offset = 1000;

    int findTargetSumWays(vector<int>& a, int t) {
        int n = a.size();
        int s = 0;
        for (int i: a) s += abs(i);
        if (abs(t) > s) return 0;
        vector<vector<int>> dp(n + 1, vector<int>(2 * s + 1, 0));
        dp[0][s] = 1;
        for (int i = 1; i <= n; i ++)
            for (int j = -s; j <= s; j ++) {
                if (j - a[i - 1] >= -s) dp[i][j + s] += dp[i - 1][j - a[i - 1] + s];
                if (j + a[i - 1] <= s) dp[i][j + s] += dp[i - 1][j + a[i - 1] + s];
            }
        return dp[n][t + s];
    }
};
```

[518. 零钱兑换 II - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/coin-change-2/)

```cpp
class Solution {
public:
    int change(int amount, vector<int>& coins) {
        // dp[i][j] 表示 考虑前i个物品，凑出 j 的方案数
        int n = coins.size();
        vector<int> dp(amount + 1, 0);
        dp[0] = 1;
        for (int i = 0; i < n; i ++)
            for (int j = coins[i]; j <= amount; j ++) {
                dp[j] += dp[j - coins[i]];
            }
        return dp[amount];
    }
};
```

[879. 盈利计划 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/profitable-schemes/)

```cpp
class Solution {
public:
    const int MOD = 1e9 + 7; 
    // dp[i][j][k] 表示 前i个个物品，人数不超过j，利润至少为k的方案数
    int profitableSchemes(int n, int minProfit, vector<int>& group, vector<int>& profit) {
        int m = group.size();
        vector<vector<int>> dp(n + 1, vector<int>(minProfit + 1, 0));
        for (int i = 0; i <= n; i ++) dp[i][0] = 1;
        for (int i = 1; i <= m; i ++) {
            int a = group[i - 1], b = profit[i - 1];
            for (int j = n; j >= a; j --)
                for (int k = minProfit; k >= 0; k --) {
                    dp[j][k] += dp[j - a][max(k - b, 0)];
                    dp[j][k] %= MOD;
                }
        }
        return dp[n][minProfit];
    }
};
```

[1049. 最后一块石头的重量 II - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/last-stone-weight-ii/)

**未优化空间**

问题转化为：把一堆石头分成两堆,求两堆石头重量差最小值
进一步分析：要让差值小,两堆石头的重量都要接近sum/2;我们假设两堆分别为A,B,A<sum/2,B>sum/2,若A更接近sum/2,B也相应更接近sum/2
进一步转化：将一堆stone放进最大容量为sum/2的背包,求放进去的石头的最大重量MaxWeight,最终答案即为sum-2*MaxWeight;、

```cpp
// d[i][j]代表考虑前 i 个物品（数值），凑成总和不超过 j 的最大价值。
class Solution {
public:
    int lastStoneWeightII(vector<int>& stones) {
        int sum = 0, n = stones.size();
        for (auto x: stones) sum += x;
        int t = sum / 2;
        vector<vector<int>> dp(n + 1, vector<int>(t + 1, 0));
        for (int i = 1; i <= n; i ++) {
            int x = stones[i - 1];
            for (int j = 0; j <= t; j ++) {
                dp[i][j] = dp[i - 1][j];
                if (j >= x) dp[i][j] = max(dp[i][j], dp[i - 1][j - x] + x);
            }
        }
        return sum - 2 * dp[n][t];
    }
};
```

**优化空间**

```cpp
class Solution {
public:
    int lastStoneWeightII(vector<int>& stones) {
        int sum = 0, n = stones.size();
        for (auto x: stones) sum += x;
        int t = sum / 2;
        vector<int> dp(t + 1, 0);
        for (int i = 1; i <= n; i ++) {
            int x = stones[i - 1];
            for (int j = t; j >= x; j --) {
                dp[j] = max(dp[j], dp[j - x] + x);
            }
        }
        return sum - 2 * dp[t];
    }
};
```

[1155. 掷骰子的N种方法 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/number-of-dice-rolls-with-target-sum/submissions/)

**未优化空间**

```cpp
class Solution {
public:
    const int MOD = 1e9 + 7;
    int numRollsToTarget(int n, int m, int t) {
        vector<vector<int>> f(n  + 1, vector<int>(t + 1, 0));
        f[0][0] = 1;
        for (int i = 1; i <= n; i ++)
            for (int j = 0; j <= t; j ++) 
                for (int k = 1; k <= m; k ++) {
                    if(j >= k) f[i][j] = (f[i][j] + f[i - 1][j - k]) % MOD;
                }
        return f[n][t];
    }
};
```

**优化空间**

```cpp
class Solution {
public:
    const int MOD = 1e9 + 7;
    int numRollsToTarget(int n, int m, int t) {
        vector<int> f(t + 1, 0);
        f[0] = 1;
        for (int i = 1; i <= n; i ++)
            for (int j = t; j >= 0; j --) {
                f[j] = 0; //由于我们直接是在 f[i][j]f[i][j] 格子的基础上进行方案数累加，因此在计算 f[i][j]f[i][j] 记得手动置零。
                for (int k = 1; k <= m; k ++) {
                    if(j >= k) f[j] = (f[j] + f[j - k]) % MOD;
                }
            }
        return f[t];
    }
};
```

[1449. 数位成本和为目标值的最大数字 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/form-largest-integer-with-digits-that-add-up-to-target/)

```cpp
class Solution {
public:
    // dp[i][j] 表示 考虑前i个物品，总成本为j的最大整数长度
    string largestNumber(vector<int>& cost, int t) {
        int n = cost.size();
        vector<int> dp(t + 1, INT_MIN);
        dp[0] = 0;
        for (int i = 1; i <= n; i ++) {
            int x = cost[i - 1];
            for (int j = x; j <= t; j ++) {
                dp[j] = max(dp[j], dp[j - x] + 1);
            }
        }

        if (dp[t] < 0) return "0";
        string res = "";
        for (int i = 9, j = t; i >= 1; i --) {
            int x = cost[i - 1];
            while (j >= x && dp[j] == dp[j - x] + 1) {
                res += to_string(i);
                j -= x;
            }
        }
        return res;
    }
};
```

[1995. 统计特殊四元组 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/count-special-quadruplets/)

```cpp
class Solution {
public:
    int countQuadruplets(vector<int>& nums) {
        // dp[i][j][k] 考虑前i个数，恰好凑出j，使用个数为k的方案数
        int n = nums.size();
        vector<vector<vector<int>>> f(n + 1, vector<vector<int>>(110, vector<int>(4, 0)));
        f[0][0][0] = 1;
        for (int i = 1; i <= n; i ++) {
            int x = nums[i - 1];
            for (int j = 0; j < 110; j ++) 
                for (int k = 0; k < 4; k ++) {
                    f[i][j][k] += f[i - 1][j][k];
                    if (j >= x && k >= 1) f[i][j][k] += f[i - 1][j - x][k - 1];
                }
        }

        int res = 0;
        for (int i = 3; i < n; i ++) res += f[i][nums[i]][3];
        return res;
    }
};
```

