import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javafx.beans.value.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class TrainWaypoint{

	final double PROGRAM_HEIGHT = 600;
	final double PROGRAM_WIDTH = 1200;

	final double WAYPOINT_AREA_HEIGHT = PROGRAM_HEIGHT;
	final double WAYPOINT_AREA_WIDTH = 0.8*PROGRAM_WIDTH;
	
	final double SELECTION_AREA_WIDTH = (int)0.2*PROGRAM_WIDTH;
	
	String selectionAreaStyle = "-fx-border-color: black;" +
			"-fx-border-width: 1;" +
			"-fx-border-style: solid;";

	ToggleGroup trainRadioButtonsToggleGroup;
	VBox trainRadioButtonsBox;
	Pane waypointArea;

	//The list of tracks for each train
	private ConcurrentHashMap<Integer, ArrayList<Track>> trainWaypoints = new ConcurrentHashMap<Integer, ArrayList<Track>>();

	public Scene getScene(Button trackLayoutScreenButton, Button matchSensorsButton,
			ArrayList<Track> tracks){
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
			Track t = Track.getClickedTrack(new Point2D(e.getX(), e.getY()));

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

		//Whenever the selected radio button changes, this event will be called		
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

		//When clicked, this button will add a new train to the radioButtonGroup
		Button addTrainButton = new Button("Add Train");
		addTrainButton.setOnAction(e->{
			int trainNumber = trainRadioButtonsToggleGroup.getToggles().size();

			//The train radio button
			RadioButton newTrainRadioButton = new RadioButton("Train " + trainNumber);
			newTrainRadioButton.setToggleGroup(trainRadioButtonsToggleGroup);
			newTrainRadioButton.setId(Integer.toString(trainNumber));
			newTrainRadioButton.setStyle(generateRandomColor());

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

				//We now need to re-index trainWaypoints so that the indices 
				//are 0,1,2,3,... as in the rest of the program				
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
		VBox vbox = new VBox(topButtons, mainBottomArea);
		return new Scene(vbox,PROGRAM_WIDTH, PROGRAM_HEIGHT);	
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
	
	//A method to generate a random color and return the CSS string
	private String generateRandomColor(){
		int r = (int)(Math.random()*255);
		int g = (int)(Math.random()*255);
		int b = (int)(Math.random()*255);
		return "-fx-background-color: rgb("+r+","+g+","+b+");";
	}
}
