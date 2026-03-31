class Solution {
    public List<Integer> findClosestElements(int[] arr, int k, int x) {
        int l = 0, r = arr.length - k;
        while(l < r) {
            int m = l + (r - l) / 2;
            if(x - arr[m] > arr[m + k] - x)
                l = m + 1;
            else
                r = m;
        }

        List<Integer> res = new ArrayList<>();
        for(int i = l; i < l + k; i++) {
            res.add(arr[i]);
        }
        return res;
    }
}