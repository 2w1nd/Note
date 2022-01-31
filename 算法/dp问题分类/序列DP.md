# 序列DP

### [334. 递增的三元子序列 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/increasing-triplet-subsequence/)

```cpp
class Solution {
public:
    bool increasingTriplet(vector<int>& nums) {
        int small = INT_MAX, big = INT_MAX;
        for (int x: nums) {
            if (x <= small) {
                small = x;
            } else if (x <= big) {
                big = x;
            } else {
                return true;
            }
        }
        return false;
    }
};
```

```cpp
class Solution {
public:
    bool increasingTriplet(vector<int>& nums) {
        int n = nums.size();
        vector<int> f(n + 1, INT_MAX); // f[len] = x 表示 以 长度为 len 的序列的最小尾元素为x
        int ans = 1;
        for (int i = 0; i < n; i ++) {
            int l = 1, r = i + 1;
            int x = nums[i];
            while (l < r) { // 二分查找小于nums[i]的最小元素
                int mid = l + r >> 1;
                if (f[mid] >= x) r = mid;
                else l = mid + 1; 
            }
            f[r] = x;
            ans = max(ans, r);
        }
        return ans >= 3;
    }
};
```

### [354. 俄罗斯套娃信封问题 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/russian-doll-envelopes/)

```cpp
class Solution {
public:
    int maxEnvelopes(vector<vector<int>>& envelopes) {
        int n = envelopes.size();
        vector<int> f(n, 0); // 以 i 结尾的最大信封
        sort(envelopes.begin(), envelopes.end());
        int res = 0;
        for (int i = 0; i < n; i ++) {
            f[i] = 1;
            for (int j = 0; j < i; j ++) {
                if (envelopes[i][0] > envelopes[j][0] && envelopes[i][1] > envelopes[j][1]) 
                    f[i] = max(f[i], f[j] + 1);
            }
            res = max(res, f[i]);
        }
        return res;
    }
};
```

### [368. 最大整除子集 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/largest-divisible-subset/)

```cpp
class Solution {
public:
    vector<int> largestDivisibleSubset(vector<int>& nums) {
        int n = nums.size();
        vector<int> f(n, 0); // f[i] 表示 前i个数中以a[i]结尾的整除子集的最长序列的元素个数
        sort(nums.begin(), nums.end());
        int k = 0; // 整除序列最后一个元素下标
        for (int i = 0; i < n; i ++) {
            f[i] = 1;
            for (int j = 0; j < i; j ++) 
                if (nums[i] % nums[j] == 0) 
                    f[i] = max(f[i], f[j] + 1);
            if (f[k] < f[i]) k = i;
        }

        vector<int> res; // 逆推得出答案
        while (true) {
            res.push_back(nums[k]);
            if (f[k] == 1) break; // 只剩一个元素
            for (int i = 0; i < k; i ++) {
                if ((nums[k] % nums[i] == 0) && f[k] == f[i] + 1) {
                    k = i;
                    break;
                }
            }
        }
        return res;
    }
};
```

### [390. 消除游戏 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/elimination-game/)

![image-20220126114611818](image/image-20220126114611818.png)

```cpp
class Solution {
public:
    int lastRemaining(int n) {
        if (n == 1) return 1;
        return 2 * (n / 2 + 1 - lastRemaining(n / 2));
    }
};
```

### [446. 等差数列划分 II - 子序列 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/arithmetic-slices-ii-subsequence/submissions/)

[LeetCode 446. 等差数列划分 II - 子序列 - AcWing](https://www.acwing.com/solution/content/61868/)

```cpp
class Solution {
public:
    int numberOfArithmeticSlices(vector<int>& a) {
        typedef long long LL;
        int n = a.size();
        // 由于每个数可能都会在不同等差数列中，需要用哈希表来存
        vector<unordered_map<LL, int>> f(n); // f[i][j] 表示 考虑以第 i 个数结尾 公差为j的等差数列的个数
        int res = 0;
        for (int i = 0; i < n; i ++)
            for (int k = 0; k < i; k ++) {
                LL j = (LL)a[i] - a[k];
                auto it = f[k].find(j); // 查找a[k]结尾公差为j的等差数列的个数
                int t = 0;
                if (it != f[k].end()) {
                    t = it->second;
                    res += t;
                }
                f[i][j] += t + 1;
            }
        return res;
    }
};
```

### [472. 连接词 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/concatenated-words/)

```cpp
class Solution {
public:

    typedef unsigned long long ULL;
    unordered_set<ULL> hash;
    const int P = 131;

    vector<string> findAllConcatenatedWordsInADict(vector<string>& words) {
        // 初始化字符串哈希表
        for (auto &s: words) {
            ULL t = 0;
            for (auto &c: s) {
                t = t * P + c;
            }
            hash.insert(t);
        }

        vector<string> res;
        for (auto &s: words) 
            if (check(s)) res.push_back(s);
        return res;
    }

    bool check(string str) {
        int n = str.size();
        vector<int> f(n + 1, -1); // f[i] 表示 在i前面的连接词的个数
        f[0] = 0;

        for (int i = 0; i <= n; i ++) {
            if (f[i]== -1) continue;
            ULL cur = 0;
            for (int j = i + 1; j <= n; j ++) {
                cur = cur * P + str[j - 1];
                if (hash.count(cur)) {
                    f[j] = max(f[j], f[i] + 1);
                }
            }
            if (f[n] >= 2) return true;
        }
        return false;
    }
};
```

### [583. 两个字符串的删除操作 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/delete-operation-for-two-strings/submissions/)

```cpp
class Solution {
public:
    int minDistance(string word1, string word2) {
        int n = word1.size(), m = word2.size();
        vector<vector<int>> f(n + 1, vector<int>(m + 1, 0));
        for (int i = 0; i <= n; i ++) f[i][0] = 1; 
        for (int j = 0; j <= m; j ++) f[0][j] = 1;
        for (int i = 1; i <= n; i ++)
            for (int j = 1; j <= m; j ++) {
                f[i][j] = max(f[i][j - 1], f[i - 1][j]);
                if (word1[i - 1] == word2[j - 1]) {
                    f[i][j] = max(f[i][j], f[i - 1][j - 1] + 1);
                }
            }
        int maxn = f[n][m] - 1;
        return n + m - 2 * maxn;
    }
};
```


### [629. K个逆序对数组 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/k-inverse-pairs-array/)

[LeetCode 629. K个逆序对数组 - AcWing](https://www.acwing.com/solution/content/18355/)

```cpp
class Solution {
public:
    const int MOD = 1e9 + 7;
    int kInversePairs(int n, int k) {
        // f[i][j] 表示 所有由数字 1 ~ i 组成的含有 j 个 逆序对的数组个数
        vector<vector<int>> f(n + 1, vector<int>(k + 1, 0));
        f[1][0] = 1;
        for (int i = 2; i <= n; i ++) {
            long long s = 0;
            for (int j = 0; j <= k; j ++) {
                s += f[i - 1][j];
                if (j >= i) s -= f[i - 1][j - i];
                f[i][j] = s % MOD;
            }
        } 
        return (f[n][k] + MOD) % MOD;
    } 
};
```

### [673. 最长递增子序列的个数 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/number-of-longest-increasing-subsequence/)

```cpp
class Solution {
public:
    int findNumberOfLIS(vector<int>& nums) {
        int n = nums.size();
        vector<int> f(n), g(n); // f 表示 以i结尾的最长上升子序列长度，g 表示 以 i 结尾的最长上升子序列的个数
        
        int maxl = 0, cnt = 0; // maxl是最长子序列长度，cnt是子序列个数
        for (int i = 0; i < n; i ++) {
            f[i] = g[i] = 1; 
            for (int j = 0; j < i; j ++) {
                if (nums[j] < nums[i]) {
                    if (f[i] < f[j] + 1) f[i] = f[j] + 1, g[i] = g[j];
                    else if (f[i] == f[j] + 1) g[i] += g[j];
                } 
            }
            if (maxl < f[i]) maxl = f[i], cnt = g[i];
            else if (maxl == f[i]) cnt += g[i];
        }
        return cnt;
    }
};
```

[Loading Question... - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/maximum-sum-of-3-non-overlapping-subarrays/)



### [740. 删除并获得点数 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/delete-and-earn/)

```cpp
class Solution {
public:
    const int N = 1e4 + 10;
    int deleteAndEarn(vector<int>& nums) {
        // f[i][j] 表示是否删除 j 数 的 最大点数
        int n = nums.size();
        int maxn = 0;
        vector<int> cnt(N);
        for(auto x: nums) {
            cnt[x] ++;
            maxn = max(maxn, x);
        }
        vector<vector<int>> f(maxn + 1, vector<int>(2, 0));
        for (int i = 1; i <= maxn; i ++) {
            f[i][0] = max(f[i - 1][0], f[i - 1][1]);
            f[i][1] = max(f[i][1], f[i - 1][0] + cnt[i] * i);
        }
        return max(f[maxn][0], f[maxn][1]);
    }
};
```

### [978. 最长湍流子数组 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/longest-turbulent-subarray/)

```cpp
// 优化了空间
class Solution {
public:
    int maxTurbulenceSize(vector<int>& arr) {
        int n = arr.size();
        // f[i][0/1]表示 以i结尾的数组 元素状态为j的最大湍流子数组长度
        vector<vector<int>> f(2, vector<int>(2, 0));
        
        int ans = 1;
        f[0][0] = f[0][1] = 1;
        for (int i = 1; i < n; i ++) {
            for (int j = 0; j < 2; j ++) f[i % 2][j] = 1;
            if (arr[i - 1] < arr[i]) f[i % 2][0] = f[(i - 1) % 2][1] + 1;
            if (arr[i - 1] > arr[i]) f[i % 2][1] = f[(i - 1) % 2][0] + 1;
            for (int j = 0; j < 2; j ++) ans = max(ans, f[i % 2][j]);
        }
        return ans;
    }
};
```

### [1035. 不相交的线 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/uncrossed-lines/submissions/)

```cpp
class Solution {
public:
    int maxUncrossedLines(vector<int>& nums1, vector<int>& nums2) {
        int n = nums1.size(), m = nums2.size();
        // f[i][j] 表示 考虑 前 i 个 数字，前 j 个数字组成最大公共子序列长度
        // 最长公共子序列 是考虑的情况是不一定包含第i个字符的
        // 例如，我们通常考虑f[i - 1][j]为前i - 1，j个数字的最长子序列，但这里不一定包含第j个，只是可能，所以 f[i - 1][j] 包含 f[i - 1][j - 1];
        vector<vector<int>> f(n + 1, vector<int>(m + 1, 0));
        for (int i = 1; i <= n; i ++)
            for (int j = 1; j <= m; j ++) {
                f[i][j] = max(f[i - 1][j], f[i][j - 1]);
                if (nums1[i - 1] == nums2[j - 1]) {
                    f[i][j] = max(f[i][j], f[i - 1][j - 1] + 1);
                }
            }
        return f[n][m];
    }
};
```

### [1143. 最长公共子序列 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/longest-common-subsequence/)

```cpp
class Solution {
public:
    int longestCommonSubsequence(string text1, string text2) {
        int n = text1.size(), m = text2.size();
        vector<vector<int>> f(n + 1, vector<int>(m + 1, 0));

        for (int i = 1; i <= n; i ++)
            for (int j = 1; j <= m; j ++) {
                f[i][j] = max(f[i - 1][j], f[i][j - 1]);
                if (text1[i - 1] == text2[j - 1]) {
                    f[i][j] = max(f[i][j], f[i - 1][j - 1] + 1);
                }
            }
        return f[n][m];
    }
};
```

### [1218. 最长定差子序列 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/longest-arithmetic-subsequence-of-given-difference/)

```cpp
class Solution {
public:
    // 注意覆盖问题：hash.insert遇到一样的键不会覆盖
    // 由于 arr长度 为 1e5，不能用两层循环，使用哈希表进行优化
    int longestSubsequence(vector<int>& arr, int diff) {
        int n = arr.size();
        // f[i][j] 表示 前i个数，第i个数选或不选的最长定差子序列长度
        vector<vector<int>> f(n, vector<int>(2, 0));
        f[0][1] = 1;
        unordered_map<int, int> hash;
        hash[arr[0]] = 0;
        for (int i = 1; i < n; i ++) {
            f[i][0] = max(f[i - 1][0], f[i - 1][1]);
            f[i][1] = 1;
            int prev = arr[i] - diff;
            if (hash.count(prev)) {
                f[i][1] = max(f[i][1], f[hash[prev]][1] + 1);
            }
            hash[arr[i]] = i;
        }
        return max(f[n - 1][0], f[n - 1][1]);
    }
};
```

### [1473. 粉刷房子 III - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/paint-house-iii/)

```cpp
class Solution {
public:
    const int INF = 0x3f3f3f3f;
    int minCost(vector<int>& houses, vector<vector<int>>& cost, int n, int m, int t) {
        // f[i][j][k] 表示 考慮前i个房子，第i个房子粉刷为j，分区数量为k的所有方案中的最小总花费
        vector<vector<vector<int>>> f(n + 1, vector<vector<int>>(m + 1, vector<int>(t + 1, 0)));

        for (int i = 0; i <= n; i ++)
            for (int j = 0; j <= m; j ++)
                f[i][j][0] = INF;

        for (int i = 1; i <= n; i ++) {
            int color = houses[i - 1]; // 当前房子的颜色
            for (int j = 1; j <= m; j ++) {
                for (int k = 1; k <= t; k ++) {
                    if (k > i) { // 如果分区数量大于房子数，不合法
                        f[i][j][k] = INF;
                        continue;
                    }

                    if (color != 0) { // 当前房子已经染色
                        if (color == j) { // 只有与当前颜色相同才能被转移
                            int tmp = INF;
                            for (int p = 1; p <= m; p ++) { // 与前面颜色不同（可组成分区的情况）
                                if (p != j) {
                                    tmp = min(tmp, f[i - 1][p][k - 1]);
                                }
                            }
                            f[i][j][k] = min(f[i - 1][j][k], tmp); // 与前面颜色相同的情况
                        } else {
                            f[i][j][k] = INF;
                        }
                    } else { // 当前房子未被染色
                        int u = cost[i - 1][j - 1];
                        int tmp = INF;
                        for (int p = 1; p <= m; p ++) { // 与前面颜色不同（可组成分区的情况）
                            if (p != j) {
                                tmp = min(tmp, f[i - 1][p][k - 1]);
                            }
                        }
                        f[i][j][k] = min(f[i - 1][j][k], tmp) + u; // 与前面颜色相同的情况
                    }
                }
            }
        }
        
        int ans = INF;
        for (int i = 1; i <= m; i ++) ans = min(ans, f[n][i][t]);
        return ans == INF ? -1: ans;
    }
};
```

### [1713. 得到子序列的最少操作次数 - 力扣（LeetCode） (leetcode-cn.com)](https://leetcode-cn.com/problems/minimum-operations-to-make-a-subsequence/)

```cpp
class Solution {
public:
    /*
        target 6 4 8 1 3 2
        arr    4 7 6 2 3 8 6 1
        list   1 0 5 4 2 0 3
    */

    int minOperations(vector<int>& t, vector<int>& a) {
        int n = t.size(), m = a.size();
        unordered_map<int, int> hash;
        for (int i = 0; i < n; i ++) hash.insert({t[i], i});
        // 建立 target 和 arr 的映射关系
        vector<int> list;
        for (auto x: a) {
             // 由于target各元素不相同，list中保存了arr与target相同元素的下标，并且递增，故可转化为LIS问题
            if (hash.count(x))  list.push_back(hash[x]);
        }
        int cnt = list.size();
        // q[i] 表示 长度为i的上升子序列 中末尾元素最小的数
        vector<int> q(cnt + 1, 0);
        // 使用LIS的贪心+二分的方法求解，复杂度为nlog(n)
        // 个人感觉这种优化方式主要是维护一个单调队列，每次加入新的元素，要和之前加入的对比，
        // 找到比自己小的最后一个数，那么它就可以代替这之前的那个数，因为它更小，更好维护递增序列，
        // 例如 1 3 5 ，加入1个4（规定长度3），那肯定4替换掉5更好
        int len = 0;
        for (int i = 0; i < cnt; i ++) {
            int l = 0, r = len;
            while (l < r) {
                int mid = l + r + 1 >> 1;
                if (q[mid] < list[i]) l = mid;
                else r = mid - 1;
            }
            q[r + 1] = list[i];
            if (r + 1 > len) len ++;
        }
        return n - len;
    }
};
```

