class TimeMap {

    HashMap<String, TreeMap<Integer, String>> map;

    public TimeMap() {
        map = new HashMap<>();
    }
    
    public void set(String key, String value, int timestamp) {
        map.putIfAbsent(key, new TreeMap<>());
        map.get(key).put(timestamp, value);
    }
    
    public String get(String key, int timestamp) {
        TreeMap<Integer, String> curr = map.get(key);
        if(curr == null)
            return "";
        
        Integer n = curr.floorKey(timestamp);
        if(n == null)
            return "";

        return curr.get(n);
    }
}
