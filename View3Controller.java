package application;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import application.View2Controller;

public class View3Controller {

	@FXML
	public Pane matchingSensorsArea;
	private ToggleGroup radioButtonsToggleGroup;
	@FXML
	private VBox radioButtonsBox;
	
	private HashMap<RadioButton, Track> matchedTracks = new HashMap<RadioButton, Track>();
	
	String unMatchedStyle = "-fx-background-color: rgb(255,0,0);";
	String matchedStyle = "-fx-background-color: rgb(0,255,0);";
	
	@FXML
	public Button waypointBtn;
	@FXML
	public Button getSysInfoBtn;
	@FXML
	public Button trainRunBtn;
	
	@FXML
	void previousPane(ActionEvent event) {
		ViewNavigator.loadView(ViewNavigator.VIEW_2);
	}
	
	@FXML
	public void trainWaypoints(ActionEvent event) {
		ViewNavigator.loadView(ViewNavigator.VIEW_22);
	}
	
	@FXML
	public void nextPane(ActionEvent event) {
		ViewNavigator.loadView(ViewNavigator.VIEW_4);
	}
	
	@FXML
	public void initialize() {
	
		radioButtonsToggleGroup = new ToggleGroup();

		radioButtonsToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			public void changed(ObservableValue<? extends Toggle> ov, Toggle toggle, Toggle new_toggle) {
				if(new_toggle != null){	

					for(Track t: View2Controller.tracks){
						t.setStyle(Track.unselectedStyle);
					}

					RadioButton newButton = (RadioButton)new_toggle;

					//Step 1: Light up corresponding track
					//The button is a sensor button
					if(newButton.getId().startsWith("sensor")){
						IdentifySensorTask identifySensorTask = new IdentifySensorTask(newButton.getId());						
						SocketCommunication.executor.submit(identifySensorTask);
						//identifySensorTask.setOnFailed(e-> System.out.println("Identify sensor task failed"));
						//identifySensorTask.setOnSucceeded(e-> System.out.println("Identify sensor task succeeded"));
					}

					//The button is a switch button
					else{						
						IdentifySwitchTask identifySwitchTask = new IdentifySwitchTask(newButton.getId());
						SocketCommunication.executor.submit(identifySwitchTask);
						//identifySwitchTask.setOnFailed(e-> System.out.println("Identify switch task failed"));
						//identifySwitchTask.setOnSucceeded(e-> System.out.println("Identify switch task succeeded"));
					}

					//Step 2: The user clicks on that track
					Track selectedTrack = matchedTracks.get(newButton);					

					//This track has already been matched
					if(selectedTrack != null){
						//Display this track as selected
						selectedTrack.setStyle(Track.selectedStyle);
						Track.selected = selectedTrack;
					}

					//This sensor or switch track has not already been matched
					else{
						//If there is a selected track, unselect it
						if(Track.selected != null){
							Track.selected.setStyle(Track.unselectedStyle);
							Track.selected = null;
						}
					}	
				}       
			}
		});

		//When clicked, this button will generate a list of buttons based
		//on the configuration of the track
		getSysInfoBtn.setOnAction(e->{
			try{
				GetTrainInfoTask trainInfoTask = new GetTrainInfoTask();
				SocketCommunication.executor.submit(trainInfoTask);
				trainInfoTask.setOnSucceeded((evt) -> {
					SocketCommunication.mostRecentCommand = "getInfo";
				});	
			}
			catch(Exception exp){
				exp.printStackTrace();
			}
		});

		//The logic for when a sensor track is clicked
		matchingSensorsArea.setOnMouseClicked(e->{
			Track t = Track.getClickedTrack(new Point2D(e.getX(), e.getY()));

			//There was not a track under the button click
			if(t == null && Track.selected != null){
				Track.selected.setStyle(Track.unselectedStyle);
				Track.selected = null;
			}

			//If this track has not already been matched to by one of the radio buttons
			if(!matchedTracks.containsValue(t)){
				//If the clicked track is a sensor track
				if(t != null && t instanceof SensorTrack){
					if(radioButtonsToggleGroup.getSelectedToggle() != null){

						RadioButton selectedButton = (RadioButton)radioButtonsToggleGroup.getSelectedToggle();
						String buttonID = selectedButton.getId();

						//Make sure that the button is 
						//actually a sensor button
						if(buttonID.startsWith("sensor")){
							((SensorTrack)t).lionelID = buttonID;
							matchedTracks.put(selectedButton, t);
							selectedButton.setStyle(matchedStyle);
						}
					}		
				}

				//If the clicked track is a SwitchRightTrack
				if(t != null && t instanceof SwitchRightTrack){
					if(radioButtonsToggleGroup.getSelectedToggle() != null){

						RadioButton selectedButton = (RadioButton)radioButtonsToggleGroup.getSelectedToggle();
						String buttonID = selectedButton.getId();

						//Make sure that the button is 
						//actually a switch button
						if(buttonID.startsWith("switch")){
							((SwitchRightTrack)t).lionelID = buttonID;
							matchedTracks.put(selectedButton, t);
							selectedButton.setStyle(matchedStyle);
						}
					}		
				}

				//If the clicked track is a SwitchLeftTrack
				if(t != null && t instanceof SwitchLeftTrack){
					if(radioButtonsToggleGroup.getSelectedToggle() != null){

						RadioButton selectedButton = (RadioButton)radioButtonsToggleGroup.getSelectedToggle();
						String buttonID = selectedButton.getId();

						//Make sure that the button is 
						//actually a switch button
						if(buttonID.startsWith("switch")){
							((SwitchLeftTrack)t).lionelID = buttonID;
							matchedTracks.put(selectedButton, t);
							selectedButton.setStyle(matchedStyle);
						}
					}		
				}
			}
		});
	}
}
