package app.jfxgg;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import app.jfxgg.dto.SettingDTO;
import app.jfxgg.services.SettingService;
import app.jfxgg.services.ShortcutService;
import app.jfxgg.utils.ui.AlertUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class AppController implements Initializable {

	@FXML
	private Label lbFileExecutable;
	
	@FXML
	private TextField tfFileExecutable;
	
	@FXML
	private Label lbHost;
	
	@FXML
	private TextField tfHost;
	
	@FXML
	private Label lbUsername;
	
	@FXML
	private TextField tfUsername;
	
	@FXML
	private Label lbPass;
	
	@FXML
	private PasswordField pfPass;
	
	@FXML
	private TextField tfDefaultApp;
	
	@FXML
	private Label lbStatus;
	
	@FXML
	public void handleChooseExecutable(ActionEvent event) {
		
		Node node = (Node) event.getSource();
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Executable", "*.exe");
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setTitle("Select Go-Global client executable");
		
		File file = fileChooser.showOpenDialog(node.getScene().getWindow());
		if (file != null) {
			tfFileExecutable.setText(file.getAbsolutePath());
		}
	}
	
	@FXML
	public void handleCreateShortcut(ActionEvent event) {

		try {
			ShortcutService.create();
			showStatusMessage("Shortcut created");
		} catch (Exception e) {
			AlertUtils.showAlertError("An error occurred while creating the shortcut");
			e.printStackTrace();
		}
	}
	
	@FXML
	public void handleSave(ActionEvent event) {
		
		if(!validateFields()) {
			return;
		}
		
		String executable = tfFileExecutable.getText().trim();
		String host = tfHost.getText().trim();
		String username = tfUsername.getText().trim();
		String password = pfPass.getText().trim();
		String defaultApp = tfDefaultApp.getText().trim();
		
		SettingDTO dto = SettingDTO.of(executable, host, username, password, defaultApp);
		
		try {
			SettingService.save(dto);
			showStatusMessage("Configuration file saved");
		} catch (Exception e) {
			AlertUtils.showAlertError("An error occurred while saving the configuration file");
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		
		loadSettings();
	}
	
	private void loadSettings() {
		
		try {
			SettingDTO dto = SettingService.load();
			tfFileExecutable.setText(dto.getExecutable());
			tfHost.setText(dto.getHost());
			tfUsername.setText(dto.getUsername());
			tfDefaultApp.setText(dto.getDefaultApp());
		} catch (Exception e) {
			AlertUtils.showAlertError("An error occurred while loading the configuration file");
			e.printStackTrace();
		}
	}
	
	private boolean validateFields() {

		boolean isValidUSername = validateField(tfUsername, lbUsername);
		boolean isValidHost = validateField(tfHost, lbHost);
		boolean isValidFileExecutable = validateField(tfFileExecutable, lbFileExecutable);
		return isValidFileExecutable && isValidHost && isValidUSername;
	}
	
	private boolean validateField(TextField textField, Label label) {
		
		boolean isValid = !textField.getText().isEmpty();
		setValidField(label, isValid);
		if (!isValid) {
			textField.requestFocus();			
		}
		return isValid;
	}
	
	private void setValidField(Label label, boolean isValid) {
		
		label.setTextFill(isValid ? Color.BLACK : Color.RED);
	}
	
	private void showStatusMessage(String message) {
		
		lbStatus.setText(message);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
	        public void run() {
	        	Platform.runLater(() -> {
	    				Platform.runLater(() -> {
	    					lbStatus.setText("");
	    					timer.cancel();
	    				});
	    		});
	        }
	    }, 5000L);
	}
}
