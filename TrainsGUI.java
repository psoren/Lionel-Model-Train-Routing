import java.util.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

//TODO: Let the user add train ID numbers for each train to the trainWaypoint screen

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
	TrainRunning trainRunning;
	SocketCommunication socketCommunication;

	Scene trainWaypointScene;
	Scene trackLayoutScene;
	Scene matchingSensorScene;
	Scene trainRunningScene;
	Stage mainStage;

	@Override
	public void start(Stage primaryStage) throws Exception{
		mainStage = primaryStage;

		trackLayout = new TrackLayout(this);
		Button trackLayoutTotrainWaypointButton = new Button("Train Waypoints");
		trackLayoutScene = trackLayout.getScene(trackLayoutTotrainWaypointButton, tracks);

		trackLayoutTotrainWaypointButton.setOnAction(e->{

			//If all of the tracks are not connected, do not let the 
			//user move on to the next screen
			if(tracks.size() == 0){
				Popup.display("Please create your tracks.", "Input Your Track");
			}
			else{
				ArrayList<Track> reachableTracks = TrackLayout.DFSinit(tracks.get(0));

				//We could reach all of the tracks from the first track,
				//so the tracks are all connected
				if(reachableTracks.size() == tracks.size()){
					mainStage.setScene(trainWaypointScene);
					trainWaypoint.waypointArea.getChildren().clear();
					for(Track t: tracks){
						t.setStyle(Track.unselectedStyle);
						Track.selected = null;
						trainWaypoint.waypointArea.getChildren().add(t);
					}
				}
				else{
					Popup.display("Please make sure that all of your tracks are connected", "Connect Your Tracks");
				}
			}
		});

		trainWaypoint = new TrainWaypoint(this);
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

		matchingSensors = new MatchingSensors(this);
		Button matchingSensorsToTrainWaypointButton = new Button("Train Waypoints");
		Button matchingSensorsToTrainRunningButton = new Button("Run Train");
		matchingSensorScene = matchingSensors.getScene(matchingSensorsToTrainWaypointButton, matchingSensorsToTrainRunningButton, tracks);

		matchingSensorsToTrainWaypointButton.setOnAction(e-> {
			mainStage.setScene(trainWaypointScene);			
			trainWaypoint.waypointArea.getChildren().clear();
			for(Track t: tracks){trainWaypoint.waypointArea.getChildren().add(t);}
		});

		matchingSensorsToTrainRunningButton.setOnAction(e->{
			mainStage.setScene(trainRunningScene);
			trainRunning.trainRunningArea.getChildren().clear();
			for(Track t: tracks){trainRunning.trainRunningArea.getChildren().add(t);}
			Popup.display("Please place your trains directly before their first waypoint.", "Train Placement");
		});

		trainRunning = new TrainRunning(this);
		Button trainRunningToMatchSensorsBtn = new Button("Match Sensors");
		trainRunningScene = trainRunning.getScene(trainRunningToMatchSensorsBtn, tracks);

		trainRunningToMatchSensorsBtn.setOnAction(e->{
			mainStage.setScene(matchingSensorScene);
			matchingSensors.matchingSensorsArea.getChildren().clear();
			for(Track t: tracks){matchingSensors.matchingSensorsArea.getChildren().add(t);}
		});

		socketCommunication = new SocketCommunication(this);

		mainStage.setTitle("Trains");
		mainStage.setScene(trackLayoutScene);
		mainStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
