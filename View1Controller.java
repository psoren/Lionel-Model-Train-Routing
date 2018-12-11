package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller class for the first vista.
 */
public class View1Controller {

	/**
	 * Event handler fired when the user requests a new vista.
	 *
	 * @param event the event that triggered the handler.
	 */

	@FXML
	public GridPane gp;
	@FXML
	public Button btnCreate;
	@FXML
	public Button btnLoad;
	@FXML
	public Button btnSettings;
	@FXML
	public Label wifiConnected; 

	@FXML
	public void nextPane(ActionEvent event) {
		ViewNavigator.loadView(ViewNavigator.VIEW_2);
	}

	public String getSSID() throws IOException {
		String ssid = null;
		ProcessBuilder build = new ProcessBuilder("cmd.exe", "/c", "netsh wlan show all");
		build.redirectErrorStream(true);
		Process proc = build.start();
		BufferedReader buff = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		while (buff.read() != -1) {
			String line = buff.readLine();
			if (line.contains("SSID")) 
			{
				if (!line.contains("BSSID") && !line.contains("SSIDs") && !line.contains("name")) 
				{
					ssid = line.substring(28);
				}
			}
		}
		return ssid;
	}
	
	public void refresh() throws IOException {
		String connection = getSSID();
		if (!connection.contains("Lionel")) {
			connection += " (PLEASE CONNECT TO A LIONEL WIFI SOURCE)";
		}
		wifiConnected.setText(connection);
	}

	@FXML
	public void initialize() {
		try {
			refresh();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}