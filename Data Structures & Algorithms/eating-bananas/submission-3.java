class Solution {
    public int minEatingSpeed(int[] piles, int h) {
        int l = 1, r = Arrays.stream(piles).max().getAsInt();
        while(l < r) {
            int m = l + (r - l) / 2;
            int sum = 0;
            for(int pile : piles) {
                sum += (int)Math.ceil((double) pile / m);
            }
            if(sum <= h)
                r = m;
            else
                l = m + 1;
        }
        return r;
    }
}
