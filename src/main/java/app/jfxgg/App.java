package app.jfxgg;

import app.jfxgg.services.ShortcutService;
import app.jfxgg.utils.ui.AlertUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class App extends Application {

	public static final String EXECUTE_ARG = "-e";
	
	@Override
	public void start(Stage primaryStage) {
		
		Parameters params = getParameters();
		if (params.getRaw().stream().anyMatch(a -> a.equalsIgnoreCase(EXECUTE_ARG))) {
			try {
				ShortcutService.execute();
				System.exit(0);
			} catch (Exception e) {
				AlertUtils.showAlertError("An error occurred while call the executable client");
				e.printStackTrace();
			}
			return;
		}
		
		try {
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("/fxml/app/App.fxml"));
			Scene scene = new Scene(root, 600, 330);
			scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
			primaryStage.setTitle("JavaFX Go-Global - Wrapper");
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setResizable(false);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {		
		launch(args);
	}
}
