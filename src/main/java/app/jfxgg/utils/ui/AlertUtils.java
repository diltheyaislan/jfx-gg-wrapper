package app.jfxgg.utils.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertUtils {

	public static void showAlertError(String message) {
		Alert a = new Alert(AlertType.ERROR);
		a.setContentText(message);
		a.show();
	}
}
