package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller class for the second vista.
 */
public class View2Controller {

	/**
	 * Event handler fired when the user requests a previous vista.
	 *
	 * @param event the event that triggered the handler.
	 */

	@FXML
	public Pane trackLayoutArea;

	@FXML
	public ImageView straightImgVw;
	@FXML
	public Image straight = new Image("file:///C:/Users/Brita/Desktop/straight.png");

	@FXML
	public ImageView sensorImgVw;
	@FXML
	public Image sensor = new Image("file:///C:/Users/Brita/Desktop/sensor.png");

	@FXML
	public ImageView switchLeftVw;
	@FXML
	public Image switchLeft = new Image("file:///C:/Users/Brita/Desktop/switchLeft.png");
	
	@FXML
	public ImageView curveLeftVw;
	@FXML
	public Image curveLeft = new Image("file:///C:/Users/Brita/Desktop/curveLeft.png");
	
	@FXML
	public ImageView curveRightVw;
	@FXML
	public Image curveRight = new Image("file:///C:/Users/Brita/Desktop/curveRight.png");

	@FXML
	public static ArrayList<Track> tracks = new ArrayList<Track>();

	@FXML
	public VBox trackSelectionArea;

	@FXML
	public Button removeButton;
	@FXML
	public Button removeAllButton;
	@FXML
	public Button finishButton;

	@FXML
	void previousPane(ActionEvent event) {
		ViewNavigator.loadView(ViewNavigator.VIEW_1);
	}
	
	public void addActionListener(EventHandler<ActionEvent> eventHandlerSetNotSave) {
		trackLayoutArea.setOnMouseClicked(e->{
			Track.layoutAreaClicked(new Point2D(e.getX(), e.getY()));
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
						track = new StraightTrack((int)e.getSceneX(), (int)e.getSceneY(), "/Users/Brita/eclipse-workspace/CS440Gui/assets");
					}
					else if(trackName.equals("sensor")){
						track = new SensorTrack((int)e.getSceneX(), (int)e.getSceneY(), "/Users/Brita/eclipse-workspace/CS440Gui/assets");
					}
					else if(trackName.equals("switchLeft")){
						track = new SwitchLeftTrack((int)e.getSceneX(), (int)e.getSceneY(), "/Users/Brita/eclipse-workspace/CS440Gui/assets");
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

		switchLeftVw.setOnDragDetected(e->{
			Dragboard db = switchLeftVw.startDragAndDrop(TransferMode.ANY);
			ClipboardContent content = new ClipboardContent();
			content.putImage(switchLeft);
			content.putString("switchLeft");
			db.setContent(content);
			e.consume();
		});
	}

		@FXML
		void remove(ActionEvent event) {
		removeButton.setOnAction(e->{
			if(tracks.size() >= 1 && Track.selected != null){
				Track.selected.unlockConnectedTracks();
				tracks.remove(Track.selected);				
				trackLayoutArea.getChildren().clear();
				trackLayoutArea.getChildren().addAll(tracks);
			}
		});
	}
	
		@FXML
		void removeAll(ActionEvent event) {
		removeAllButton.setOnAction(e->{
			tracks.clear();
			trackLayoutArea.getChildren().clear();
			trackLayoutArea.getChildren().addAll(tracks);
		});
	}
}