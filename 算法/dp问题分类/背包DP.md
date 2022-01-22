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

