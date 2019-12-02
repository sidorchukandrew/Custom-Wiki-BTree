import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WebsiteUtilities {
	
	public static final String ADDRESS_PREFIX_FOR_FILES_IN_CACHE = "C:\\Users\\Andrew Sidorchuk\\CSC365 Workspace\\Assignment 2 Loader\\Website Cache\\";
	public static Hashtable <Integer, String> allWebsites = new Hashtable <Integer, String>();
	
	public static void storeWebsitesLocallyInCache() {
		
		File fileContainingWebsiteURLs = new File("websites.txt");

		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(fileContainingWebsiteURLs));
			String contents;
			Document webDocument;
			String websiteURL;
			
			while((websiteURL = fileReader.readLine()) != null) {
				
				allWebsites.put(websiteURL.hashCode(), websiteURL);
				
				webDocument = Jsoup.connect(websiteURL).get();
				contents = webDocument.html();
				
				writeWebsiteToCache(contents, webDocument.title());
				findMoreSites(webDocument);
			}
			
			fileReader.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(allWebsites.size());

	}
	
	private static void writeWebsiteToCache(String contents, String title) {
	
		FileOutputStream fos;
		
		try {
			
			fos = new FileOutputStream(new File(ADDRESS_PREFIX_FOR_FILES_IN_CACHE + title.replace("\\", "") + ".txt"));
			fos.write(contents.getBytes());
			fos.close();
		
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}

	}
	
	private static void findMoreSites(Document webDocument) {
		
		Random r = new Random();

		for(int sitesTotal = 1; sitesTotal <= 19; ++sitesTotal) {
			
			Elements elements = webDocument.select("a[href]");
			String websiteURL = "";
			boolean websiteFound = false;
			
			// Search for a suitable website to read from that hasn't been used already
			while(!websiteFound) {
				
				int randomWebsiteNumber = r.nextInt(elements.size());
				
				if((websiteURL = elements.get(randomWebsiteNumber).attr("abs:href")).matches("https://en\\.wikipedia\\.org/wiki/\\w*")) {
					
					if(!allWebsites.containsKey(websiteURL.hashCode())) {
						
						System.out.println(websiteURL);
						websiteFound = true;
						allWebsites.put(websiteURL.hashCode(), websiteURL);
					}

				}
			}
			
			// Save the newly found website 
			try {
				webDocument = Jsoup.connect(websiteURL).get();
				String contents = webDocument.html();
				
				writeWebsiteToCache(contents, webDocument.title());
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}//end for

	}
	
	public static void createBTreesFromCache() {
		
		// Read from the websites
		
		File folder = new File(ADDRESS_PREFIX_FOR_FILES_IN_CACHE);
		Document document;
		
		try {
			
			int i = 1;
			for(File file : folder.listFiles()) {
				 document = Jsoup.parse(file, "UTF-8");
				 Elements elements = document.getElementsByTag("body");
				 
				BTree tree = new BTree(file.getName(), elements.text().split(" "));
				System.out.println("----------------------------------------------------------------------------------   " + i++);
				break;

			}
		} catch(IOException e) {
			
		}
	}
}
