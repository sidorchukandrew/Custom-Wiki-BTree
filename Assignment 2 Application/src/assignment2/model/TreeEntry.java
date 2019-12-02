package assignment2.model;

public class TreeEntry {
	private String name;
	private SimpleNode root;
	
	public TreeEntry(String name, SimpleNode root) {
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