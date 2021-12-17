#### [剑指 Offer 47. 礼物的最大价值](https://leetcode-cn.com/problems/li-wu-de-zui-da-jie-zhi-lcof/)

```cpp
// 优化为一维
class Solution {
public:
    int maxValue(vector<vector<int>>& grid) {
        int n = grid.size(), m = grid[0].size();
        vector<int> dp(m + 1);
        for (int i = 1; i <= n; i ++)
            for (int j = 1; j <= m; j ++)
                dp[j] = max(dp[j], dp[j - 1]) + grid[i - 1][j - 1];
        return dp[m];
    }
};
```

#### [剑指 Offer 63. 股票的最大利润](https://leetcode-cn.com/problems/gu-piao-de-zui-da-li-run-lcof/)

```cpp
class Solution {
public:
    int maxProfit(vector<int>& prices) {
        int profit = INT_MIN, cost = INT_MAX;
        for (auto c: prices) {
            cost = min(cost, c);
            profit = max(profit, c - cost);
        }
        if (profit == INT_MIN) return 0;
        return profit;
    }
};
```

#### [221. 最大正方形](https://leetcode-cn.com/problems/maximal-square/)

```cpp
class Solution {
public:
    int maximalSquare(vector<vector<char>>& matrix) {
        int res = 0;
        int n = matrix.size(), m = matrix[0].size();
        vector<vector<int>> dp(n + 1, vector<int>(m + 1));
        for (int i = 1; i <= n; i ++)
            for (int j = 1; j <= m; j ++) 
                if (matrix[i - 1][j - 1] == '1') {
                    dp[i][j] = min(dp[i - 1][j], min(dp[i][j - 1], dp[i - 1][j - 1])) + 1;
                    res = max(res, dp[i][j]);
                }
        return res * res;
    }
};
```

#### [82. 删除排序链表中的重复元素 II](https://leetcode-cn.com/problems/remove-duplicates-from-sorted-list-ii/)

```cpp
/**
 * Definition for singly-linked list.
 * struct ListNode {
 *     int val;
 *     ListNode *next;
 *     ListNode() : val(0), next(nullptr) {}
 *     ListNode(int x) : val(x), next(nullptr) {}
 *     ListNode(int x, ListNode *next) : val(x), next(next) {}
 * };
 */
class Solution {
public:
    ListNode* deleteDuplicates(ListNode* head) {
        auto dummy = new ListNode(-1);
        dummy->next = head;
        auto prev = dummy, cur = head;
        while (cur) {
            while (cur && cur->next && cur->val == cur->next->val) cur = cur->next;  // 在重复元素组中最后一个元素停下
            if (prev->next == cur) prev = prev->next;
            else prev->next = cur->next;
            cur = cur->next;
        }
        return dummy->next;
    }
};
```

#### [148. 排序链表](https://leetcode-cn.com/problems/sort-list/)

```cpp
/**
 * Definition for singly-linked list.
 * struct ListNode {
 *     int val;
 *     ListNode *next;
 *     ListNode() : val(0), next(nullptr) {}
 *     ListNode(int x) : val(x), next(nullptr) {}
 *     ListNode(int x, ListNode *next) : val(x), next(next) {}
 * };
 */
class Solution {
public:
    ListNode* sortList(ListNode* head) {
        auto dummy = new ListNode(-1);
        dummy->next = head;
        int n = 0;
        for (auto p = head; p; p = p->next) n ++;

        for (int i = 1; i < n; i *= 2) {
            auto cur = dummy;
            for (int j = 0; j + i < n; j += i * 2) {
                auto first = cur->next, second = cur->next;
                for (int k = 0; k < i; k ++) second = second->next;
                int f = 0, s = 0;
                while (f < i && s < i && second) {
                    if (first->val < second->val) {
                        cur = cur->next = first, first = first->next, f ++;
                    } else {
                        cur = cur->next = second, second = second->next, s ++;
                    }
                }
                while (f < i) cur = cur->next = first, first = first->next, f ++;
                while (s < i && second) cur = cur->next = second, second = second->next, s ++;
                cur->next = second;
            }
        } 
        return dummy->next;
    }
};
```

#### [198. 打家劫舍](https://leetcode-cn.com/problems/house-robber/)

```cpp
class Solution {
public:
    int rob(vector<int>& nums) {
        int n = nums.size();
        vector<vector<int>> dp(n, vector<int>(2, 0));

        dp[0][0] = nums[0], dp[0][1] = 0;
        for (int i = 1; i < n; i ++) {
            dp[i][0] = dp[i - 1][1] + nums[i];
            dp[i][1] = max(dp[i - 1][0], dp[i - 1][1]);
        }
        return max(dp[n - 1][0], dp[n - 1][1]);
    }
};
```

#### [213. 打家劫舍 II](https://leetcode-cn.com/problems/house-robber-ii/)

```cpp
class Solution {
public:
    int rob(vector<int>& nums) {
        int n = nums.size();
        if (n == 1) return nums[0];
        vector<vector<int>> dp(n, vector<int>(2, 0));
        // 0 抢， 1 不抢
        // 首位不抢
        dp[0][1] = dp[0][0] = 0;
        for (int i = 1; i < n; i ++) {
            dp[i][0] = dp[i - 1][1] + nums[i];
            dp[i][1] = max(dp[i - 1][0], dp[i - 1][1]);
        }
        int res1 = max(dp[n - 1][0], dp[n - 1][1]);
        // 首位抢，末尾不抢
        // dp.clear();
        dp[0][1] = 0, dp[0][0] = nums[0];
        for (int i = 1; i < n - 1; i ++) {
            dp[i][0] = dp[i - 1][1] + nums[i];
            dp[i][1] = max(dp[i - 1][0], dp[i - 1][1]);
        }
        int res2 = max(dp[n - 2][0], dp[n - 2][1]);
        return max(res1, res2);
    }
};
```

#### [123. 买卖股票的最佳时机 III](https://leetcode-cn.com/problems/best-time-to-buy-and-sell-stock-iii/)

```cpp
class Solution {
public:
    int maxProfit(vector<int>& prices) {
        int n = prices.size();
        vector<vector<int>> dp(n + 1, vector<int>(4, 0));

        dp[0][0] = -prices[0];
        dp[0][2] = -prices[0];
        for (int i = 1; i < n; i ++) {
            dp[i][0] = max(dp[i - 1][0], -prices[i]);
            dp[i][1] = max(dp[i - 1][1], dp[i - 1][0] + prices[i]);
            dp[i][2] = max(dp[i - 1][2], dp[i - 1][1] - prices[i]);
            dp[i][3] = max(dp[i - 1][3], dp[i - 1][2] + prices[i]);
        }
        int res = INT_MIN;
        for (int i = 0; i < 4; i ++) res = max(dp[n - 1][i], res);
        return res;
    }
};
```

