class Solution {
    public int maxAreaOfIsland(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        int maxArea = 0;
        for(int i = 0; i < m; i++) {
            for(int j = 0; j < n; j++) {
                if(grid[i][j] == 1) {
                    int area = dfs(grid, i, j, m, n);
                    maxArea = Math.max(maxArea, area);
                }
            }
        }
        return maxArea;
    }

    private int dfs(int[][] grid, int r, int c, int m, int n) {
        if(r < 0 || r >= m || c < 0 || c >= n || grid[r][c] == 0)
            return 0;
        
        grid[r][c] = 0;

        return 1 + dfs(grid, r + 1, c, m, n) 
                + dfs(grid, r - 1, c, m, n) 
                + dfs(grid, r, c + 1, m, n) 
                + dfs(grid, r, c - 1, m, n);
    }
}
