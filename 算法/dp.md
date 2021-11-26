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

