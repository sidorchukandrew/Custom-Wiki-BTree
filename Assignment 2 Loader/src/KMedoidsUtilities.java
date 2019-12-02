import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class KMedoidsUtilities {
	
	double totalCost;
	
	static String [] initialMedoids = {"Mammillaria spinosissima - Wikipedia.txt",
										"Cat - Wikipedia.txt",
										"House - Wikipedia.txt",
										"Person - Wikipedia.txt",
										"Duke of Bedford - Wikipedia.txt"};
	static TreeCacheEntry [] medoids; 
	static final int K = 5;
	static Cluster [] clusters = new Cluster[K];
	static final int NON_MEDOID_COUNT = 93;
	static ClusterResult bestClusterCost;
	static String nextRandomMedoid;
	static final int NUMBER_OF_SWAPS_BOUND = 1000;
	
	public static void initializeMedoids(LRUCache cache) {									//Step 1, initialize the medoids and K
		
		medoids = new TreeCacheEntry[K];
		
		for(int i = 0; i < medoids.length; ++i) {
			medoids[i] = cache.get(initialMedoids[i].hashCode()); 
			clusters[i] = new Cluster(medoids[i].getName());
		}
		
		computeSimilarities(cache);															//Step 2, compute first round of similarities	
		setNewMedoids(cache);																//Step 3, set a new medoid and perform again
		
		for(int swapCount = 1; swapCount < NUMBER_OF_SWAPS_BOUND; ++swapCount) {
			computeSimilarities(cache);															
			setNewMedoids(cache);
		}
		
		writeClustersToFile();
	}
	
	
//							 ______________________________________________________________________
// 			 				| Cosine with M1 |   C w/ M2   |  C w/ M3   |  C w/ M4   |   C w/ M5   |
//	Non-Medoid Website 1	| - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -|
//			 		   2	|________________|_____________|____________|____________|_____________|
//					   3	|________________|_____________|____________|____________|_____________|
//			 		   4	|________________|_____________|____________|____________|_____________|
//			 		   .	|________________|_____________|____________|____________|_____________|
//			 		   .	|________________|_____________|____________|____________|_____________|
//					   . 	|________________|_____________|____________|____________|_____________|
//
	
	public static void computeSimilarities(LRUCache cache) {
		
		double similaritiesTable [] [] = new double [NON_MEDOID_COUNT] [K];

		Set set = cache.getMap().entrySet();
		Iterator iterator = set.iterator();
		Random random = new Random();
		
		int randomNumber = random.nextInt(93);
		int row = 0;
		
		// Iterate over each website tree that's not a medoid
		while(iterator.hasNext()) {	
			Map.Entry me =(Map.Entry)iterator.next();
			TreeCacheEntry tree = (TreeCacheEntry)me.getValue();
			
			//Initialize
			double smallestCosine = 1;
			int smallestCosineIndex = 0;
			
			if(!isAMedoid(tree.getName())) {
				
				// Compare the tree to each medoid
				for(int column = 0; column < K; ++column){
					similaritiesTable[row][column] = BTreeUtilities.cosine(medoids[column].getRoot(), tree.getRoot());
					
					// Keep track of which medoid has the smallest cosine/distance
					if(similaritiesTable[row][column] < smallestCosine) {
						smallestCosine = similaritiesTable[row][column];
						smallestCosineIndex = column;
					}
				}
				
				if(randomNumber == row)
					nextRandomMedoid = tree.getName();
				
				// Cumulate 
				clusters[smallestCosineIndex].addCost(smallestCosine);
				clusters[smallestCosineIndex].add(tree.getName());
				row++;
			}
		}
		gatherResults();
	}
	
	private static void gatherResults() {
		
		// Compute the cost total
		ClusterResult newResult = new ClusterResult(medoids, clusters);
		newResult.addTotalCost();
		
		// Keep track of only the clusters with the lowest cost
		if(bestClusterCost == null)
			bestClusterCost = newResult;
		else if(bestClusterCost.getTotalCost() > newResult.getTotalCost())
			bestClusterCost = newResult;

	}
	
	private static boolean isAMedoid(String name) {
		
		for(int medoidIndex = 0; medoidIndex < K; medoidIndex++) {
			
			if(medoids[medoidIndex].getName().compareTo(name) == 0)
				return true;
		}
		
		return false;
	}
	
	private static void setNewMedoids(LRUCache cache) {
		
		// We need to change one medoid and perform the whole thing again
		// First pick which nonmedoid is becoming a new medoid
		TreeCacheEntry newMedoid = cache.get(nextRandomMedoid.hashCode());
		
		// Second, randomly select which medoid to replace
		Random r = new Random();
		int randomNumber = r.nextInt(K);
		medoids[randomNumber] = newMedoid;
		
		// Reinitialize the clusters
		for(int i = 0; i < clusters.length; ++i) {
			clusters[i].resetCost();
			clusters[i].resetRelatedWebsites();
		}
		
		clusters[randomNumber] = new Cluster(medoids[randomNumber].getName());
	}
	
	private static void writeClustersToFile() {
		
		File file = new File("C:\\Users\\Andrew Sidorchuk\\CSC365 Workspace\\clusters.txt");
		try {
			FileOutputStream fos = new FileOutputStream(file);

				fos.write(("cluster for " + bestClusterCost.getMedoid(0) + ":\n").getBytes());
				for(String website : bestClusterCost.getClusterOne())
					fos.write(new String(website + "\n").getBytes());
				
				fos.write(("\ncluster for " + bestClusterCost.getMedoid(1) + ":\n").getBytes());
				for(String website : bestClusterCost.getClusterTwo())
					fos.write(new String(website + "\n").getBytes());
				
				fos.write(("\ncluster for " + bestClusterCost.getMedoid(2) + ":\n").getBytes());
				for(String website : bestClusterCost.getClusterThree())
					fos.write(new String(website + "\n").getBytes());
				
				fos.write(("\ncluster for " + bestClusterCost.getMedoid(3) + ":\n").getBytes());
				for(String website : bestClusterCost.getClusterFour())
					fos.write(new String(website + "\n").getBytes());
				
				fos.write(("\ncluster for " + bestClusterCost.getMedoid(4) + ":\n").getBytes());
				for(String website : bestClusterCost.getClusterFive())
					fos.write(new String(website + "\n").getBytes());
			
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
