class Solution {
    public int[] maxSlidingWindow(int[] nums, int k) {
        int len = nums.length;
        int[] res = new int[len - k + 1];
        PriorityQueue<int[]> pq = new PriorityQueue<>((a,b) -> b[0] - a[0]);
        int ind = 0;
        for(int i = 0; i < len; i++) {
            pq.add(new int[]{nums[i], i});
            if(i >= k - 1) {
                while(pq.peek()[1] <= i - k)
                    pq.poll();
            res[ind++] = pq.peek()[0];
            }
        }
        return res;
    }
}
