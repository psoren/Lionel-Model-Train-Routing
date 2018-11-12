import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.*;

public class TrainsGUI extends Application{

	public static ArrayList<Track> tracks = new ArrayList<Track>();

	//The location of the tracks in the layout area
	private ArrayList<Point2D> trackLayoutAreaCoords = new ArrayList<Point2D>();

	//The list of tracks for each train
	private ConcurrentHashMap<Integer, ArrayList<Track>> trainWaypoints = new ConcurrentHashMap<Integer, ArrayList<Track>>();

	//Stuff for the waypoint scene
	Scene trainWaypointScene;
	Scene trackLayoutScene;
	ToggleGroup trainRadioButtonsToggleGroup;
	VBox trainRadioButtonsBox;
	Pane waypointArea;

	@Override
	public void start(Stage primaryStage) throws Exception{

		/************Constants***********/
		final double PROGRAM_HEIGHT = 600;
		final double PROGRAM_WIDTH = 1200;

		final double SELECTION_AREA_HEIGHT = PROGRAM_HEIGHT;
		final double SELECTION_AREA_WIDTH = (int)0.2*PROGRAM_WIDTH;

		final double LAYOUT_AREA_HEIGHT = PROGRAM_HEIGHT;
		final double LAYOUT_AREA_WIDTH = 0.8*PROGRAM_WIDTH;

		final double WAYPOINT_AREA_HEIGHT = PROGRAM_HEIGHT;
		final double WAYPOINT_AREA_WIDTH = 0.8*PROGRAM_WIDTH;

		final String BASE = "https://raw.githubusercontent.com/psoren/TrainsGUI/master/assets/";

		final String STRAIGHT_IMG = "straight.png";
		final String SENSOR_IMG = "sensor.png";
		final String SWITCHRIGHT_IMG = "switchRight.png";
		final String SWITCHLEFT_IMG = "switchLeft.png";
		final String CURVERIGHT_IMG = "curveRight.png";
		final String CURVELEFT_IMG = "curveLeft.png";
		/****************************************/


		/************Track Layout Area***********/
		Group group = new Group();
		Pane trackLayoutArea = new Pane(group);

		trackLayoutArea.setOnMouseClicked(e->{
			Track.layoutAreaClicked(new Point2D(e.getX(), e.getY()));
		});

		trackLayoutArea.setPrefHeight(LAYOUT_AREA_HEIGHT);
		trackLayoutArea.setPrefWidth(LAYOUT_AREA_WIDTH);
		/****************************************/


		/************Track Selection Area***********/
		Image straight = new Image(BASE + STRAIGHT_IMG);		
		ImageView straightImgVw = new ImageView(straight);

		straightImgVw.setFitHeight(50);
		straightImgVw.setFitWidth(100);

		Image sensor = new Image(BASE + SENSOR_IMG);
		ImageView sensorImgVw = new ImageView(sensor);
		sensorImgVw.setFitHeight(55);
		sensorImgVw.setFitWidth(50);

		Image switchRight = new Image(BASE + SWITCHRIGHT_IMG);
		ImageView switchRightVw = new ImageView(switchRight);
		switchRightVw.setFitHeight(60);
		switchRightVw.setFitWidth(100);

		Image switchLeft = new Image(BASE + SWITCHLEFT_IMG);
		ImageView switchLeftVw = new ImageView(switchLeft);
		switchLeftVw.setFitHeight(60);
		switchLeftVw.setFitWidth(100);

		Image curveRight = new Image(BASE + CURVERIGHT_IMG);
		ImageView curveRightVw = new ImageView(curveRight);
		curveRightVw.setFitHeight(60);
		curveRightVw.setFitWidth(100);

		Image curveLeft = new Image(BASE + CURVELEFT_IMG);
		ImageView curveLeftVw = new ImageView(curveLeft);
		curveLeftVw.setFitHeight(60);
		curveLeftVw.setFitWidth(100);

		/****************************************/


		/************Drag-and-drop implementation***********/
		//Define drag and drop controls from the each imageView to the selection area
		straightImgVw.setOnDragDetected(e->{			
			Dragboard db = straightImgVw.startDragAndDrop(TransferMode.ANY);
			ClipboardContent content = new ClipboardContent();
			content.putImage(straight);
			content.putString("straight");
			db.setContent(content);
			e.consume();
		});

		sensorImgVw.setOnDragDetected(e->{
			Dragboard db = sensorImgVw.startDragAndDrop(TransferMode.ANY);
			ClipboardContent content = new ClipboardContent();
			content.putImage(sensor);
			content.putString("sensor");
			db.setContent(content);
			e.consume();
		});

		switchRightVw.setOnDragDetected(e->{
			Dragboard db = switchRightVw.startDragAndDrop(TransferMode.ANY);
			ClipboardContent content = new ClipboardContent();
			content.putImage(switchRight);
			content.putString("switchRight");
			db.setContent(content);
			e.consume();
		});

		switchLeftVw.setOnDragDetected(e->{
			Dragboard db = switchLeftVw.startDragAndDrop(TransferMode.ANY);
			ClipboardContent content = new ClipboardContent();
			content.putImage(switchLeft);
			content.putString("switchLeft");
			db.setContent(content);
			e.consume();
		});

		curveRightVw.setOnDragDetected(e->{
			Dragboard db = curveRightVw.startDragAndDrop(TransferMode.ANY);
			ClipboardContent content = new ClipboardContent();
			content.putImage(curveRight);
			content.putString("curveRight");
			db.setContent(content);
			e.consume();
		});

		curveLeftVw.setOnDragDetected(e->{
			Dragboard db = curveLeftVw.startDragAndDrop(TransferMode.ANY);
			ClipboardContent content = new ClipboardContent();
			content.putImage(curveLeft);
			content.putString("curveLeft");
			db.setContent(content);
			e.consume();
		});

		trackLayoutArea.setOnDragOver(e->{
			if (e.getGestureSource() != trackLayoutArea &&
					e.getDragboard().hasImage()) {
				e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			}
			e.consume();
		});

		trackLayoutArea.setOnDragDropped(e-> {
			Dragboard db = e.getDragboard();
			boolean success = false;
			if(db.hasImage() && db.hasString()){

				//Create a new track based on the string contained in the dragBoard
				String trackName = db.getString();
				Track track = null;
				try{
					if(trackName.equals("straight")){
						track = new StraightTrack((int)e.getSceneX(), (int)e.getSceneY(), BASE + STRAIGHT_IMG);
					}
					else if(trackName.equals("sensor")){
						track = new SensorTrack((int)e.getSceneX(), (int)e.getSceneY(), BASE + SENSOR_IMG);
					}
					else if(trackName.equals("switchRight")){
						track = new SwitchRightTrack((int)e.getSceneX(), (int)e.getSceneY(), BASE + SWITCHRIGHT_IMG);
					}
					else if(trackName.equals("switchLeft")){
						track = new SwitchLeftTrack((int)e.getSceneX(), (int)e.getSceneY(), BASE + SWITCHLEFT_IMG);
					}
					else if(trackName.equals("curveRight")){
						track = new CurveRightTrack((int)e.getSceneX(), (int)e.getSceneY(), BASE + CURVERIGHT_IMG);
					}
					else if(trackName.equals("curveLeft")){
						track = new CurveLeftTrack((int)e.getSceneX(), (int)e.getSceneY(), BASE + CURVELEFT_IMG);
					}
				}
				catch(FileNotFoundException err){
					err.printStackTrace();
				}
				//Shift to account for width and height of track
				track.setLayoutX(track.getLayoutX() - track.getWidth()/2);
				track.setLayoutY(track.getLayoutY() - track.getHeight()/2);

				tracks.add(track);
				trackLayoutArea.getChildren().add(track);
				success = true;
			}
			e.setDropCompleted(success);
			e.consume();
		});
		/****************************************/

		/*************Organization of selection area************/
		VBox vbox = new VBox(20, straightImgVw, sensorImgVw, switchRightVw, switchLeftVw, curveRightVw, curveLeftVw);

		ScrollPane trackSelectionArea = new ScrollPane();
		trackSelectionArea.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		trackSelectionArea.setHbarPolicy(ScrollBarPolicy.NEVER);
		trackSelectionArea.setContent(vbox);
		trackSelectionArea.setMinHeight(SELECTION_AREA_HEIGHT);
		trackSelectionArea.setMinWidth(SELECTION_AREA_WIDTH);

		String selectionAreaStyle = "-fx-border-color: black;" +
				"-fx-border-width: 1;" +
				"-fx-border-style: solid;";
		trackSelectionArea.setStyle(selectionAreaStyle);
		/****************************************/

		//Button to remove the selected track
		Button removeButton = new Button("Remove Track");
		removeButton.setOnAction(e->{
			if(tracks.size() >= 1 && Track.selected != null){
				Track.selected.unlockConnectedTracks();
				tracks.remove(Track.selected);				
				trackLayoutArea.getChildren().clear();
				trackLayoutArea.getChildren().addAll(tracks);
			}
		});

		//Button to remove all tracks
		Button removeAllButton = new Button("Remove All Tracks");
		removeAllButton.setOnAction(e->{
			tracks.clear();
			trackLayoutArea.getChildren().clear();
			trackLayoutArea.getChildren().addAll(tracks);
		});

		Button trainWaypointSceneButton = new Button("Train Waypoints");
		trainWaypointSceneButton.setOnAction(e->{
			primaryStage.setScene(trainWaypointScene);
			moveTracksToWaypointArea();
		});
		/*****************Layout Organization****************/

		VBox buttons = new VBox(25, removeButton, removeAllButton, trainWaypointSceneButton);
		trackLayoutArea.setMaxWidth(900);
		HBox hbox = new HBox(25, trackLayoutArea, trackSelectionArea, buttons);
		Group root = new Group(hbox);

		trackLayoutScene = new Scene(root, PROGRAM_WIDTH, PROGRAM_HEIGHT);
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













































		/***************Second Scene********************/
		Button trackLayoutScreenButton = new Button("Track Layout");
		trackLayoutScreenButton.setOnAction(e->{
			primaryStage.setScene(trackLayoutScene);
			trackLayoutArea.getChildren().clear();

			//Set the location of the tracks to what they were previously	
			for(int i = 0; i < tracks.size(); i++){
				tracks.get(i).setLayoutX(trackLayoutAreaCoords.get(i).getX());
				tracks.get(i).setLayoutY(trackLayoutAreaCoords.get(i).getY());
				trackLayoutArea.getChildren().add(tracks.get(i));
			}
		});

		HBox topButtons= new HBox(trackLayoutScreenButton);
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
		trackSelectionArea.setMaxHeight(300);
		trackSelectionArea.setMinWidth(SELECTION_AREA_WIDTH);

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


		/***************Final Stage Stuff**********************/
		primaryStage.setTitle("Trains");
		primaryStage.setScene(trackLayoutScene);
		primaryStage.show();
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

	private void moveTracksToWaypointArea(){
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
