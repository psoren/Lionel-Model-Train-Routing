package application;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import application.Track;
import application.View22Controller;

/**
 * Controller class for the second vista.
 */
public class View2Controller {

	private View22Controller view22Controller;

    public void setView22Controller(View22Controller view22Controller) {
        this.view22Controller = view22Controller;
    }

	@FXML
	public Pane trackLayoutArea;
	@FXML
	public Pane waypointArea;

	@FXML
	public ImageView straightImgVw;
	public Image straight = new Image("https://raw.githubusercontent.com/psoren/TrainsGUI/master/assets/straight.png");

	@FXML
	public ImageView sensorImgVw;
	public Image sensor = new Image("https://raw.githubusercontent.com/psoren/TrainsGUI/master/assets/sensor.png");

	@FXML
	public ImageView switchLeftVw;
	public Image switchLeft = new Image("https://raw.githubusercontent.com/psoren/TrainsGUI/master/assets/switchLeft.png");
	
	@FXML
	public ImageView switchRightVw;
	public Image switchRight = new Image("https://raw.githubusercontent.com/psoren/TrainsGUI/master/assets/switchRight.png");

	@FXML
	public ImageView curveLeftVw;
	public Image curveLeft = new Image("https://raw.githubusercontent.com/psoren/TrainsGUI/master/assets/curveLeft.png");

	@FXML
	public ImageView curveRightVw;
	public Image curveRight = new Image("https://raw.githubusercontent.com/psoren/TrainsGUI/master/assets/curveRight.png");

	public static ArrayList<Track> tracks = new ArrayList<Track>();
	//The location of the tracks in the layout area
	public ArrayList<Point2D> trackLayoutAreaCoords = new ArrayList<Point2D>();

	@FXML
	public VBox trackSelectionArea;

	@FXML
	public Button removeButton;
	@FXML
	public Button removeAllButton;
	@FXML
	public Button trainWaypointsButton;
	@FXML
	public Button finishButton;

	@FXML
	void previousPane(ActionEvent event) {
		ViewNavigator.loadView(ViewNavigator.VIEW_1);
	}

	@FXML
	public void nextPane(ActionEvent event) {
		ViewNavigator.loadView(ViewNavigator.VIEW_3);
	}
	
	@FXML
	public void trainWaypoints(ActionEvent event) {
		ViewNavigator.loadView(ViewNavigator.VIEW_22);
	}

	@FXML
	public void initialize() {
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
						track = new StraightTrack((int)e.getSceneX(), (int)e.getSceneY(), "https://raw.githubusercontent.com/psoren/TrainsGUI/master/assets/straight.png");
					}
					else if(trackName.equals("sensor")){
						track = new SensorTrack((int)e.getSceneX(), (int)e.getSceneY(), "https://raw.githubusercontent.com/psoren/TrainsGUI/master/assets/sensor.png");
					}
					else if(trackName.equals("switchLeft")){
						track = new SwitchLeftTrack((int)e.getSceneX(), (int)e.getSceneY(), "https://raw.githubusercontent.com/psoren/TrainsGUI/master/assets/switchLeft.png");
					}
					else if(trackName.equals("switchRight")){
						track = new SwitchLeftTrack((int)e.getSceneX(), (int)e.getSceneY(), "https://raw.githubusercontent.com/psoren/TrainsGUI/master/assets/switchRight.png");
					}
					else if(trackName.equals("curveRight")){
						track = new SensorTrack((int)e.getSceneX(), (int)e.getSceneY(), "https://raw.githubusercontent.com/psoren/TrainsGUI/master/assets/curveRight.png");
					}
					else if(trackName.equals("curveLeft")){
						track = new SwitchLeftTrack((int)e.getSceneX(), (int)e.getSceneY(), "https://raw.githubusercontent.com/psoren/TrainsGUI/master/assets/curveLeft.png");
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
		
		CheckBox enableDraggingCheckBox = new CheckBox("Move Around");
		enableDraggingCheckBox.setSelected(false);

		enableDraggingCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val){
				if(new_val){
					enableDragging(trackLayoutArea);
				}
				else{
					disableDragging(trackLayoutArea);
				}	
			}
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
	
	public void moveTracksToWaypointArea(){
		//Save location of tracks in layout area
		//before moving to waypoint area
		trackLayoutAreaCoords.clear();
		waypointArea.getChildren().clear();
		for(Track t: tracks){
			trackLayoutAreaCoords.add(new Point2D(t.getLayoutX(), t.getLayoutY()));
			view22Controller.waypointArea.getChildren().add(t);
		}
	}

	//Helper method for DFS
	public static ArrayList<Track> DFSinit(Track t){
		ArrayList<Track> visited = new ArrayList<Track>();
		DFSrec(visited, t);
		return visited;
	}

	//Recursive method for DFS
	//This method is used to make sure that all of the tracks
	//on the TrackLayout screen are connected
	public static void DFSrec(ArrayList<Track> visited, Track t){

		visited.add(t);

		//Need to check sideTrack
		if(t instanceof SwitchRightTrack){
			if(((SwitchRightTrack)t).sideTrack != null && 
					!visited.contains(((SwitchRightTrack)t).sideTrack)){
				DFSrec(visited, ((SwitchRightTrack)t).sideTrack);
			}
		}
		//Also need to check sideTrack
		else if(t instanceof SwitchLeftTrack){
			if(((SwitchLeftTrack)t).sideTrack != null && 
					!visited.contains(((SwitchLeftTrack)t).sideTrack)){
				DFSrec(visited, ((SwitchLeftTrack)t).sideTrack);
			}
		}
		if(t.frontTrack != null && !visited.contains(t.frontTrack)){
			DFSrec(visited, t.frontTrack);
		}
		if(t.backTrack != null && !visited.contains(t.backTrack)){
			DFSrec(visited, t.backTrack);
		}
	}

	//This method is used to allow the user to drag on the background
	//to move around the area in order to input a large track
	private void enableDragging(Pane p){
		final ObjectProperty<Point2D> mouseAnchor = new SimpleObjectProperty<>();
		p.setOnMousePressed(e -> {
			mouseAnchor.set(new Point2D(e.getSceneX(), e.getSceneY()));
		});
		p.setOnMouseDragged(e -> {
			//If we did not click on a track
			if(Track.getClickedTrack(new Point2D(e.getX(), e.getY())) == null){
				double deltaX = e.getSceneX() - mouseAnchor.get().getX();
				double deltaY = e.getSceneY() - mouseAnchor.get().getY();

				//relocate each of the tracks to the opposite of the direction dragged
				for(Track t: tracks){
					t.relocate(t.getLayoutX() - deltaX, t.getLayoutY() - deltaY);

				}
				mouseAnchor.set(new Point2D(e.getSceneX(), e.getSceneY()));
			}
		});
	}

	private void disableDragging(Pane p){
		p.setOnMousePressed(e -> {});
		p.setOnMouseDragged(e -> {});
	}
}