import java.io.*;
import java.util.*;
import javafx.application.*;
import javafx.geometry.Point2D;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class TrainsGUI extends Application{

	public static ArrayList<Track> tracks = new ArrayList<Track>();

	@Override
	public void start(Stage primaryStage) throws Exception{
		
		/************Constants***********/
		final double PROGRAM_HEIGHT = 600;
		final double PROGRAM_WIDTH = 1200;

		final double SELECTION_AREA_HEIGHT = PROGRAM_HEIGHT;
		final double SELECTION_AREA_WIDTH = (int)0.2*PROGRAM_WIDTH;

		final double LAYOUT_AREA_HEIGHT = PROGRAM_HEIGHT;
		final double LAYOUT_AREA_WIDTH = 0.8*PROGRAM_WIDTH;

		
		//final String BASE = "/Users/parker/Documents/Courses/senior/CS440/TrainsGUI/src/assets/";
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

		//group.getChildren().addAll(tracks);
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
		//VBox vbox = new VBox(20, straightImgVw, sensorImgVw, switchRightVw, switchLeftVw, curveRightVw, curveLeftVw);
		VBox vbox = new VBox(20, straightImgVw, sensorImgVw, switchRightVw, switchLeftVw, curveLeftVw);
		
		ScrollPane trackSelectionArea = new ScrollPane();
		trackSelectionArea.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		trackSelectionArea.setHbarPolicy(ScrollBarPolicy.NEVER);
		trackSelectionArea.setContent(vbox);
		//trackSelectionArea.setPrefHeight(SELECTION_AREA_HEIGHT);
		//trackSelectionArea.setPrefWidth(SELECTION_AREA_WIDTH);
	
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
		
		Button generateGraphButton = new Button("Generate Graph");
		generateGraphButton.setOnAction(e->{
			Track.generateGraph();
		});
		
		/*****************Layout Organization****************/
		VBox buttons = new VBox(25, removeButton, removeAllButton, generateGraphButton);
		trackLayoutArea.setMaxWidth(900);
		HBox hbox = new HBox(25, trackLayoutArea, trackSelectionArea, buttons);
		Group root = new Group(hbox);

		Scene scene = new Scene(root, PROGRAM_WIDTH, PROGRAM_HEIGHT);
		primaryStage.setTitle("Trains");
		primaryStage.setScene( scene);
		primaryStage.show();
		/******************************************************/
	}

	public static void main(String[] args) {
		launch(args);
	}
}
