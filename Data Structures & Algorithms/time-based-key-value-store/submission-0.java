class TimeMap {

    HashMap<String, TreeMap<Integer, String>> map;

    public TimeMap() {
        map = new HashMap<>();
    }
    
    public void set(String key, String value, int timestamp) {
        map.computeIfAbsent(key, k -> new TreeMap<>()).put(timestamp, value);
    }
    
    public String get(String key, int timestamp) {
        if(!map.containsKey(key))
            return "";
        TreeMap<Integer, String> temp = map.get(key);
        Map.Entry<Integer, String> entry = temp.floorEntry(timestamp);
        return entry == null ? "" : entry.getValue();
    }
}
