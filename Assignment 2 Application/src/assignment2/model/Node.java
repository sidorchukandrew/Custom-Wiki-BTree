package assignment2.model;

public class Node {

	final int INT_SIZE = 4;
	final int LONG_SIZE = 8;
	final int POSITION_OFFSET = 0;
	final int PARENT_OFFSET = POSITION_OFFSET + 8;
	final int LEAFSTATUS_OFFSET = PARENT_OFFSET + 8;
	final int FIRST_KEY_OFFSET = LEAFSTATUS_OFFSET + INT_SIZE;
	final int FIRST_CHILD_OFFSET = FIRST_KEY_OFFSET + (K - 1) * LONG_SIZE;
	final int FREQUENCY_OFFSET = FIRST_CHILD_OFFSET + K * LONG_SIZE;
	
	static final int K = 8;
	long [] keys = new long [K - 1];											// 8 * 7 = 56 bytes
	long [] children = new long [K];											// 8 * 8 = 64 bytes
	
	static final long NULL = -1L;
	long parent; 																// 8 bytes
	long position;																// 8 bytes
	int leafStatus;																// 4 bytes
	int [] frequency = new int[K - 1];											// 4 * 7 = 28 bytes
}
