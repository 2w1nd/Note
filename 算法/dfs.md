#### [剑指 Offer 12. 矩阵中的路径](https://leetcode-cn.com/problems/ju-zhen-zhong-de-lu-jing-lcof/)

```cpp
class Solution {
public:
    bool exist(vector<vector<char>>& board, string word) {
        for (int i = 0; i < board.size(); i ++)
            for (int j = 0; j < board[i].size(); j ++) {
                if (dfs(board, word, 0, i, j)) return true;
            }
        return false;
    }

    bool dfs(vector<vector<char>> &board, string word, int u, int x, int y) {
        if (board[x][y] != word[u]) return false;
        if (u == word.size() - 1) return true;

        char t = board[x][y];
        board[x][y] = '*';
        int dx[] = {-1, 0, 1, 0}, dy[] = {0, 1, 0, -1};
        for (int i = 0; i < 4; i ++) {
            int nx = x + dx[i], ny = y + dy[i];
            if (nx >= 0 && nx < board.size() && ny >= 0 && ny < board[0].size() && board[nx][ny] != '*') {
                if (dfs(board, word, u + 1, nx, ny)) return true;
            }
        }
        board[x][y] = t;
        return false;
    }
};
```

#### [剑指 Offer 13. 机器人的运动范围](https://leetcode-cn.com/problems/ji-qi-ren-de-yun-dong-fan-wei-lcof/)

```cpp
class Solution {
public: 

    int getSingle(int a) {
        int res = 0;
        while (a) {
            res += a % 10;
            a /= 10;
        }
        return res;
    }
    
    int getSum(int x, int y) {
        return getSingle(x) + getSingle(y);
    }

    int dfs(int sx, int sy, int ex, int ey, int k, vector<vector<bool>> &st) {
        if (sx >= ex || sy >= ey || getSum(sx, sy) > k || st[sx][sy]) return 0;
        st[sx][sy] = true;
        return 1 + dfs(sx + 1, sy, ex, ey, k, st) + dfs(sx, sy + 1, ex, ey, k, st);
    }

    int movingCount(int m, int n, int k) {
        vector<vector<bool>> st(m, vector<bool>(n, false));
        return dfs(0, 0, m, n, k, st);
    }
};
```

