package assignment2.model;

public class CosineResult {

	double cosine;
	String siteA, siteB;
	
	public CosineResult(double cosine, String a, String b) {
		this.cosine = cosine;
		siteA = a;
		siteB = b;
	}
	
	public String getSiteA() {
		return siteA;
	}
	
	public String getSiteB() {
		return siteB;	
	}
	
	public double getCosine() {
		return cosine;
	}
}