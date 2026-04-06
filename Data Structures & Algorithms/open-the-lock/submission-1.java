class Solution {
    public int openLock(String[] deadends, String target) {
        Set<String> visit = new HashSet<>(Arrays.asList(deadends));
        if(visit.contains("0000"))
            return -1;
        int count = 0;
        Queue<String> q = new LinkedList<>();
        q.add("0000");
        visit.add("0000");

        while(!q.isEmpty()) {
            int size = q.size();
            for(int ind = 0; ind < size; ind++) {
                String code = q.poll();
                if(code.equals(target))
                    return count;
                
                for(int i = 0; i < 4; i++) {
                    char[] arr = code.toCharArray();
                    int d = arr[i] - '0';

                    arr[i] = (char) (((d + 1) % 10) + '0');
                    String up = String.valueOf(arr);
                    if(!visit.contains(up)) {
                        q.offer(up);
                        visit.add(up);
                    }
                   arr[i] = (char) (((d + 9) % 10) + '0');
                   String down = String.valueOf(arr);
                   if(!visit.contains(down)) {
                        q.offer(down);
                        visit.add(down);
                    }
                }
            }
            count++;
        }
        return -1;
    }
}