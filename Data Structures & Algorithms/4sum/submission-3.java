class Solution {
    public List<List<Integer>> fourSum(int[] nums, int target) {
        Arrays.sort(nums);
        int len = nums.length;
        List<List<Integer>> res = new ArrayList<>();

        for(int i = 0; i < len - 3; i++) {
            if(i > 0 && nums[i] == nums[i-1])
                continue;
            for(int j = i + 1; j < len - 2; j++) {
                if(j > i + 1 && nums[j] == nums[j - 1])
                    continue;
                int l = j + 1, r = len - 1;
                while(l < r) {
                    long sum = (long) nums[i] + nums[j] + nums[l] + nums[r];
                    if(sum < target)
                        l++;
                    else if(sum > target)
                        r--;
                    else {
                        res.add(Arrays.asList(nums[i], nums[j], nums[l], nums[r]));
                        l++;
                        r--;
                        while(l < r && nums[l] == nums[l-1])
                            l++;
                        while(l < r && nums[r] == nums[r+1])
                            r--;
                    }
                }
            }
        }
        return res;
    }
}