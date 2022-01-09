# 线性DP

[10. 正则表达式匹配 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/regular-expression-matching/):star::star::star::star::star:

```cpp
class Solution {
public:
    bool isMatch(string ss, string pp) {
        int n = ss.size(), m = pp.size();
        string s = " " + ss;
        string p = " " + pp;
        // dp状态表示：dp[i][j] s以i结尾的子串与p以j结尾的子串是否匹配
        vector<vector<bool>> dp(n + 1, vector<bool>(m + 1)); 
        dp[0][0] = true;
        for (int i = 0; i <= n; i ++)
            for (int j = 1; j <= m; j ++) {
                if (j + 1 <= m && p[j + 1] == '*') continue; // 下一个是*，当前 字符不能单独使用，跳过

                if (i - 1 >= 0 && p[j] != '*') {
                    dp[i][j] = dp[i - 1][j - 1] && (s[i] == p[j] || p[j] == '.'); // 对应了p[j]为普通字符和'.'两种情况
                }

                else if (p[j] == '*') {
                    // 字符为'*'
                    dp[i][j] = (j - 2 >= 0 && dp[i][j - 2]) || (i - 1 >= 0 && dp[i - 1][j] && (s[i] == p[j - 1] || p[j - 1] == '.'));
                }
            }
        return dp[n][m];
    }
};
```

[44. 通配符匹配 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/wildcard-matching/):star::star::star::star:

```cpp
class Solution {
public:
    bool isMatch(string ss, string pp) {
        int n = ss.size();
        int m = pp.size();
        string s = " " + ss;
        string p = " " + pp;
        vector<vector<bool>> dp(n + 1, vector<bool>(m + 1));
        dp[0][0] = true;
        for (int i = 0; i <= n; i ++)
            for (int j = 1; j <= m; j ++) {
                if (p[j] != '*') {
                    dp[i][j] = i - 1 >= 0 && dp[i - 1][j - 1] && (p[j] == s[i] || p[j] == '?');
                } else {
                    dp[i][j] = dp[i][j - 1] || (i - 1 >= 0 && dp[i - 1][j]);
                }
            }
        return dp[n][m];
    }
};
```

[45. 跳跃游戏 II - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/jump-game-ii/):star::star::star:

```cpp
class Solution {
public:
    int jump(vector<int>& nums) {
        int n = nums.size();
        vector<int> dp(n);
        for (int i = 1, j = 0; i < n; i ++) {
            while (j + nums[j] < i) j ++;
            dp[i] = dp[j] + 1;
        }
        return dp[n - 1];
    }
};
```

[91. 解码方法 - 力扣（LeetCode） (leetcode-cn.com):star::star:](https://leetcode-cn.com/problems/decode-ways/):star:

```cpp
class Solution {
public:
    int numDecodings(string ss) {
        int n = ss.size();
        string s = " " + ss;
        vector<int> dp(n + 1, 0);
        dp[0] = 1;
        for (int i = 1; i <= n; i ++) {
            int a = s[i] - '0', b = (s[i - 1] - '0') * 10 + (s[i] - '0');
            if (a >= 1 && a <= 9) dp[i] = dp[i - 1];
            if (b >= 10 && b <= 26) dp[i] += dp[i - 2];
        }
        return dp[n];
    }
};
```

#### [115. 不同的子序列](https://leetcode-cn.com/problems/distinct-subsequences/)

```cpp
class Solution {
public:
    int numDistinct(string ss, string tt) {
        int n = ss.size(), m = tt.size();
        string s = " " + ss;
        string t = " " + tt;
        vector<vector<int>> dp(n + 1, vector<int>(m + 1));
        // f[i][j] 表示 0~i，0~j的字符串匹配个数
        for (int i = 0; i <= n; i ++) dp[i][0] = 1;
        for (int i = 1; i <= n; i ++)
            for (int j = 1; j <= m; j ++) {
                // 不匹配s[i]
                dp[i][j] = dp[i - 1][j];
                if (s[i] == t[j]) { // 匹配s[i]
                    dp[i][j] = (0LL + dp[i][j] + dp[i - 1][j - 1]) % INT_MAX;
                }
            }
        return dp[n][m];
    }
};
```

[119. 杨辉三角 II - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/pascals-triangle-ii/)

```cpp
class Solution {
public:
    vector<int> getRow(int rowIndex) {
        vector<int> dp(rowIndex + 1);
        dp[0] = 1;
        for (int i = 1; i <= rowIndex; i ++)
            for (int j = i; j >= 0; j --) {
                if (j - 1 >= 0) dp[j] += dp[j - 1];
                if (dp[j] == 0) dp[j] = 1;
            }
        vector<int> res;
        for (int i = 0; i < rowIndex + 1; i ++) res.push_back(dp[i]);
        return res;
    }
};
```

[213. 打家劫舍 II - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/house-robber-ii/)

```cpp
class Solution {
public:
    int rob(vector<int>& nums) {
        int n = nums.size();
        if (n == 1) return nums[0];
        vector<vector<int>> dp(n, vector<int>(2, 0));
        
        // 不选第1间, 0不选，1选
        for (int i = 1; i < n; i ++) {
            dp[i][1] = dp[i - 1][0] + nums[i];
            dp[i][0] = max(dp[i - 1][0], dp[i - 1][1]);
        }
        int res1 = max(dp[n - 1][0], dp[n - 1][1]);

        dp[0][1] = nums[0], dp[0][0] = 0;
        for (int i = 1; i < n - 1; i ++) {
            dp[i][1] = dp[i - 1][0] + nums[i];
            dp[i][0] = max(dp[i - 1][0], dp[i - 1][1]);    
        }
        int res2 = max(dp[n - 2][0], dp[n - 2][1]);
        return max(res1, res2);
    }
};
```

[403. 青蛙过河 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/frog-jump/)

```cpp
class Solution {
public:
    bool canCross(vector<int>& stones) {
        int n = stones.size();
        if (stones[1] != 1) return false;
        // dp[i][j]表示跳到位置i步长为j是否可以
        vector<vector<int>> dp(n + 1, vector<int>(n + 1, 0));
        dp[1][1] = true;
        for (int i = 2; i < n; i ++)
            for (int j = 1; j < i; j ++) {
                int k = stones[i] - stones[j]; // 步长
                if(k <= j + 1) {
                    dp[i][k] = dp[j][k - 1] || dp[j][k] || dp[j][k + 1];
                }
            }
        for (int i = 1; i < n; i ++) 
            if (dp[n - 1][i]) return true;
        return false;
    }
};
```

