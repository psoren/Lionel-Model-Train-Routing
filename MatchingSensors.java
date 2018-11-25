import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import javafx.beans.value.*;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.*;
import javafx.scene.layout.*;

public class MatchingSensors{

	//This is not working because there is more than one socket
	
	//There are 6 sensors and one switch
	//The command to get all of the connected sensors/tracks:
	//(0x01 - PDI_CMD_ALLGET)
	//(0x04 - ACTION_INFO)
	//D1 01 00 04 FB DF
	//The sensor information that we get back is in the form
	//D1 23 0B 04 11 01 01 72 3A DF
	//in this case sensor 11 (because 0B)

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
	private HashMap<RadioButton, SensorTrack> matchedTracks;

	String selectionAreaStyle = "-fx-border-color: black;" +
			"-fx-border-width: 1;" +
			"-fx-border-style: solid;";

	final double PROGRAM_HEIGHT = 600;
	final double PROGRAM_WIDTH = 1200;

	final double MATCHINGSENSORS_AREA_HEIGHT = PROGRAM_HEIGHT;
	final double MATCHINGSENSORS_AREA_WIDTH = 0.8*PROGRAM_WIDTH;

	static ExecutorService executor;
	static Socket socket = null;
	
	public Scene getScene(Button waypointBtn, ArrayList<Track> tracks) throws Exception{

		matchedTracks = new HashMap<RadioButton, SensorTrack>();

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

		//Whenever the selected radio button changes, this event will be called		
		sensorRadioButtonsToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			public void changed(ObservableValue<? extends Toggle> ov, Toggle toggle, Toggle new_toggle) {
				if(new_toggle != null){					
					System.out.println("The selected sensor was changed");

					//SensorTrack st = matchedTracks.get(new_toggle);

					//					if(st != null){
					//						st.setStyle(selectionAreaStyle);
					//
					//					}	
				}       
			}
		});

		String hostName = "192.168.99.1";
		int portNumber = 50001;

		socket = new Socket(hostName, portNumber);
		
		executor = Executors.newFixedThreadPool(2);

		PingSocketTask pingSocket = new PingSocketTask();
		executor.submit(pingSocket);

		pingSocket.messageProperty().addListener((obs, oldMsg, newMsg) -> {
			System.out.println("message: " + newMsg);
		});

		//Setting up the ScrollPane
		ScrollPane sensorList = new ScrollPane();
		sensorList.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		sensorList.setHbarPolicy(ScrollBarPolicy.NEVER);
		sensorList.setContent(sensorRadioButtonsBox);
		sensorList.setMaxHeight(300);
		sensorList.setMinWidth(300);

		Button getSensorInfoButton = new Button("Get Sensor Info");
		getSensorInfoButton.setOnAction(e->{
			try{
				GetTrainInfoTask sensorInfoTask = new GetTrainInfoTask();

				executor.submit(sensorInfoTask);

				sensorInfoTask.setOnSucceeded((evt) -> {
					System.out.println("sensorInfoTask has succeeded");
					ArrayList<RadioButton> sensorButtons = createSensorButtons(sensorInfoTask.getValue());

					for(RadioButton rb: sensorButtons){
						rb.setToggleGroup(sensorRadioButtonsToggleGroup);
					}

					sensorRadioButtonsBox.getChildren().clear();
					sensorRadioButtonsBox.getChildren().addAll(sensorButtons);
				});	
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


		VBox rightSide = new VBox(20, getSensorInfoButton, sensorList);
		HBox mainMatchingSensors = new HBox(20, matchingSensorsArea, rightSide); 
		VBox vbox = new VBox(topSensorButtons, mainMatchingSensors);
		return new Scene(vbox, PROGRAM_WIDTH, PROGRAM_HEIGHT);		
	}

	private ArrayList<RadioButton> createSensorButtons(String sensorInfo){
		//TODO: finish this
		ArrayList<RadioButton> sensors = new ArrayList<RadioButton>();

		RadioButton sensor1 = new RadioButton("Sensor 11");
		sensor1.setId("11");

		RadioButton sensor2 = new RadioButton("Sensor 12");
		sensor2.setId("12");

		sensors.add(sensor1);
		sensors.add(sensor2);

		return sensors;
	}
}
