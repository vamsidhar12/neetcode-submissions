class Solution {
    public List<Integer> partitionLabels(String s) {
        Map<Character, Integer> lastIndex = new HashMap<>();
        for(int i = 0; i < s.length(); i++)
            lastIndex.put(s.charAt(i), i);

        int size = 0, last = 0;
        List<Integer> res = new ArrayList<>();

        for(int i = 0; i < s.length(); i++) {
            size++;
            last = Math.max(last, lastIndex.get(s.charAt(i)));
            if(i == last) {
                res.add(size);
                size = 0;
            }
        }
        return res;
    }
}
