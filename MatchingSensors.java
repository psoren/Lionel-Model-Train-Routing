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

	//The commands to identify sensors
	//You can just click on the sensor button at the top
	//which opens the sensor screen and then click identify
	//need to make sure that we toggle the light, not turn it
	//on or turn it off
	//The command to identify sensor 12: D1 31 0C 06 BD DF
	//The command to identify sensor 15: D1 31 0F 06 BD DF

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

		/***********User Interface Stuff*********************/
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
		/****************************************************/
		
		/***********User Interface Stuff*********************/
		mostRecentCommand = "";
		matchedTracks = new HashMap<RadioButton, Track>();

		//Whenever the selected radio button changes, this event will be called		
		sensorRadioButtonsToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			public void changed(ObservableValue<? extends Toggle> ov, Toggle toggle, Toggle new_toggle) {
				if(new_toggle != null){					
					System.out.println("The selected sensor was changed");
					//SensorTrack st = matchedTracks.get(new_toggle);
					//if(st != null){
					//	st.setStyle(selectionAreaStyle);
					//}	
				}       
			}
		});

		String hostName = "192.168.99.1";
		int portNumber = 50001;

		socket = new Socket(hostName, portNumber);
		executor = Executors.newFixedThreadPool(2);

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
		Button getSensorInfoButton = new Button("Get Sensor Info");
		getSensorInfoButton.setOnAction(e->{
			try{
				GetTrainInfoTask sensorInfoTask = new GetTrainInfoTask();
				executor.submit(sensorInfoTask);
				sensorInfoTask.setOnSucceeded((evt) -> mostRecentCommand = "getInfo");	
			}
			catch(Exception exp){
				exp.printStackTrace();
			}
		});

		//The logic for when a sensor track is clicked
		matchingSensorsArea.setOnMouseClicked(e->{
			Track t = Track.getClickedTrack(new Point2D(e.getX(), e.getY()));

			if(t != null && t instanceof SensorTrack){
				if(sensorRadioButtonsToggleGroup.getSelectedToggle() != null){

					RadioButton selectedButton = (RadioButton)sensorRadioButtonsToggleGroup.getSelectedToggle();
					int buttonID = Integer.parseInt(selectedButton.getId());
					((SensorTrack)t).lionelID = buttonID;
					matchedTracks.put(selectedButton, ((SensorTrack)t));
				}		
			}
		});

		/***************Final User Interface Stuff************/
		VBox rightSide = new VBox(20, getSensorInfoButton, sensorList);
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
