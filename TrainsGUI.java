import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;

public class TrainsGUI extends Application{

	static ArrayList<Track> tracks = new ArrayList<Track>();

	@Override
	public void start(Stage primaryStage) throws Exception{

		/************Constants***********/
		final double PROGRAM_HEIGHT = 600;
		final double PROGRAM_WIDTH = 1000;

		final double SELECTION_AREA_HEIGHT = PROGRAM_HEIGHT;
		final double SELECTION_AREA_WIDTH = (int)0.2*PROGRAM_WIDTH;

		final double LAYOUT_AREA_HEIGHT = PROGRAM_HEIGHT;
		final double LAYOUT_AREA_WIDTH = 0.8*PROGRAM_WIDTH;

		final String BASE = "/Users/parker/Documents/Courses/senior/CS440/TrainsGUI/src/assets/";
		final String STRAIGHT_IMG = "straight.png";
		final String SENSOR_IMG = "sensor.png";
		/****************************************/

		
		/************Track Layout Area***********/
		MouseGestures mg = new MouseGestures();
		Group group = new Group();

		group.getChildren().addAll(getTrackImages(tracks));
		Pane trackLayoutArea = new Pane(group);
		trackLayoutArea.setPrefHeight(LAYOUT_AREA_HEIGHT);
		trackLayoutArea.setPrefWidth(LAYOUT_AREA_WIDTH);
		/****************************************/

		
		/************Track Selection Area***********/
		Image straight = new Image(new FileInputStream(BASE + STRAIGHT_IMG));
		ImageView straightImgVw = new ImageView(straight);
		straightImgVw.setFitHeight(100);
		straightImgVw.setFitWidth(50);

		Image sensor = new Image(new FileInputStream(BASE + SENSOR_IMG));
		ImageView sensorImgVw = new ImageView(sensor);
		sensorImgVw.setFitHeight(100);
		sensorImgVw.setFitWidth(50);
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

		trackLayoutArea.setOnDragOver(e->{
			if (e.getGestureSource() != trackLayoutArea &&
					e.getDragboard().hasImage()) {
				e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			}
			e.consume();
		});

		trackLayoutArea.setOnDragEntered(e->{
			e.consume();
		});

		trackLayoutArea.setOnDragExited(e->{
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
				}
				catch(FileNotFoundException err){
					err.printStackTrace();
				}
				//Shift to account for width and height of track
				int trackX = (int)track.imageView.getX();
				int trackY = (int)track.imageView.getY();

				track.imageView.setX(trackX - track.imageView.getFitWidth()/2);
				track.imageView.setY(trackY - track.imageView.getFitHeight()/2);

				mg.makeDraggable(track);
				tracks.add(track);
				group.getChildren().add(track.imageView);
				success = true;
			}
			e.setDropCompleted(success);
			e.consume();
		});

		trackLayoutArea.setOnDragDone(e->{
			e.consume();
		});

		VBox trackSelectionArea = new VBox(20, straightImgVw, sensorImgVw);
		trackSelectionArea.setPrefHeight(SELECTION_AREA_HEIGHT);
		trackSelectionArea.setPrefWidth(SELECTION_AREA_WIDTH);
		/****************************************/
		
		
		/*****************Organization of layout****************/
		HBox hbox = new HBox(20, trackLayoutArea, trackSelectionArea);
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

	public ArrayList<ImageView> getTrackImages(ArrayList<Track> tracks){
		ArrayList<ImageView> imageViews = new ArrayList<ImageView>();	
		for(Track t: tracks){
			imageViews.add(t.imageView);
		}
		return imageViews;
	}
}
