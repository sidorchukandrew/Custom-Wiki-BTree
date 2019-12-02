
public class TreeCacheEntry {

	private String name;
	private SimpleNode root;
	
	public TreeCacheEntry(String name, SimpleNode root) {
		this.name = name;
		this.root = root;
	}
	
	public SimpleNode getRoot() {
		return root;
	}
	
	public String getName() {
		return name;
	}
}
