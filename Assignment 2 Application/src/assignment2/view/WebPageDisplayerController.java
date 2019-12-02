package assignment2.view;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import assignment2.FileParser;
import assignment2.Main;
import assignment2.WebUtilities;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class WebPageDisplayerController 
{

	@FXML
	private ListView medoidsList;
	
	@FXML
	private ListView clustersList;
	
	@FXML
	private Button closeAppButton;
	
	@FXML
	private TextField userWebsiteInput;
	
	@FXML
	private Button searchButton;
	
	private Main main;
	
	ObservableList<String> observableMedoids;
	
	ObservableList<String> observableClusters;

	public void setMain(Main main) 
	{
		this.main = main;
		
	}
	
	@FXML
	private void initialize()
	{
		FileParser.parse();
		WebUtilities.initializeMedoidTrees();
		
		observableMedoids = FXCollections.observableArrayList(FileParser.getMedoids());
		medoidsList.setItems(observableMedoids);
		
		medoidsList.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				
				observableClusters = FXCollections.observableArrayList(FileParser.getClusters(medoidsList.getSelectionModel().getSelectedItem().toString()));
				clustersList.setItems(observableClusters);
				
				if(event.getClickCount() == 2) {
					if (Desktop.isDesktopSupported()) {
			            try {
			            	String url = FileParser.buildWebsiteURL(clustersList.getSelectionModel().getSelectedItem().toString());
							Desktop.getDesktop().browse(new URI(url));
						} catch (IOException | URISyntaxException e) {
							e.printStackTrace();
						}
					}
				}
			}

		});
		
		clustersList.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				
				if(event.getClickCount() == 2) {
					if (Desktop.isDesktopSupported()) {
			            try {
			            	String url = FileParser.buildWebsiteURL(clustersList.getSelectionModel().getSelectedItem().toString());
							Desktop.getDesktop().browse(new URI(url));
						} catch (IOException | URISyntaxException e) {
							e.printStackTrace();
						}
					}
				}
				
			}
			
		});
		
		searchButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				String bestMatch = WebUtilities.scrapeWebsite(userWebsiteInput.getText());
				bestMatch = " " + bestMatch;
				observableClusters = FXCollections.observableArrayList(FileParser.getClusters(bestMatch));
				clustersList.setItems(observableClusters);
				medoidsList.getSelectionModel().select(FileParser.getIndexOfMedoid(bestMatch));
			}
		});
	}


	
	@FXML
	private void handleCloseAppPressed(ActionEvent e)
	{
		main.getStage().close();
	}

}
