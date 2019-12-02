import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache {
	 
	private LinkedHashMap<Integer, TreeCacheEntry> map;
     private final int CAPACITY;
     
     public LRUCache(int capacity) {
         CAPACITY = capacity;
        
         map = new LinkedHashMap<Integer, TreeCacheEntry>(capacity, 0.75f, true){
             protected boolean removeEldestEntry(Map.Entry eldest) {
            	 if(size() > CAPACITY)
            		 System.out.println("Evicting");
                 return size() > CAPACITY;
             }
         };
     }
     public TreeCacheEntry get(int key) {
         return map.getOrDefault(key, null);
     }
     public void set(int key, TreeCacheEntry value) {
         map.put(key, value);
     }
     
     public LinkedHashMap getMap() {
    	 return map;
     }
     
     public int getSize() {
    	 return map.size();
     }
}
