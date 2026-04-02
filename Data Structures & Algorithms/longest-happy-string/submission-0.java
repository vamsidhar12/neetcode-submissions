class Solution {
    public String longestDiverseString(int a, int b, int c) {
        StringBuilder sb = new StringBuilder();
        PriorityQueue<int[]> pq = new PriorityQueue<>((x,y) -> y[0] - x[0]);
        if(a > 0 )
            pq.add(new int[]{a, 'a'});
        if(b > 0)
            pq.add(new int[]{b, 'b'});
        if(c > 0)
            pq.add(new int[]{c, 'c'});

        while(!pq.isEmpty()) {
            int[] count = pq.poll();
            if(sb.length() > 1 
            && sb.charAt(sb.length() - 1) == count[1] 
            && sb.charAt(sb.length() - 2) == count[1]) {
                if(pq.isEmpty())
                    break;
                int[] next = pq.poll();
                sb.append((char)next[1]);
                next[0]--;
                if(next[0] > 0)
                    pq.add(next);
                pq.add(count);
            } else {
                sb.append((char)count[1]);
                count[0]--;
                if(count[0] > 0)
                    pq.add(count);
            }
        }
        return sb.toString();
    }
}