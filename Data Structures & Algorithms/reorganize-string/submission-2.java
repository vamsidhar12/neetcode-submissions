class Solution {
    public String reorganizeString(String s) {
        int[] freq = new int[26];
        int max = 0;
        for(char c : s.toCharArray()) {
            freq[c-'a']++;
            max = Math.max(max, freq[c-'a']);
        }
        
        PriorityQueue<int[]> pq = new PriorityQueue<>((a,b) -> b[0] - a[0]);
        StringBuilder res = new StringBuilder();

        for(int i = 0; i < 26; i++)
            if(freq[i] > 0)
                pq.offer(new int[] {freq[i], i});

        int[] prev = null;
        while(!pq.isEmpty() || prev != null) {
            if(prev != null && pq.isEmpty())
                return "";
            int[] curr = pq.poll();
            res.append((char)(curr[1] + 'a'));
            curr[0]--;

            if(prev != null) {
                pq.offer(prev);
                prev = null;
            }
            if(curr[0] > 0)
                prev = curr;
        }
        return res.toString();
    }
}