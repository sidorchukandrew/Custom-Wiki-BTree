import java.io.File;
import java.util.ArrayList;

public class Main {

	static final int CACHE_SIZE = 98;
	
	public static void main(String[] args) {

//		WebsiteUtilities.storeWebsitesLocallyInCache();
//		WebsiteUtilities.createBTreesFromCache();
		
		LRUCache cache = new LRUCache(CACHE_SIZE);
		
		File folder = new File(BTreeUtilities.TREES_LOCATION);
		File [] files = folder.listFiles();

		for(int i = 0; i < CACHE_SIZE; ++i) {
			SimpleNode root = BTreeUtilities.read_tree(files[i].getPath());
			cache.set(files[i].getName().hashCode(), new TreeCacheEntry(files[i].getName(), root));
		}
		
		KMedoidsUtilities.initializeMedoids(cache);

	}
}
