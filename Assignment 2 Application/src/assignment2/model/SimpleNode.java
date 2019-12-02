package assignment2.model;


public class SimpleNode {

	static final int K = 8;
	public long [] keys = new long [K - 1];											
	public SimpleNode [] children = new SimpleNode [K];											
	
	static final long NULL = -1L;
	public SimpleNode parent; 																
	public long position;																
	public int leafStatus;																
	public int [] frequency = new int[K - 1];	
}

