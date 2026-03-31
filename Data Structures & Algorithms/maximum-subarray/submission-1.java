class Solution {
    public int maxSubArray(int[] nums) {
        int max = Integer.MIN_VALUE, curr = 0;
        for(int num : nums) {
            if(curr < 0)
                curr = 0;
            curr += num;
            max = Math.max(max, curr);
        }
        return max;
    }
}
