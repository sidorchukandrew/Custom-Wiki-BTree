import java.util.ArrayList;

public class ClusterResult {
	
	private double totalCost;
	private Cluster[] clusters;
	private ArrayList<String> relatedWebsitesOne,relatedWebsitesTwo,relatedWebsitesThree,relatedWebsitesFour,relatedWebsitesFive;
	private String [] medoids; 
	
	public ClusterResult(TreeCacheEntry [] m, Cluster [] c) {
		
		medoids = new String[m.length];
		
		relatedWebsitesOne = new ArrayList<String>();
		relatedWebsitesTwo = new ArrayList<String>();
		relatedWebsitesThree = new ArrayList<String>();
		relatedWebsitesFour = new ArrayList<String>();
		relatedWebsitesFive = new ArrayList<String>();
		
		clusters = c;
		
		for(String website : clusters[0].getRelatedWebsites())
			relatedWebsitesOne.add(website);
		for(String website : clusters[1].getRelatedWebsites())
			relatedWebsitesTwo.add(website);
		for(String website : clusters[2].getRelatedWebsites())
			relatedWebsitesThree.add(website);
		for(String website : clusters[3].getRelatedWebsites())
			relatedWebsitesFour.add(website);
		for(String website : clusters[4].getRelatedWebsites())
			relatedWebsitesFive.add(website);
		
		for(int i = 0; i < m.length; ++i)
			medoids[i] = m[i].getName();
	}
	
	public void addTotalCost() {
		for(Cluster c : clusters) 
			totalCost += c.getCost();
	}
	
	public double getTotalCost() {
		return totalCost;
	}
	
	public Cluster[] getClusters() {
		return clusters;
	}
	
	public ArrayList<String> getClusterOne(){
		return relatedWebsitesOne;
	}
	
	public ArrayList<String> getClusterTwo(){
		return relatedWebsitesTwo;
	}
	
	public ArrayList<String> getClusterThree(){
		return relatedWebsitesThree;
	}
	
	public ArrayList<String> getClusterFour(){
		return relatedWebsitesFour;
	}
	
	public ArrayList<String> getClusterFive(){
		return relatedWebsitesFive;
	}
	
	public String getMedoid(int i) {
		return medoids[i];
	}
}
