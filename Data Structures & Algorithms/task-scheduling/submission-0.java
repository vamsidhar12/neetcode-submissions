class Solution {
    public int leastInterval(char[] tasks, int n) {
        int[] freq = new int[26];
        int max = 0;
        for(char c : tasks) {
            freq[c - 'A']++;
            max = Math.max(max, freq[c - 'A']);
        }
        
        int count = 0;
        for(int f : freq)
            if(f == max)
                count++;
        int res = (max - 1) * (n + 1) + count;
        return Math.max(res, tasks.length);
    }
}
