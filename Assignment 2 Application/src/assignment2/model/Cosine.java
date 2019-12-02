package assignment2.model;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Hashtable;

public class Cosine {

	static SimpleNode root;
	static Hashtable<Integer, Long> keysUsed = new Hashtable<Integer, Long>();
	public static final String TREES_LOCATION = "C:\\Users\\Andrew Sidorchuk\\CSC365 Workspace\\Assignment 2 Loader\\BTrees\\";
	static final int SIZE_OF_NODE = 168;
	static final int K = 8;
	static final int INT_SIZE = 4;
	static final int LONG_SIZE = 8;
	static final int POSITION_OFFSET = 0;
	static final int PARENT_OFFSET = POSITION_OFFSET + 8;
	static final int LEAFSTATUS_OFFSET = PARENT_OFFSET + 8;
	static final int FIRST_KEY_OFFSET = LEAFSTATUS_OFFSET + INT_SIZE;
	static final int FIRST_CHILD_OFFSET = FIRST_KEY_OFFSET + (K - 1) * LONG_SIZE;
	static final int FREQUENCY_OFFSET = FIRST_CHILD_OFFSET + K * LONG_SIZE;
	
	static double cumulativeNumeratorSum = 0, cumulativeFirstFactorDenominator = 0, cumulativeSecondFactorDenominator;
	static RandomAccessFile file;
	static FileChannel channel;
	
	public static void display(SimpleNode root) {
		
		System.out.println("POSITION : " + root.position);
		for(long key : root.keys) {
			if(key != Long.MAX_VALUE)
				System.out.print(key + " ");
			else
				System.out.print("_ ");
		}
		
		System.out.println();
		
		for(SimpleNode child  : root.children) {
			if(child != null)
				display(child);
		}
			
	}
	
	public static SimpleNode read_tree(String fileName) {
	
		try {
			file = new RandomAccessFile(fileName, "r");
			channel = file.getChannel();
			
			root = read_disk(0);
			
			channel.close();
			file.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return root;
	}
	
	private static SimpleNode read_disk(long pointer) {
		
		SimpleNode x = new SimpleNode();
		
		try {
			ByteBuffer buffer = ByteBuffer.allocate(SIZE_OF_NODE);
			
			channel.position(pointer);
			channel.read(buffer);
			x.position = buffer.getLong(POSITION_OFFSET);
			
			x.parent = null;
			
			x.leafStatus = buffer.getInt(LEAFSTATUS_OFFSET);
				
			for(int keyIndex = 0; keyIndex < x.keys.length; ++keyIndex) 
				x.keys[keyIndex] = buffer.getLong(FIRST_KEY_OFFSET + (keyIndex * LONG_SIZE));
				
			for(int childIndex = 0; childIndex < x.children.length; ++childIndex) {
				
				long nextPointer = buffer.getLong(FIRST_CHILD_OFFSET + (childIndex * LONG_SIZE));
				if(nextPointer != Long.MAX_VALUE)
					x.children[childIndex] = read_disk(nextPointer);
			}
				
			for(int freqIndex = 0; freqIndex < x.frequency.length; ++freqIndex) {
				x.frequency[freqIndex] = buffer.getInt(FREQUENCY_OFFSET + (freqIndex * INT_SIZE));	
			}
			
			buffer.clear();
			return x;		
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static double cosine(SimpleNode a, SimpleNode b) {
		
		keysUsed = new Hashtable<Integer, Long>();
		
		cumulativeNumeratorSum = 0;
		cumulativeFirstFactorDenominator = 0;
		cumulativeSecondFactorDenominator = 0;
		
		forEachInA(a, b);
		forEachInB(b, a);
		
		if(cumulativeNumeratorSum == 0)
			return 0;
		else{
			//Numerator is complete already, denominator needs to be combined
			double denominator = (cumulativeFirstFactorDenominator * cumulativeFirstFactorDenominator)
								+ (cumulativeSecondFactorDenominator * cumulativeSecondFactorDenominator);
			
			return cumulativeNumeratorSum / denominator;
		}
	}
	
	public static void forEachInA(SimpleNode a, SimpleNode b) {
		
		int sizeOfChildrenA = check_size_of(a.children);
		
		for(int childIndex = 0; childIndex < sizeOfChildrenA; ++childIndex)
			forEachInA(a.children[childIndex], b);
		
		int sizeOfKeysA = check_size_of(a.keys);
		for(int keyIndex = 0; keyIndex < sizeOfKeysA; ++keyIndex) {
			
			long currentKey = a.keys[keyIndex];
			double frequencyB = searchForFrequency(b, currentKey);
			keysUsed.put(new Long(currentKey).hashCode(), currentKey);
			computeNumerator(a.frequency[keyIndex], frequencyB);
			computeDenominator(a.frequency[keyIndex], frequencyB);
		}
	}
	
	public static void forEachInB(SimpleNode b, SimpleNode a) {
		int sizeOfChildrenB = check_size_of(b.children);
		
		for(int childIndex = 0; childIndex < sizeOfChildrenB; ++childIndex)
			forEachInB(b.children[childIndex], a);
		
		int sizeOfKeysB = check_size_of(b.keys);
		for(int keyIndex = 0; keyIndex < sizeOfKeysB; ++keyIndex) {
			long currentKey = b.keys[keyIndex];
			
			if(!keysUsed.containsKey(new Long(currentKey).hashCode())) {
				double frequencyA = searchForFrequency(a, currentKey);
				keysUsed.put(new Long(currentKey).hashCode(), currentKey);
				computeNumerator(frequencyA, b.frequency[keyIndex]);
				computeDenominator(frequencyA, b.frequency[keyIndex]);
			}
		
		}
	}
	
	private static void computeNumerator(double frequencyA, double frequencyB)
	{
		//sum of ((x-y)^2)
		cumulativeNumeratorSum += ((frequencyA - frequencyB) * (frequencyA - frequencyB));
	}
	
	private static void computeDenominator(double frequencyA, double frequencyB)
	{
		//sum of all x's
		cumulativeFirstFactorDenominator += frequencyA;
		
		//sum of all y's
		cumulativeSecondFactorDenominator += frequencyB;
	}
	
	private static int searchForFrequency(SimpleNode x, long key) {
		
		int i = 0;
		int numberOfXKeys = check_size_of(x.keys);
		
		while(i < numberOfXKeys && key > x.keys[i])
			++i;
		
		if(i < numberOfXKeys && key == x.keys[i])
			return x.frequency[i];
		
		if(x.leafStatus == 1)
			return 0;
		else {
			SimpleNode child = x.children[i];
			return searchForFrequency(child, key);
		}
	}

	private static int check_size_of(long [] keys) {
		int count = 0;
		
		for(long key : keys) {
			if(key == Long.MAX_VALUE)
				break;
			else
				++count;
		}
		
		return count;
	}
	
	private static int check_size_of(SimpleNode [] children) {
		int count = 0;
		
		for(SimpleNode child : children) {
			if(child == null)
				break;
			else
				++count;
		}
		
		return count;
	}
	
}

