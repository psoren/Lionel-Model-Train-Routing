package application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import application.Track;
import application.View2Controller;

public class View22Controller {
	
	private View2Controller view2Controller;

    public void setView2Controller(View2Controller view2Controller) {
        this.view2Controller = view2Controller;
    }

    @FXML
	public Pane waypointArea;
	@FXML
	public VBox trackSelectionArea;
	
	@FXML
	ScrollPane trainWaypointsScrollPane;

	@FXML 
	public VBox trainRadioButtonsBox;
	@FXML
	public Button trackLayoutScreenButton; //"track layout"
	@FXML
	public Button addTrainButton;
	
	private ConcurrentHashMap<Integer, ArrayList<Track>> trainWaypoints = new ConcurrentHashMap<Integer, ArrayList<Track>>();

	ToggleGroup trainRadioButtonsToggleGroup;

	@SuppressWarnings("static-access")
	@FXML
	public void initialize() {
		
//		view2Controller.moveTracksToWaypointArea();

		//Set the location of the tracks to what they were previously	
		trackLayoutScreenButton.setOnAction(e->{
			for(int i = 0; i < view2Controller.tracks.size(); i++){
				View2Controller.tracks.get(i).setLayoutX(view2Controller.trackLayoutAreaCoords.get(i).getX());
				View2Controller.tracks.get(i).setLayoutY(view2Controller.trackLayoutAreaCoords.get(i).getY());
				view2Controller.trackLayoutArea.getChildren().add(View2Controller.tracks.get(i));
			}
		});

		/**The area where the user can select which train they are adding waypoints for**/
		trainRadioButtonsToggleGroup = new ToggleGroup();

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

		//whenever the selected radio button changes, this event will be called		
		trainRadioButtonsToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			public void changed(ObservableValue<? extends Toggle> ov,
					Toggle toggle, Toggle new_toggle) {
				//Draw the circles on each of the tracks in the tracklist
				if(new_toggle != null){					
					waypointArea.getChildren().clear();
					waypointArea.getChildren().addAll(view2Controller.tracks);
					//System.out.println("the current trains are:" + trainWaypoints);

					int id = new Integer(((RadioButton)trainRadioButtonsToggleGroup.getSelectedToggle()).getId());
					drawCirclesOnTracks(trainWaypoints.get(id));
				}       
			}
		});

		//When clicked, this button will add a new train to the radioButtonGroup
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

			/****************************************************/
		});
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
}