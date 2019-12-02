import java.util.ArrayList;

public class Cluster {

	private double cost = 0;
	private String medoidName;
	private ArrayList<String> websitesAssociatedWithThisCluster = new ArrayList<String>();
	
	public Cluster(String medoidName) {
		this.medoidName = medoidName;
	}
	
	public void addCost(double c) {
		cost += c;
	}
	
	public double getCost() {
		return cost;
	}
	
	public void add(String name) {
		websitesAssociatedWithThisCluster.add(name);
	}
	
	public void resetCost() {
		cost = 0;
	}
	
	public ArrayList<String> getRelatedWebsites(){
		return websitesAssociatedWithThisCluster;
	}
	
	public void resetRelatedWebsites() {
		websitesAssociatedWithThisCluster = new ArrayList<String>();
	}
}
