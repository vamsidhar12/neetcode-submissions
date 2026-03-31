class Solution {
    public int maxProfit(int[] prices) {
        int max = 0;
        int l = 0, r = 1;
        while(r < prices.length) {
            if(prices[l] < prices[r]) {
                int profit = prices[r] - prices[l];
                max = Math.max(max, profit);
            }
            else
                l = r;
            r++;
        }
        return max;
    }
}
