import java.util.*;
import java.util.concurrent.*;
import javafx.application.*;
import javafx.beans.value.*;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.*;

public class TrainsGUI extends Application{
	
	String selectionAreaStyle = "-fx-border-color: black;" +
			"-fx-border-width: 1;" +
			"-fx-border-style: solid;";

	//The location of the tracks in the layout area
	private ArrayList<Point2D> trackLayoutAreaCoords = new ArrayList<Point2D>();

	public static ArrayList<Track> tracks = new ArrayList<Track>();
	
	//The list of tracks for each train
	private ConcurrentHashMap<Integer, ArrayList<Track>> trainWaypoints = new ConcurrentHashMap<Integer, ArrayList<Track>>();

	//Stuff for the waypoint scene
	Scene trainWaypointScene;
	Scene trackLayoutScene;
	Scene matchingSensorScene;


	ToggleGroup trainRadioButtonsToggleGroup;
	VBox trainRadioButtonsBox;

	Pane waypointArea;
	Pane matchingSensorsArea;
	ToggleGroup sensorRadioButtonsToggleGroup;
	VBox sensorRadioButtonsBox;

	Stage mainStage;

	@Override
	public void start(Stage primaryStage) throws Exception{

		mainStage = primaryStage;

		/************Constants***********/
		final double PROGRAM_HEIGHT = 600;
		final double PROGRAM_WIDTH = 1200;

		final double SELECTION_AREA_WIDTH = (int)0.2*PROGRAM_WIDTH;

		final double WAYPOINT_AREA_HEIGHT = PROGRAM_HEIGHT;
		final double WAYPOINT_AREA_WIDTH = 0.8*PROGRAM_WIDTH;
		/****************************************/

		TrackLayoutSceneCreator trackLayoutSceneGroup = new TrackLayoutSceneCreator();
		
		Button trainWaypointSceneButton = new Button("Train Waypoints");
		trainWaypointSceneButton.setOnAction(e->{
			mainStage.setScene(trainWaypointScene);
			moveTracksToWaypointArea(tracks);
		});

		trackLayoutScene = new Scene(trackLayoutSceneGroup.newTrackLayoutScene(trainWaypointSceneButton, tracks), 
				PROGRAM_WIDTH, PROGRAM_HEIGHT);
		trackLayoutScene.addEventFilter(KeyEvent.KEY_PRESSED, e->{
			if(Track.selected != null){
				if(e.getCode()==KeyCode.LEFT){
					Track.selected.rotateCCW();
				}
				else if(e.getCode()==KeyCode.RIGHT){
					Track.selected.rotateCW();
				}
			}
		});

		/****************************************************/

		/***************Train Waypoint Scene********************/
		Button trackLayoutScreenButton = new Button("Track Layout");
		trackLayoutScreenButton.setOnAction(e->{
			mainStage.setScene(trackLayoutScene);
			trackLayoutSceneGroup.trackLayoutArea.getChildren().clear();

			//Set the location of the tracks to what they were previously	
			for(int i = 0; i < tracks.size(); i++){
				tracks.get(i).setLayoutX(trackLayoutAreaCoords.get(i).getX());
				tracks.get(i).setLayoutY(trackLayoutAreaCoords.get(i).getY());
				trackLayoutSceneGroup.trackLayoutArea.getChildren().add(tracks.get(i));
			}
		});

		Button matchSensorsButton = new Button("Match Sensors");
		matchSensorsButton.setOnAction(e->{
			mainStage.setScene(matchingSensorScene);
		});

		HBox topButtons= new HBox(trackLayoutScreenButton, matchSensorsButton);
		topButtons.setAlignment(Pos.BASELINE_CENTER);

		/**The area where the user can select which train they are adding waypoints for**/
		trainRadioButtonsToggleGroup = new ToggleGroup();
		trainRadioButtonsBox = new VBox(10);

		/**The area where the user can click on and add waypoints**/
		Group waypointAreaGroup = new Group();
		waypointArea = new Pane(waypointAreaGroup);
		waypointArea.setPrefHeight(WAYPOINT_AREA_HEIGHT);
		waypointArea.setPrefWidth(WAYPOINT_AREA_WIDTH);

		//How to add the tracks to the respective train track lists
		waypointArea.setOnMouseClicked(e->{
			Track t = Track.waypointAreaClicked(new Point2D(e.getX(), e.getY()));

			if(t != null){
				//System.out.println("track" + t + " was clicked");
				if(trainRadioButtonsToggleGroup.getSelectedToggle() != null){

					//The train that is selected
					int id = new Integer(((RadioButton)trainRadioButtonsToggleGroup.getSelectedToggle()).getId());

					//If this train already contains this track, do not add it.
					//Otherwise, add it
					ArrayList<Track> selectedTrainTrackList = trainWaypoints.get(id);

					if(!selectedTrainTrackList.contains(t)){
						selectedTrainTrackList.add(t);
					}
					drawCirclesOnTracks(trainWaypoints.get(id));
				}		
			}
		});
		/****************************************************/

		//whenever the selected radio button changes, this event will be called		
		trainRadioButtonsToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			public void changed(ObservableValue<? extends Toggle> ov,
					Toggle toggle, Toggle new_toggle) {
				//Draw the circles on each of the tracks in the tracklist
				if(new_toggle != null){					
					waypointArea.getChildren().clear();
					waypointArea.getChildren().addAll(tracks);
					//System.out.println("the current trains are:" + trainWaypoints);

					int id = new Integer(((RadioButton)trainRadioButtonsToggleGroup.getSelectedToggle()).getId());
					drawCirclesOnTracks(trainWaypoints.get(id));
				}       
			}
		});

		ScrollPane trainWaypointsScrollPane = new ScrollPane();
		trainWaypointsScrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		trainWaypointsScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		trainWaypointsScrollPane.setContent(trainRadioButtonsBox);
		trackLayoutSceneGroup.trackSelectionArea.setMaxHeight(300);
		trackLayoutSceneGroup.trackSelectionArea.setMinWidth(SELECTION_AREA_WIDTH);

		//When clicked, this button will add a new train to the radioButtonGroup
		Button addTrainButton = new Button("Add Train");
		addTrainButton.setOnAction(e->{
			int trainNumber = trainRadioButtonsToggleGroup.getToggles().size();

			//The train radio button
			RadioButton newTrainRadioButton = new RadioButton("Train " + trainNumber);
			newTrainRadioButton.setToggleGroup(trainRadioButtonsToggleGroup);
			newTrainRadioButton.setId(Integer.toString(trainNumber));

			newTrainRadioButton.setStyle(generateRandomColor());

			newTrainRadioButton.setOnAction(radioBtnEvt->{
				//System.out.println("newtrainradiobutton "+ trainNumber +"was clicked");
			});

			ArrayList<Track> waypointTracks = new ArrayList<Track>();
			trainWaypoints.put(trainNumber, waypointTracks);

			//The associated delete train button
			Button deleteTrainButton = new Button("X");
			deleteTrainButton.setId(Integer.toString(trainNumber));

			//When the delete button is clicked
			deleteTrainButton.setOnAction(deleteEvent->{

				//Remove the specified toggle
				for(Toggle toggle: trainRadioButtonsToggleGroup.getToggles()){
					RadioButton button = (RadioButton)toggle;
					if(button.getId().equals(deleteTrainButton.getId())){
						trainRadioButtonsToggleGroup.getToggles().remove(button);
						break;
					}
				}

				//Remove the specified train from the VBox
				for(Node node: trainRadioButtonsBox.getChildren()){
					if(node.getId().equals(deleteTrainButton.getId())){
						trainRadioButtonsBox.getChildren().remove(node);
						break;
					}
				}

				//Go through and reset IDs of HBoxes and deleteTrainButtons
				int newId = 0;
				for(Node node: trainRadioButtonsBox.getChildren()){
					HBox trainBox = (HBox)node;
					trainBox.setId(Integer.toString(newId));
					for(Node buttonBox: trainBox.getChildren()){
						buttonBox.setId(Integer.toString(newId));
						if(buttonBox instanceof RadioButton){
							((RadioButton)buttonBox).setText("Train " + newId);
						}
					}
					newId++;
				}

				//Shift the indices of trainWaypoints down by 1
				int indexToRemove = new Integer(deleteTrainButton.getId());
				//System.out.println("index of train to remove: " + indexOfTrainToRemove);

				//Remove the track at the specified index
				trainWaypoints.remove(indexToRemove);

				//We now need to re-index trainWaypoints so that
				//the indices are 0,1,2,3,... as in the rest
				//of the program				
				Iterator<Integer> iterator = trainWaypoints.keySet().iterator();
				while(iterator.hasNext()){
					int index = iterator.next();
					if(index > indexToRemove){						
						trainWaypoints.put(index-1, trainWaypoints.get(index));
					}
				}
			});

			HBox trainBox = new HBox(10, newTrainRadioButton, deleteTrainButton);
			trainBox.setId(Integer.toString(trainNumber));

			//trainBox.setStyle(generateRandomColor());
			trainBox.setId(Integer.toString(trainNumber));

			//Add new radio button to VBox
			trainRadioButtonsBox.getChildren().add(trainBox);
		});

		VBox trainPickArea = new VBox(20, addTrainButton, trainWaypointsScrollPane);
		trainPickArea.setMinWidth(200);
		trainPickArea.setStyle(selectionAreaStyle);

		//The main HBox
		HBox mainBottomArea = new HBox(waypointArea, trainPickArea);
		VBox scene2Main = new VBox(topButtons, mainBottomArea);

		trainWaypointScene = new Scene(scene2Main,PROGRAM_WIDTH, PROGRAM_HEIGHT);
		/****************************************************/








		/***************Matching Sensor Scene Stuff**********************/

		//The button to go back to the train waypoint scene
		Button matchingSensorsToTrainWaypointButton = new Button("Train Waypoints");
		matchingSensorsToTrainWaypointButton.setOnAction(e->{
			mainStage.setScene(trainWaypointScene);
		});
		HBox topSensorButtons= new HBox(matchingSensorsToTrainWaypointButton);
		topSensorButtons.setAlignment(Pos.BASELINE_CENTER);



		/**The area where the user can match sensors**/
		Group matchingSensorsGroup = new Group();
		matchingSensorsArea = new Pane(matchingSensorsGroup);
		matchingSensorsArea.setPrefHeight(WAYPOINT_AREA_HEIGHT);
		matchingSensorsArea.setPrefWidth(WAYPOINT_AREA_WIDTH);
		/******************************************************/




		/**The list of sensors that you can click on to match up the sensors**/
		sensorRadioButtonsToggleGroup = new ToggleGroup();
		sensorRadioButtonsBox = new VBox(10);

		//Whenever the selected radio button changes, this event will be called		
		sensorRadioButtonsToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			public void changed(ObservableValue<? extends Toggle> ov, Toggle toggle, Toggle new_toggle) {
				if(new_toggle != null){					
					System.out.println("The selected sensor was changed");
				}       
			}
		});

		//Finish setting up this layout and make sure we can send sensor stuff to the wifi module

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
		//The command to identify sensor 12: D1 31 0C 06 BD DF
		//The command to identify sensor 15: D1 31 0F 06 BD DF

		ScrollPane sensorList = new ScrollPane();
		sensorList.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		sensorList.setHbarPolicy(ScrollBarPolicy.NEVER);
		sensorList.setContent(sensorRadioButtonsBox);
		sensorList.setMaxHeight(300);
		sensorList.setMinWidth(SELECTION_AREA_WIDTH);










		//Final setup stuff
		HBox mainMatchingSensors = new HBox(20, matchingSensorsArea, sensorList); 
		VBox matchingSensorMainHBox = new VBox(topSensorButtons, mainMatchingSensors);
		matchingSensorScene = new Scene(matchingSensorMainHBox, PROGRAM_WIDTH, PROGRAM_HEIGHT);
		/******************************************************/

























		/***************Final Stage Stuff**********************/
		mainStage.setTitle("Trains");
		mainStage.setScene(trackLayoutScene);
		mainStage.show();
		/******************************************************/
	}

	//A method to generate a random color and return the css string
	private String generateRandomColor(){
		int r = (int)(Math.random()*255);
		int g = (int)(Math.random()*255);
		int b = (int)(Math.random()*255);
		return "-fx-background-color: rgb("+r+","+g+","+b+");";
	}

	//A method to draw the correctly colored circles on tracks
	private void drawCirclesOnTracks(ArrayList<Track> ts){
		int counter = 1;
		for(Track t: ts){
			String buttonStyle = ((RadioButton)trainRadioButtonsToggleGroup.getSelectedToggle()).getStyle();
			Circle circ = new Circle(t.getLayoutX() + t.getWidth()/2, t.getLayoutY() + t.getHeight()/2, 20);
			circ.setStyle("-fx-fill: " + buttonStyle.substring(22));
			Text num = new Text(t.getLayoutX() + t.getWidth()/2, t.getLayoutY() + t.getHeight()/2, Integer.toString(counter));
			String textStyle = "-fx-font: 20px Arial; -fx-stroke: white; -fx-stroke-width: 3;";
			num.setStyle(textStyle);
			waypointArea.getChildren().addAll(circ, num);
			counter++;
		}
	}

	private void moveTracksToMatchingSensorsArea(ArrayList<Track> tracks){
		for(Track t: tracks){
			matchingSensorsArea.getChildren().add(t);
		}
	}

	private void moveTracksToWaypointArea(ArrayList<Track> tracks){
		//Save location of tracks in layout area
		//before moving to waypoint area
		trackLayoutAreaCoords.clear();
		waypointArea.getChildren().clear();
		for(Track t: tracks){
			trackLayoutAreaCoords.add(new Point2D(t.getLayoutX(), t.getLayoutY()));
			waypointArea.getChildren().add(t);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
