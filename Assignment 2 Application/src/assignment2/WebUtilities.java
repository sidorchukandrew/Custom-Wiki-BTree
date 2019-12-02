package assignment2;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import assignment2.model.BTree;
import assignment2.model.Cosine;
import assignment2.model.CosineResult;
import assignment2.model.SimpleNode;
import assignment2.model.TreeEntry;

public class WebUtilities {
	
	static ArrayList<TreeEntry> medoidTrees;
	
	public static void initializeMedoidTrees() {
		
		medoidTrees = new ArrayList<TreeEntry>();
		for(String medoidName : FileParser.getMedoids()) {
			medoidName = medoidName.substring(1);
			medoidTrees.add(new TreeEntry(medoidName, Cosine.read_tree(Cosine.TREES_LOCATION + medoidName)));
		}
	}
	
	public static String scrapeWebsite(String url){
		Document document;
		try 
		{
			document = Jsoup.connect(url).get();
			Elements elements = document.getElementsByTag("body");
			
			String [] wordsFromWebsite = elements.text().split(" ");
			
			return createBTrees(document.title(), wordsFromWebsite);
		}
		
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static String createBTrees(String name, String [] wordsFromWebsite) {
		
		String ADDRESS_OF_USER_TREES = "C:\\Users\\Andrew Sidorchuk\\CSC365 Workspace\\Assignment 2 Application\\UserBTrees\\";
		BTree userTree = new BTree(name, wordsFromWebsite);
		SimpleNode rootOfUser = Cosine.read_tree(ADDRESS_OF_USER_TREES + name);
		
		ArrayList<CosineResult> cosineResults = new ArrayList<CosineResult>();
		
		for(TreeEntry medoid : medoidTrees)
			cosineResults.add(new CosineResult(Cosine.cosine(rootOfUser, medoid.getRoot()), name, medoid.getName()));
		
		CosineResult bestResult = findBest(cosineResults);
		
		return bestResult.getSiteB();
		
	}
	
	private static CosineResult findBest(ArrayList<CosineResult> results) {
		
		CosineResult best = null;
		double cosine = 1;
		for(CosineResult r : results) {
			if(r.getCosine() < cosine) {
				best = r;
				cosine = r.getCosine();
			}
		}
		return best;
	}
			

}
