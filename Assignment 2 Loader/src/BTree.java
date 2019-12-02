import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;


//                  N O T E S
// 
// - use RandomAccessFile
// - use ByteBuffer
//

public class BTree {
	
	final int INT_SIZE = 4;
	final int LONG_SIZE = 8;
	final int POSITION_OFFSET = 0;
	final int PARENT_OFFSET = POSITION_OFFSET + 8;
	final int LEAFSTATUS_OFFSET = PARENT_OFFSET + 8;
	final int FIRST_KEY_OFFSET = LEAFSTATUS_OFFSET + INT_SIZE;
	final int FIRST_CHILD_OFFSET = FIRST_KEY_OFFSET + (K - 1) * LONG_SIZE;
	final int FREQUENCY_OFFSET = FIRST_CHILD_OFFSET + K * LONG_SIZE;
	final long NULL = Long.MAX_VALUE;
	
	static final int K = 8;
	
	RandomAccessFile file;
	FileChannel channel;
	Node root;
	final int SIZE_OF_NODE = 168;
	String name;
	
	private final String PREFIX_ADDRESS_OF_TREES = "C:\\Users\\Andrew Sidorchuk\\CSC365 Workspace\\Assignment 2 Loader\\BTrees\\";
	
	public BTree(String name, String [] contents) {

		
		try {
			file = new RandomAccessFile("C:\\Users\\Andrew Sidorchuk\\CSC365 Workspace\\tester2.txt", "rw");
			channel = file.getChannel();
			
			this.name = name;
			create();
			
//			insert(100L);
//			insert(100L);
//			insert(70L);
//			insert(900L);
//			insert(101L);
//			insert(30L);
//			insert(720L);
//			insert(650L);
//	
//			insert(300L);									// splits here
//			
//			insert(200L);
//			insert(1L);
//			insert(700L);
//			insert(800L);
//			insert(900L);
//			insert(502L);	
//			insert(503L);
			
//			insert(550L);									// splits here
//			insert(1000L);
//			insert(27L);
//			
//			insert(400L);
//			insert(701L);
//			insert(702L);
//			insert(703L);
//			
//			insert(704L);									// splits here
//			insert(504L);
//			insert(505L);
//			insert(506L);
//			
//			insert(507L);									// splits here
//			insert(28L);
//			insert(29L);
//			insert(30L);
//			insert(31L);
//			
//			insert(15L);									// splits here
//			insert(401L);
//			insert(402L);
//			insert(403L);
//			
//			insert(250L);									// splits here
//			insert(551L);
//			insert(552L);
//			insert(553L);
			
//			for(String word : contents) {
//				word = word.toLowerCase();
//				insert(MurmurHash.hash64(word));
//			}
			
			channel.close();
			file.close();
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void create(){
		
		Node x = allocate_node();
		
		x.leafStatus = 1;		
		disk_write(x);
		
		root = x;
	}
	
	public Node allocate_node() {
		
		Node x = new Node();
		
		File f = new File("C:\\Users\\Andrew Sidorchuk\\CSC365 Workspace\\tester2.txt");
		long seekPosition = f.length();
		x.position = seekPosition;
		
		x.parent = NULL;
		x.leafStatus = -1;
		
		for(int keyIndex = 0; keyIndex < x.keys.length; ++keyIndex)
			x.keys[keyIndex] = NULL;
		
		for(int childIndex = 0; childIndex < x.children.length; ++childIndex)
			x.children[childIndex] = NULL;
		
		for(int freqIndex = 0; freqIndex < x.frequency.length; ++freqIndex)
			x.frequency[freqIndex] = -1;
		
		try {

			
			ByteBuffer byteBuffer =  ByteBuffer.allocate(SIZE_OF_NODE);
			byteBuffer.putLong(x.POSITION_OFFSET, x.position);
			byteBuffer.putLong(x.PARENT_OFFSET, x.parent);
			byteBuffer.putInt(x.LEAFSTATUS_OFFSET, x.leafStatus);
			
			for(int keyIndex = 0; keyIndex < x.keys.length; ++keyIndex)
				byteBuffer.putLong((x.FIRST_KEY_OFFSET + (keyIndex * x.LONG_SIZE)), x.keys[keyIndex]);
			
			for(int childIndex = 0; childIndex < x.children.length; ++childIndex)
				byteBuffer.putLong((x.FIRST_CHILD_OFFSET + (childIndex * x.LONG_SIZE)), x.children[childIndex]);
			
			for(int freqIndex = 0; freqIndex < x.frequency.length; ++freqIndex)
				byteBuffer.putInt((x.FREQUENCY_OFFSET + (freqIndex * x.INT_SIZE)), x.frequency[freqIndex]);
			
			file.seek(x.position);
			channel.write(byteBuffer);

			 
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return x;
	}
	
	public long search(Node x, long key) {
	
		int i = 0;
		int numberOfXKeys = check_size_of(x.keys);
		
		while(i < numberOfXKeys && key > x.keys[i])
			++i;
		
		if(i < numberOfXKeys && key == x.keys[i])
			return x.position;
		
		if(x.leafStatus == 1)
			return NULL;
		else {
			Node child = disk_read(x.children[i]);
			return search(child, key);
		}
	}
	
	public void insert(long key) {
		
		long pos = search(root, key);
		
		if(pos != NULL) {
			updateFrequency(pos, key);
			return;
		}
		
		int sizeOfRootKeys = check_size_of(root.keys);
		Node r = root;
		
		if(sizeOfRootKeys == K - 1) {
			Node newRoot = allocate_node();
			root = newRoot;
			newRoot.leafStatus = 0;
		
			long temp = r.position;						// allocate node sets the address of the next node
			r.position = newRoot.position;				// we need to switch the old root's position with the new root's position
			newRoot.position = temp; 
			
			newRoot.children[0] = r.position;
			r.parent = newRoot.position;

			split_child(newRoot, 0, r);  
			
			insert_nonfull(newRoot, key);
		}
		else
			insert_nonfull(r, key);
	}
	
	public void insert_nonfull(Node x, long key) {
		
		int i = check_size_of(x.keys);
		
		// f x is a leaf, insert the key into this node
		if(x.leafStatus == 1) {
			
			while(i > 0 && key < x.keys[i - 1]) {
				x.keys[i] = x.keys[i  - 1];
				x.frequency[i] = x.frequency[i - 1];
				x.frequency[i - 1] = -1;
				--i;
			}
			
			x.keys[i] = key;
			
			if(x.frequency[i] == -1)
				x.frequency[i] = 1;
			else
				x.frequency[i]++;

			disk_write(x);
		}
		
		// If x is not a leaf, we must find the correct leaf to descend to
		else {
			
			while(i > 0 && key < x.keys[i - 1])
				--i;
			
			Node child = disk_read(x.children[i]);
			int sizeOfChildKeys = check_size_of(child.keys);
			
			// If the node is full, we need to split it
			if(sizeOfChildKeys == K - 1) {
				split_child(x, i, child);										// i = child index, i - 1 = key index+
				
				if(key > x.keys[i])
					++i;
				
				child = disk_read(x.children[i]);
			}
			
			insert_nonfull(child, key);
		}
	}
	
	public void updateFrequency(long pos, long key) {
		
		Node x = disk_read(pos);
		int i = 0;
		
		while(x.keys[i] != NULL) {
			if(x.keys[i] == key)
				break;
			else
				++i;
		}

		++x.frequency[i];
	
		disk_write(x);
	}
	
	public Node split_child(Node parent, int childIndex, Node child) { 

		int t = parent.K / 2;
		Node z = allocate_node();
		
		// Set our new node's leaf status
		z.leafStatus = child.leafStatus;
		
		// Put back half of child's keys and frequencies on front of new node
		for(int j = 0; j <= t - 2; ++j) { 
			z.keys[j] = child.keys[j  + t];
			z.frequency[j] = child.frequency[j + t];
			child.keys[j + t] = NULL;
			child.frequency[j + t] = -1;
		}
		
		// If child is not a leaf, we need to move children to z. Change z's children's parent  to z
		if(child.leafStatus != 1)
			for(int j = 0; j < t; ++j){
				z.children[j] = child.children[j + t];
				child.children[j + t] = NULL;
				Node grandChild = disk_read(z.children[j]);
				grandChild.parent = z.position;
				disk_write(grandChild);
			}
		
		// Move the references to the parent's children up one spot so we can insert z as a child
		int sizeOfParentKeys = check_size_of(parent.keys);
		
		for(int pointer = sizeOfParentKeys + 1; pointer > childIndex + 1; --pointer)
			parent.children[pointer] = parent.children[pointer - 1];				
		
		parent.children[childIndex + 1] = z.position;
		
		// Move keys in parent over one spot so we can promote a key into the parent		
		for(int pointer = sizeOfParentKeys; pointer > childIndex; --pointer)
			parent.keys[pointer] = parent.keys[pointer - 1];
		
		for(int pointer = sizeOfParentKeys; pointer > childIndex; --pointer)
			parent.frequency[pointer] = parent.frequency[pointer - 1];
		
		// Do the actual promotion
		parent.keys[childIndex] = child.keys[t - 1];
		parent.frequency[childIndex] = child.frequency[t - 1];
		
		child.keys[t - 1] = NULL;
		child.frequency[t - 1] = -1;
		
		z.parent = child.parent;
		
		disk_write(child);
		disk_write(z);
		disk_write(parent);
		
		return z;
	}
	
	public Node disk_read(long pointer){	
		
		Node x = new Node();
		
		try {
			ByteBuffer buffer = ByteBuffer.allocate(SIZE_OF_NODE);
			
			for(int seekPosition = 0; seekPosition < channel.size(); seekPosition = seekPosition + SIZE_OF_NODE) {
				channel.position(seekPosition);
				channel.read(buffer);
				
				if(pointer == buffer.getLong(POSITION_OFFSET)) {
					x.position = buffer.getLong(POSITION_OFFSET);
					x.parent = buffer.getLong(PARENT_OFFSET);
					x.leafStatus = buffer.getInt(LEAFSTATUS_OFFSET);
					
					for(int keyIndex = 0; keyIndex < x.keys.length; ++keyIndex) 
						x.keys[keyIndex] = buffer.getLong(FIRST_KEY_OFFSET + (keyIndex * LONG_SIZE));
					
					for(int childIndex = 0; childIndex < x.children.length; ++childIndex) 
						x.children[childIndex] = buffer.getLong(FIRST_CHILD_OFFSET + (childIndex * LONG_SIZE));
					
					for(int freqIndex = 0; freqIndex < x.frequency.length; ++freqIndex) {
						x.frequency[freqIndex] = buffer.getInt(FREQUENCY_OFFSET + (freqIndex * INT_SIZE));
					}
					
					return x;
				}
				
				buffer.clear();
					
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void disk_write(Node x) {
		
		if(x.position == 0)
			root = x;
		
		try {
			ByteBuffer byteBuffer =  ByteBuffer.allocate(SIZE_OF_NODE);
			byteBuffer.putLong(x.POSITION_OFFSET, x.position);
			byteBuffer.putLong(x.PARENT_OFFSET, x.parent);
			byteBuffer.putInt(x.LEAFSTATUS_OFFSET, x.leafStatus);
			
			for(int keyIndex = 0; keyIndex < x.keys.length; ++keyIndex)
				byteBuffer.putLong((x.FIRST_KEY_OFFSET + (keyIndex * x.LONG_SIZE)), x.keys[keyIndex]);
			
			for(int childIndex = 0; childIndex < x.children.length; ++childIndex)
				byteBuffer.putLong((x.FIRST_CHILD_OFFSET + (childIndex * x.LONG_SIZE)), x.children[childIndex]);
			
			for(int freqIndex = 0; freqIndex < x.frequency.length; ++freqIndex)
				byteBuffer.putInt((x.FREQUENCY_OFFSET + (freqIndex * x.INT_SIZE)), x.frequency[freqIndex]);
			
				try {	
					file.seek(x.position);
					channel.write(byteBuffer);
				} catch (IOException e) {
					e.printStackTrace();
				}
			 
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
		
	}
	
	public int check_size_of(int [] keys) {
		int count = 0;
		
		for(int key : keys) {
			if(key == NULL)
				break;
			else
				++count;
		}
		
		return count;
	}
	
	public int check_size_of(long [] children) {
		int count = 0;
		
		for(long child : children) {
			if(child == NULL)
				break;
			else
				++count;
		}
		
		return count;
	}
}
