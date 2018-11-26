import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class TrackLayout{
	
	public Pane trackLayoutArea;
	public ScrollPane trackSelectionArea;
	
	String selectionAreaStyle = "-fx-border-color: black;" +
			"-fx-border-width: 1;" +
			"-fx-border-style: solid;";
	
	public Scene getScene(Button waypointButton, ArrayList<Track> tracks) throws Exception{

		final double PROGRAM_HEIGHT = 600;
		final double PROGRAM_WIDTH = 1200;

		final double SELECTION_AREA_HEIGHT = PROGRAM_HEIGHT;
		final double SELECTION_AREA_WIDTH = (int)0.2*PROGRAM_WIDTH;

		final double LAYOUT_AREA_HEIGHT = PROGRAM_HEIGHT;
		final double LAYOUT_AREA_WIDTH = 0.8*PROGRAM_WIDTH;

		//If image source is online, change how we access the pictures
		//final String BASE = "https://raw.githubusercontent.com/psoren/TrainsGUI/master/assets/";
		final String BASE = "/Users/parker/Documents/Courses/senior/CS440/TrainsGUI/src/assets/";
		
		
		final String STRAIGHT_IMG = "straight.png";
		final String SENSOR_IMG = "sensor.png";
		final String SWITCHRIGHT_IMG = "switchRight.png";
		final String SWITCHLEFT_IMG = "switchLeft.png";
		final String CURVERIGHT_IMG = "curveRight.png";
		final String CURVELEFT_IMG = "curveLeft.png";
		/****************************************/


		/************Track Layout Area***********/
		Group group = new Group();
		trackLayoutArea = new Pane(group);

		trackLayoutArea.setOnMouseClicked(e->{
			Track.layoutAreaClicked(new Point2D(e.getX(), e.getY()));
		});

		trackLayoutArea.setPrefHeight(LAYOUT_AREA_HEIGHT);
		trackLayoutArea.setPrefWidth(LAYOUT_AREA_WIDTH);
		/****************************************/


		/************Track Selection Area***********/
		
		FileInputStream straightStream = new FileInputStream(BASE + STRAIGHT_IMG);
		Image straight = new Image(straightStream);		
		ImageView straightImgVw = new ImageView(straight);
		straightImgVw.setFitHeight(50);
		straightImgVw.setFitWidth(100);

		FileInputStream sensorStream = new FileInputStream(BASE + SENSOR_IMG);
		Image sensor = new Image(sensorStream);
		ImageView sensorImgVw = new ImageView(sensor);
		sensorImgVw.setFitHeight(55);
		sensorImgVw.setFitWidth(50);

		
		FileInputStream srStream = new FileInputStream(BASE + SWITCHRIGHT_IMG);
		Image switchRight = new Image(srStream);
		ImageView switchRightVw = new ImageView(switchRight);
		switchRightVw.setFitHeight(60);
		switchRightVw.setFitWidth(100);

		FileInputStream slStream = new FileInputStream(BASE + SWITCHLEFT_IMG);
		Image switchLeft = new Image(slStream);
		ImageView switchLeftVw = new ImageView(switchLeft);
		switchLeftVw.setFitHeight(60);
		switchLeftVw.setFitWidth(100);

		
		FileInputStream crStream = new FileInputStream(BASE + CURVERIGHT_IMG);
		Image curveRight = new Image(crStream);
		ImageView curveRightVw = new ImageView(curveRight);
		curveRightVw.setFitHeight(60);
		curveRightVw.setFitWidth(100);

		FileInputStream clStream = new FileInputStream(BASE + CURVELEFT_IMG);
		Image curveLeft = new Image(clStream);
		ImageView curveLeftVw = new ImageView(curveLeft);
		curveLeftVw.setFitHeight(60);
		curveLeftVw.setFitWidth(100);

		/****************************************/


		/************Drag-and-drop implementation***********/
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
		
		trackSelectionArea = new ScrollPane();
		trackSelectionArea.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		trackSelectionArea.setHbarPolicy(ScrollBarPolicy.NEVER);
		trackSelectionArea.setContent(vbox);
		trackSelectionArea.setMinHeight(SELECTION_AREA_HEIGHT);
		trackSelectionArea.setMinWidth(SELECTION_AREA_WIDTH);
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
		/*****************Layout Organization****************/

		VBox buttons = new VBox(25, removeButton, removeAllButton, waypointButton);
		trackLayoutArea.setMaxWidth(900);
		HBox hbox = new HBox(25, trackLayoutArea, trackSelectionArea, buttons);		
		Scene trackLayoutScene =  new Scene(hbox, PROGRAM_WIDTH, PROGRAM_HEIGHT);
		
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
		return trackLayoutScene;	
	}
}
