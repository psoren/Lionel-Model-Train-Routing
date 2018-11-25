import java.util.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

public class TrainsGUI extends Application{

	String selectionAreaStyle = "-fx-border-color: black;" +
			"-fx-border-width: 1;" +
			"-fx-border-style: solid;";

	public static ArrayList<Track> tracks = new ArrayList<Track>();

	final double PROGRAM_HEIGHT = 600;
	final double PROGRAM_WIDTH = 1200;

	TrackLayout trackLayout;
	TrainWaypoint trainWaypoint;
	MatchingSensors matchingSensors;
	
	Scene trainWaypointScene;
	Scene trackLayoutScene;
	Scene matchingSensorScene;
	Stage mainStage;

	@Override
	public void start(Stage primaryStage) throws Exception{
		mainStage = primaryStage;

		trackLayout = new TrackLayout();
		Button trackLayoutTotrainWaypointButton = new Button("Train Waypoints");
		trackLayoutScene = trackLayout.getScene(trackLayoutTotrainWaypointButton, tracks);

		trackLayoutTotrainWaypointButton.setOnAction(e->{
			mainStage.setScene(trainWaypointScene);
			trainWaypoint.waypointArea.getChildren().clear();
			for(Track t: tracks){trainWaypoint.waypointArea.getChildren().add(t);}
		});

		trainWaypoint = new TrainWaypoint();
		Button trainWaypointToTrackLayoutButton = new Button("Track Layout");
		Button trainWaypointToMatchingSensorsButton = new Button("Match Sensors");
		trainWaypointScene = trainWaypoint.getScene(trainWaypointToTrackLayoutButton, trainWaypointToMatchingSensorsButton, tracks);

		trainWaypointToTrackLayoutButton.setOnAction(e->{
			mainStage.setScene(trackLayoutScene);
			trackLayout.trackLayoutArea.getChildren().clear();
			for(Track t: tracks){trackLayout.trackLayoutArea.getChildren().add(t);}
		});

		trainWaypointToMatchingSensorsButton.setOnAction(e ->{
			mainStage.setScene(matchingSensorScene);
			for(Track t: tracks){matchingSensors.matchingSensorsArea.getChildren().add(t);}
		});

		matchingSensors = new MatchingSensors();
		Button matchingSensorsToTrainWaypointButton = new Button("Train Waypoints");
		matchingSensorScene = matchingSensors.getScene(matchingSensorsToTrainWaypointButton, tracks);

		matchingSensorsToTrainWaypointButton.setOnAction(e-> {
			mainStage.setScene(trainWaypointScene);			
			trainWaypoint.waypointArea.getChildren().clear();
			for(Track t: tracks){trainWaypoint.waypointArea.getChildren().add(t);}
		});

		mainStage.setTitle("Trains");
		mainStage.setScene(trackLayoutScene);
		mainStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
