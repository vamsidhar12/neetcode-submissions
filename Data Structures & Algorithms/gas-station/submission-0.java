class Solution {
    public int canCompleteCircuit(int[] gas, int[] cost) {
        int total = 0, res = 0, diff = 0;
        for(int i = 0; i < gas.length; i++) {
            diff += gas[i] - cost[i];
            total += (gas[i] - cost[i]);
            if(diff < 0) {
                res = i + 1;
                diff = 0;
            }
        }
        return total < 0 ? -1 :res;
    }
}
