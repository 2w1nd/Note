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

[576. 出界的路径数 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/out-of-boundary-paths/)

```cpp
class Solution {
public:

    const int MOD = (int) 1e9 + 7;
    int n, m, maxn;

    int dx[4] = {-1, 0, 1, 0}, dy[4] = {0, 1, 0, -1};

    int findPaths(int _m, int _n, int _maxMove, int r, int c) {
        n = _m, m = _n, maxn = _maxMove;
        vector<vector<int>> dp(n * m, vector<int>(maxn + 1, 0));
        // 初始化边缘
        for (int i = 0; i < n; i ++)
            for (int j = 0; j < m; j ++) {
                if (i == 0) add(i, j, dp);
                if (j == 0) add(i, j, dp);
                if (i == n - 1) add(i, j, dp);
                if (j == m - 1) add(i, j, dp);
            }

        // f[(x,y)][step]=f[(x−1,y)][step−1]+f[(x+1,y)][step−1]+f[(x,y−1)][step−1]+f[(x,y+1)][step−1]
        for (int k = 1; k <= maxn; k ++) {
            for (int idx = 0; idx < m * n; idx ++) {
                vector<int> info(2, 0);
                info = parseIdx(idx);
                int x = info[0], y = info[1];
                for (int i = 0; i < 4; i ++) {
                    int nx = x + dx[i], ny = y + dy[i];
                    if (nx < 0 || nx >= n || ny < 0 || ny >= m) continue;
                    int nidx = getIdx(nx, ny);
                    dp[idx][k] += dp[nidx][k - 1];
                    dp[idx][k] %= MOD;
                }
            }
        }
           
        return dp[getIdx(r, c)][maxn];
    }

    void add(int x, int y, vector<vector<int>> &dp) {
        for (int k = 1; k <= maxn; k ++) {
            dp[getIdx(x, y)][k] ++;
        }
    }

    int getIdx(int x, int y) {
        return x * m + y;
    }

    vector<int> parseIdx(int idx) {
        return vector<int>{idx / m, idx % m};
    }
};
```

[639. 解码方法 II - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/decode-ways-ii/submissions/)

```cpp
class Solution {
public:

    const int MOD = (int) 1e9 + 7;

    // i 表示当前遍历字符下标，j表示前一个字符下标
    // f[i] 表示 以 s[i] 结尾的字符串，共有多少种解码方法
    /*
        s[i] == '*'：
            s[i]可以单独组成一个，f[i] += f[i - 1] * 9
            可以和前面的字符组成：
                若s[j] == 1，那么 f[i] += f[i - 2] * 9 (11 - 19)
                若s[j] == 2，那么 f[i] += f[i - 2] * 6 (21 - 26)
                若s[j] == *，那么 f[i] += f[i - 2] * 15
        s[i] != '*'，s[i]为数字时：
            s[j] 为 '*'：
                s[i] == 0，f[i] += f[i - 2] * 2;  （10，20）
                s[i] != 0：
                    f[i] = f[i - 1] (1 - 9)
                    1 <= s[i] <= 6  f[i] += f[i - 2] * 2    （11 - 16，21 - 26）
                    7 <= s[i] <= 9  f[i] += f[i - 2] * 1    （17 - 19）
            s[j] != '*'：
                s[i] == 0，f[i] = f[i - 2] (10, 20)
                s[i] != 0： 
                    f[i] = f[i - 1] (1 - 9)
                    s[j] == 1，f[i] = f[i - 2]; (11 - 19)
                    s[j] == 2 且 1 <= s[i] <= 6 ，f[i] = f[i - 2]; (21 - 26)

    */

    int numDecodings(string s) {
        int n = s.size();
        vector<long> f(n, 0); // 注意这里要long
        f[0] = s[0] == '*' ? 9 : (s[0] != '0' ? 1 : 0);
        for (int i = 1; i < n; i ++) {
            char c = s[i], prev = s[i - 1];
            if (c == '*') {
                f[i] += f[i - 1] * 9;
                if (prev == '*') f[i] += (i - 2 >= 0 ? f[i - 2] : 1) * 15;
                else {
                    int u = prev - '0';
                    if (u == 1) f[i] += (i - 2 >= 0 ? f[i - 2] : 1) * 9;
                    else if (u == 2) f[i] += (i - 2 >= 0 ? f[i - 2] : 1) * 6;
                }
            } else {
                int t = c - '0';
                if (prev == '*') {
                    if (t == 0) f[i] += (i - 2 >= 0 ? f[i - 2] : 1) * 2;
                    else {
                        f[i] += f[i - 1];
                        if (t >= 1 && t <= 6) f[i] += (i - 2 >= 0 ? f[i - 2] : 1) * 2;
                        if (t >= 7 && t <= 9) f[i] += (i - 2 >= 0 ? f[i - 2] : 1);
                    } 
                } else {
                    int u = prev - '0';
                    if (t == 0) {
                        if (u == 1 || u == 2) {
                            f[i] += (i - 2 >= 0 ? f[i - 2] : 1);
                        }
                    } else {
                        f[i] = f[i - 1];
                        if (u == 1) f[i] += (i - 2 >= 0 ? f[i - 2] : 1);
                        if (u == 2 && t <= 6) f[i] += (i - 2 >= 0 ? f[i - 2] : 1);
                    }
                }
            }
            f[i] %= MOD;
        }
        return (int) f[n - 1];
    }
};
```

[650. 只有两个键的键盘 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/2-keys-keyboard/)

```cpp
// f[i][j]表示经过最后一次操作，记事本上有i个字符，粘贴板上有j个字符的最小操作次数
class Solution {
public:
    const int MAX_INT = 0x3f3f3f3f;
    int minSteps(int n) {
        vector<vector<int>> dp(n + 1, vector<int>(n + 1, MAX_INT));
        dp[1][0] = 0, dp[1][1] = 1;
        for (int i = 2; i <= n; i ++) {
            int minN = MAX_INT;
            for (int j = 0; j <= i / 2; j ++) {
                dp[i][j] = dp[i - j][j] + 1; // paste
                minN = min(minN, dp[i][j]); 
            }
            dp[i][i] = minN + 1; // copy all
        }
        int res = MAX_INT;
        for (int i = 0; i <= n; i ++) res = min(res, dp[n][i]);
        return res;
    }
};
```

[678. 有效的括号字符串 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/valid-parenthesis-string/submissions/)

```cpp
class Solution {
public:
    bool checkValidString(string s) {
        int n = s.size();
        vector<vector<bool>> dp(n + 1, vector<bool>(n + 1, 0));
        dp[0][0] = true;
        for (int i = 1; i <= n; i ++) {
            char c = s[i - 1];
            for (int j = 0; j <= i; j ++) {
                if (c == '(') {
                    if (j - 1 >= 0) dp[i][j] = dp[i - 1][j - 1];
                } else if (c == ')') {  
                    if (j + 1 <= i) dp[i][j] = dp[i - 1][j + 1];
                } else {
                    dp[i][j] = dp[i - 1][j];
                    if (j - 1 >= 0) dp[i][j] = dp[i][j] | dp[i - 1][j - 1];
                    if (j + 1 <= i) dp[i][j] = dp[i][j] | dp[i - 1][j + 1];
                }
            }
        }
        return dp[n][0];
    }
};
```

[1220. 统计元音字母序列的数目 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/count-vowels-permutation/)

```cpp
class Solution {
public:
    const int MOD = 1e9 + 7;
    // dp[i][j] 表示 长度为i的字符串，结尾为j的字符串 的 个数
    int countVowelPermutation(int n) {
        vector<vector<long>> dp(n, vector<long>(5, 0));
        for (int i = 0; i < 5; i ++) dp[0][i] = 1;
        for (int i = 1; i < n; i ++) {
            // 每个元音 'a' 后面都只能跟着 'e'
            dp[i][1] += dp[i - 1][0];
            // 每个元音 'e' 后面只能跟着 'a' 或者是 'i'
            dp[i][0] += dp[i - 1][1];
            dp[i][2] += dp[i - 1][1];
            // 每个元音 'i' 后面 不能 再跟着另一个 'i'
            dp[i][0] += dp[i - 1][2];
            dp[i][1] += dp[i - 1][2];
            dp[i][3] += dp[i - 1][2];
            dp[i][4] += dp[i - 1][2];
            // 每个元音 'o' 后面只能跟着 'i' 或者是 'u'
            dp[i][2] += dp[i - 1][3];
            dp[i][4] += dp[i - 1][3];
             // 每个元音 'u' 后面只能跟着 'a'
            dp[i][0] += dp[i - 1][4];

            for (int j = 0; j < 5; j ++) dp[i][j] %= MOD;
        }
        long res = 0;
        for (int i = 0; i < 5; i ++) res += dp[n - 1][i];
        return (int) (res % MOD);
    }
};
```

[1751. 最多可以参加的会议数目 II - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/maximum-number-of-events-that-can-be-attended-ii/submissions/)

```cpp
class Solution {
public:

    static bool cmp(vector<int> a, vector<int> b) { // 升序排序
        return a[1] <= b[1];
    }

    int maxValue(vector<vector<int>>& events, int k) {
        int n = events.size();
        vector<vector<int>> dp(n + 1, vector<int>(k + 1, 0));
        sort(events.begin(), events.end(), cmp);

        for (int i = 0; i < n; i ++)
            printf("[%d %d %d]", events[i][0], events[i][1], events[i][2]);

        for (int i = 1; i <= n; i ++) {
            auto p = events[i - 1];
            int s = p[0], e = p[1], v = p[2];

            int last = 0;
            for (int t = i - 1; t >= 1; t --) {
                auto l = events[t - 1];
                if (s > l[1]) {
                    last = t; // 更新为不冲突的事件下标
                    break;
                }
            }

            for (int j = 1; j <= k; j ++) {
                dp[i][j] = max(dp[i - 1][j], dp[last][j - 1] + v); // 选与不选
            }
        }
        return dp[n][k];
    }
};
```

[1787. 使所有区间的异或结果为零 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/make-the-xor-of-all-segments-equal-to-zero/)

```cpp
class Solution {
public:
    /*
        易知，将nums排列为一个二维数组（每行为k个），问题将转化为：
            使得每列相等，且最终首行异或值为0的更改元素数
        定义 f[i][xor]为考虑前i列，且首行前i列异或值的xor的更改元素数
        由于需要知道i - 1列的最小更改元素数，使之转态转移，所以需要一个状态数组prev
        另外，使用
        map记录每一列数字对应的个数
        cnt记录每一列数字总数
        所以，分析状态转移方程
        当在第0列时：
            f[0][xor] = f[0][xor] + cnt - map[xor]
        当在其他列时，需要考虑从前一列转移的状态：
            另外，还有
                考虑整列修改：
                    f[i][xor] = f[i - 1][xor] + cnt;
                考虑部分修改：
                    f[i][xor] = f[i - 1][xor ^ cur] + cnt - map[cur];
    */  
    const int maxVal = INT_MAX / 2;
    const int maxHalf = 1 << 10;
    int minChanges(vector<int>& nums, int k) {
        int n = nums.size();
        vector<vector<int>> dp(k, vector<int>(maxHalf, maxVal));
        vector<int> prev(k, maxVal);
        for (int i = 0; i < k; i ++) { // 遍历每一列
            int cnt = 0;
            unordered_map<int, int> map;
            for (int j = i; j < n; j += k) {
                map[nums[j]] ++;
                cnt ++;
            }

            if (i == 0) { // 第0列
                for (int x = 0; x < maxHalf; x ++) {
                    dp[0][x] = min(dp[0][x], cnt - map[x]);
                    prev[0] = min(prev[0], dp[0][x]);
                }
            } else { // 其他列
                for (int x = 0; x < maxHalf; x ++) {
                    dp[i][x] = prev[i - 1] + cnt;
                    for (auto &it: map) {
                        dp[i][x] = min(dp[i][x], dp[i - 1][x ^ it.first] + cnt - it.second);
                    }
                    prev[i] = min(prev[i], dp[i][x]);
                }
            }   
        }   
        return dp[k - 1][0];
    }
};
```

[剑指 Offer 42. 连续子数组的最大和 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/lian-xu-zi-shu-zu-de-zui-da-he-lcof/)

```cpp
class Solution {
public:
    int maxSubArray(vector<int>& nums) {
        int n = nums.size();
        vector<int> dp(n, 0);
        dp[0] = nums[0];
        int ans = dp[0];
        for (int i = 1; i < n; i ++) {
            dp[i] = max(nums[i], dp[i - 1] + nums[i]);
            ans = max(ans, dp[i]);
        }
        return ans;
    }
};
```

[LCP 07. 传递信息 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/chuan-di-xin-xi/)

```cpp
class Solution {
public:
    int numWays(int n, vector<vector<int>>& relation, int k) {
        vector<vector<int>> dp(k + 1, vector<int>(15, 0));
        dp[0][0] = 1;
        for (int i = 1; i <= k; i ++) 
            for(auto &r: relation) {
                int a = r[0], b = r[1];
                dp[i][b] += dp[i - 1][a];
            }
        return dp[k][n - 1];
    }
};
```

