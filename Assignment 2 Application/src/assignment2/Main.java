package assignment2;

import java.io.IOException;
import assignment2.view.WebPageDisplayerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

	private BorderPane root;
	private static Stage stage;

	@Override
	public void start(Stage primaryStage) 
	{
		stage = primaryStage;
		
		try 
		{
			root = (BorderPane)FXMLLoader.load(getClass().getResource("view/RootView.fxml"));
			Scene scene = new Scene(root,1000,700);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.initStyle(StageStyle.UNDECORATED);
			primaryStage.setScene(scene);
			primaryStage.show();
			
			showWebPageDisplayerView();
		} 
		
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public void showWebPageDisplayerView()
	{
		try
		{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/WebPageDisplayer.fxml"));
			AnchorPane webPageDisplayer = (AnchorPane) loader.load();
			
			root.setCenter(webPageDisplayer);
			WebPageDisplayerController controller = loader.getController();
			controller.setMain(this);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public Stage getStage() {
		
		return stage;
	}
}
