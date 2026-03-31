class Solution {
    public int minSubArrayLen(int target, int[] nums) {
        int window = Integer.MAX_VALUE;
        int l = 0, sum = 0;
        for(int r = 0; r < nums.length; r++) {
            sum += nums[r];
            while(target <= sum) {
                window = Math.min(window, r - l + 1);
                sum -= nums[l++];
            }
        }
        return window == Integer.MAX_VALUE ? 0 : window;
    }
}