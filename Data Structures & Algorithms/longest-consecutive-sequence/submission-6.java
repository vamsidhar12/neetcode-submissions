class Solution {
    public int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for(int num : nums)
            set.add(num);
        int res = 0;
        for(int i = 0; i < nums.length; i++) {
            int num = nums[i];
            if(set.remove(num)) {
                int count = 1;
                while(set.remove(num+1)) {
                    count++;
                    num++;
                }
                num = nums[i];
                while(set.remove(num-1)) {
                    count++;
                    num--;
                }
                res = Math.max(res, count);
            }
        }
        return res;
    }
}
