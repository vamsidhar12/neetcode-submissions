class Solution {
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        Map<Integer, List<Integer>> adj = new HashMap<>();
        int[] indegree = new int[numCourses];
        
        for(int i = 0; i < numCourses; i++)
            adj.put(i, new ArrayList<>());
        
        for(int[] pre : prerequisites) {
            adj.get(pre[1]).add(pre[0]);
            indegree[pre[0]]++;
        }

        Queue<Integer> q = new LinkedList<>();
        int[] res = new int[numCourses];

        for(int i = 0; i < numCourses; i++) {
            if(indegree[i] == 0) {
                q.offer(i);
            }
        }
        
        int ind = 0;
        while(!q.isEmpty()) {
            int n = q.poll();
            res[ind++] = n;
            
            for(int nei : adj.get(n)) {
                indegree[nei]--;
                if(indegree[nei] == 0) {
                    q.offer(nei);
                }
            }
        }
        if(ind != numCourses)
            return new int[0];
        
        return res;
    }
}
