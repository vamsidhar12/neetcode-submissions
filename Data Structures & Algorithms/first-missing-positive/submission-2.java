class Solution {
    public int firstMissingPositive(int[] nums) {
        int len = nums.length;

        for(int i = 0; i < len; i++)
            if(nums[i] < 0)
                nums[i] = 0;

        for(int i = 0 ; i < len; i++) {
            int ind = Math.abs(nums[i]);
            if(ind > 0 && ind <= len) {
                if(nums[ind - 1] > 0)
                    nums[ind - 1] *= -1;
                else
                    nums[ind - 1] = (len + 1) * -1;
            }
        }

        for(int i = 1; i <= len; i++) {
            if(nums[i-1] >= 0)
                return i;
        }

        return len + 1;
    }
}