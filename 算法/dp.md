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

