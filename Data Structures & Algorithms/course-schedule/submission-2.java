class Solution {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        HashMap<Integer, List<Integer>> prereqs = new HashMap<>();
        for(int i = 0; i < numCourses; i++)
            prereqs.put(i, new ArrayList<>());
        for(int[] pre : prerequisites)
            prereqs.get(pre[0]).add(pre[1]);

        boolean[] visited = new boolean[numCourses];
        for(int i = 0; i < numCourses; i++) {
            if(!dfs(i, prereqs, visited))
                return false;
        }
        return true;
    }

    private boolean dfs(int course, HashMap<Integer, List<Integer>> prereqs, boolean[] visited) {
        if(visited[course])
            return false;

        if(prereqs.get(course).isEmpty())
            return true;

        visited[course] = true;

        for(int pre : prereqs.get(course))
            if(!dfs(pre, prereqs, visited))
                return false;

        visited[course] = false;
        prereqs.put(course, new ArrayList<>());
        
        return true;
    }
}
