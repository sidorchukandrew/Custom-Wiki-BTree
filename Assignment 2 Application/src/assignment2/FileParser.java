package assignment2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileParser {
	
	static ArrayList<String> medoids;
	static ArrayList<String> clusterOne;
	static ArrayList<String> clusterTwo;
	static ArrayList<String> clusterThree;
	static ArrayList<String> clusterFour;
	static ArrayList<String> clusterFive;

	public static void parse() {
		
		File file = new File("C:\\Users\\Andrew Sidorchuk\\CSC365 Workspace\\clusters.txt");
		medoids = new ArrayList<String>();
		clusterOne = new ArrayList<String>();
		clusterTwo = new ArrayList<String>();
		clusterThree = new ArrayList<String>();
		clusterFour = new ArrayList<String>();
		clusterFive = new ArrayList<String>();
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line;
			int clusterCount = 0;
			
			while((line = in.readLine()) != null) {
				
				if(line.contains("cluster")) {
					line = line.replace("cluster for", "");
					line = line.replace(":", "");
					medoids.add(line);
					clusterCount++;
				}
				
				else {
					
					if(clusterCount == 1)
						clusterOne.add(line);
					else if(clusterCount == 2)
						clusterTwo.add(line);
					else if(clusterCount == 3)
						clusterThree.add(line);
					else if(clusterCount == 4)
						clusterFour.add(line);
					else if(clusterCount == 5)
						clusterFive.add(line);
				}
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static ArrayList<String> getMedoids(){
		
		return medoids;
	}
	
	public static ArrayList<String> getClusters(String medoid){
		
		int clusterIndex = 0;
		for(int i = 0; i < medoids.size(); ++i) {
			if(medoids.get(i).compareTo(medoid) == 0)
				clusterIndex = i;
		}
		
		if(clusterIndex == 0)
			return clusterOne;
		else if(clusterIndex == 1)
			return clusterTwo;
		else if(clusterIndex == 2)
			return clusterThree;
		else if(clusterIndex == 3)
			return clusterFour;
		else
			return clusterFive;
		
	}
	
	public static String buildWebsiteURL(String websiteName) {
		
		websiteName = websiteName.replace(" - Wikipedia.txt", "");
		websiteName = websiteName.replace(" ", "_");
		System.out.println("https://en.wikipedia.org/wiki/" + websiteName);
		return "https://en.wikipedia.org/wiki/" + websiteName;
	}
	
	public static int getIndexOfMedoid(String medoid) {
		
		int index = 0;
		for(int i = 0; i < medoids.size(); ++i) {
			if(medoid.compareTo(medoids.get(i)) == 0)
				index = i;
		}
		
		return index;
	}
}
