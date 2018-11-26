import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import javafx.beans.value.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.*;
import javafx.scene.layout.*;

public class MatchingSensors{

	public Pane matchingSensorsArea;
	private ToggleGroup sensorRadioButtonsToggleGroup;
	private VBox sensorRadioButtonsBox;
	private HashMap<RadioButton, Track> matchedTracks;

	String selectionAreaStyle = "-fx-border-color: black;" +
			"-fx-border-width: 1;" +
			"-fx-border-style: solid;";
	String unMatchedStyle = "-fx-background-color: rgb(255,0,0);";
	String matchedStyle = "-fx-background-color: rgb(0,255,0);";

	final double PROGRAM_HEIGHT = 600;
	final double PROGRAM_WIDTH = 1200;

	final double MATCHINGSENSORS_AREA_HEIGHT = PROGRAM_HEIGHT;
	final double MATCHINGSENSORS_AREA_WIDTH = 0.8*PROGRAM_WIDTH;

	static ExecutorService executor;
	static Socket socket = null;

	String mostRecentCommand;

	public Scene getScene(Button waypointBtn, ArrayList<Track> tracks) throws Exception{

		/***********General Setup******************************/
		HBox topSensorButtons= new HBox(waypointBtn);
		topSensorButtons.setAlignment(Pos.BASELINE_CENTER);

		/**The area where the user can match sensors**/
		Group matchingSensorsGroup = new Group();
		matchingSensorsArea = new Pane(matchingSensorsGroup);
		matchingSensorsArea.setPrefHeight(MATCHINGSENSORS_AREA_HEIGHT);
		matchingSensorsArea.setPrefWidth(MATCHINGSENSORS_AREA_WIDTH);
		/******************************************************/

		/**The list of sensors that you can click on to match up the sensors**/
		sensorRadioButtonsToggleGroup = new ToggleGroup();
		sensorRadioButtonsBox = new VBox(10);

		//Setting up the ScrollPane
		ScrollPane sensorList = new ScrollPane();
		sensorList.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		sensorList.setHbarPolicy(ScrollBarPolicy.NEVER);
		sensorList.setContent(sensorRadioButtonsBox);
		sensorList.setMaxHeight(300);
		sensorList.setMinWidth(300);
		sensorList.setContent(sensorRadioButtonsBox);

		String hostName = "192.168.99.1";
		int portNumber = 50001;

		socket = new Socket(hostName, portNumber);
		executor = Executors.newFixedThreadPool(4);
		/****************************************************/

		/***********User Interface Stuff*********************/
		mostRecentCommand = "";
		matchedTracks = new HashMap<RadioButton, Track>();

		//Whenever the selected radio button changes, this event will be called	
		sensorRadioButtonsToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			public void changed(ObservableValue<? extends Toggle> ov, Toggle toggle, Toggle new_toggle) {
				if(new_toggle != null){		

					RadioButton newButton = (RadioButton)new_toggle;

					//Step 1: Light up corresponding track
					//The button is a sensor button
					if(newButton.getId().startsWith("sensor")){
						IdentifySensorTask identifySensorTask = new IdentifySensorTask(newButton.getId());						
						executor.submit(identifySensorTask);
						//identifySensorTask.setOnFailed(e-> System.out.println("Identify sensor task failed"));
						//identifySensorTask.setOnSucceeded(e-> System.out.println("Identify sensor task succeeded"));
					}

					//The button is a switch button
					else{						
						IdentifySwitchTask identifySwitchTask = new IdentifySwitchTask(newButton.getId());
						executor.submit(identifySwitchTask);
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

		//Constantly pinging the sensor and getting input back
		PingSocketTask pingSocket = new PingSocketTask();
		executor.submit(pingSocket);

		//This will fire whenever the message property of the pingSocketTask changes
		//which only happens when we send a command to the sensor
		pingSocket.messageProperty().addListener((obs, oldMsg, newMsg) -> {
			String[] messages = newMsg.split(" ");

			if(mostRecentCommand == "getInfo"){
				ArrayList<RadioButton> sensorButtons = createSensorButtons(messages);

				for(RadioButton rb: sensorButtons){
					rb.setToggleGroup(sensorRadioButtonsToggleGroup);
				}
				sensorRadioButtonsBox.getChildren().clear();
				sensorRadioButtonsBox.getChildren().addAll(sensorButtons);
				mostRecentCommand = "";
			}
		});

		//When clicked, this button will generate a list of buttons based
		//on the configuration of the track
		Button getSystemInfoButton = new Button("Get System Info");
		getSystemInfoButton.setOnAction(e->{
			try{
				GetTrainInfoTask trainInfoTask = new GetTrainInfoTask();
				executor.submit(trainInfoTask);
				trainInfoTask.setOnSucceeded((evt) -> {
					//System.out.println("The sensorInfoTask has succeeded");
					mostRecentCommand = "getInfo";
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
					if(sensorRadioButtonsToggleGroup.getSelectedToggle() != null){

						RadioButton selectedButton = (RadioButton)sensorRadioButtonsToggleGroup.getSelectedToggle();
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
					if(sensorRadioButtonsToggleGroup.getSelectedToggle() != null){

						RadioButton selectedButton = (RadioButton)sensorRadioButtonsToggleGroup.getSelectedToggle();
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
					if(sensorRadioButtonsToggleGroup.getSelectedToggle() != null){

						RadioButton selectedButton = (RadioButton)sensorRadioButtonsToggleGroup.getSelectedToggle();
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

		/***************Final User Interface Stuff************/
		VBox rightSide = new VBox(20, getSystemInfoButton, sensorList);
		HBox mainMatchingSensors = new HBox(20, matchingSensorsArea, rightSide); 
		VBox vbox = new VBox(topSensorButtons, mainMatchingSensors);
		return new Scene(vbox, PROGRAM_WIDTH, PROGRAM_HEIGHT);	
		/*****************************************************/
	}

	//The method that creates the sensor and switch buttons based off of the information
	//from the Wifi module.  It takes in an array of strings based 
	//on what the Wifi module returned
	private ArrayList<RadioButton> createSensorButtons(String[] sensorInfo){
		ArrayList<RadioButton> sensors = new ArrayList<RadioButton>();

		for(String s: sensorInfo){
			//It is a sensor
			if(s.startsWith("D132")){
				String sensorNumber = Integer.toString(Integer.parseInt(s.substring(4,6), 16));
				String sensorName = "Sensor " + sensorNumber;
				RadioButton rb = new RadioButton(sensorName);
				rb.setId("sensor" + sensorNumber);
				rb.setStyle(unMatchedStyle);
				sensors.add(rb);
			}

			//It is a switch
			if(s.startsWith("D13E")){
				String switchNumber = Integer.toString(Integer.parseInt(s.substring(4,6), 16));
				String switchName = "Switch " + switchNumber;
				RadioButton rb = new RadioButton(switchName);
				rb.setId("switch" + switchNumber);
				rb.setStyle(unMatchedStyle);
				sensors.add(rb);	
			}
		}
		return sensors;
	}
}
