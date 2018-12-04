import java.util.*;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class TrainRunning {

	public Pane trainRunningArea;

	final double PROGRAM_HEIGHT = 600;
	final double PROGRAM_WIDTH = 1200;

	final double TRAINRUNNING_AREA_HEIGHT = PROGRAM_HEIGHT;
	final double TRAINRUNNING_AREA_WIDTH = 0.8*PROGRAM_WIDTH;

	final double SIDEBUTTONS_AREA_HEIGHT = PROGRAM_HEIGHT;
	final double SIDEBUTTONS_AREA_WIDTH = 0.2*PROGRAM_WIDTH;

	String selectionAreaStyle = "-fx-border-color: black;" +
			"-fx-border-width: 1;" +
			"-fx-border-style: solid;";

	//The main file controlling everything
	TrainsGUI gui;

	//This is used to control the trains
	//and organize when sensor information is sent to tasks
	HashMap<Integer, ControlTrainTask> trainTasks;

	public TrainRunning(TrainsGUI gui){
		this.gui = gui;	
		this.trainTasks = new HashMap<Integer, ControlTrainTask>();
	}

	public Scene getScene(Button matchSensorsBtn, ArrayList<Track> tracks){
		HBox topButtons = new HBox(matchSensorsBtn);
		topButtons.setAlignment(Pos.BASELINE_CENTER);

		Group matchingSensorsGroup = new Group();
		trainRunningArea = new Pane(matchingSensorsGroup);
		trainRunningArea.setPrefHeight(TRAINRUNNING_AREA_HEIGHT);
		trainRunningArea.setPrefWidth(TRAINRUNNING_AREA_WIDTH);

		Button startButton = new Button("Start");
		Button stopButton = new Button("Stop");
		stopButton.setDisable(true);

		ToggleGroup speedButtonsGroup = new ToggleGroup();

		RadioButton slowButton = new RadioButton("Slow");
		slowButton.setId("slow");
		slowButton.setToggleGroup(speedButtonsGroup);


		RadioButton mediumButton = new RadioButton("Medium");
		mediumButton.setId("medium");
		mediumButton.setToggleGroup(speedButtonsGroup);


		RadioButton fastButton = new RadioButton("Fast");
		fastButton.setId("fast");
		fastButton.setToggleGroup(speedButtonsGroup);

		speedButtonsGroup.selectToggle(slowButton);

		speedButtonsGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			public void changed(ObservableValue<? extends Toggle> ov, Toggle toggle, Toggle new_toggle) {
				if(new_toggle != null){
					RadioButton rb = (RadioButton)new_toggle;
					updateTrainSpeed(rb.getId());
				}
			}
		});

		HBox speedButtons = new HBox(20, slowButton, mediumButton, fastButton);
		HBox runButtons = new HBox(20, startButton, stopButton);

		VBox sideButtons = new VBox(20, runButtons, speedButtons);
		sideButtons.setAlignment(Pos.BASELINE_CENTER);
		sideButtons.setMinHeight(SIDEBUTTONS_AREA_HEIGHT);
		sideButtons.setMinWidth(SIDEBUTTONS_AREA_WIDTH);
		sideButtons.setStyle(selectionAreaStyle);

		//TODO
		//Currently we are only starting one train
		//Once we add the ability to add the train
		//IDs to the waypoint screen, we can use that
		//information instead of this hardcoded one
		//(That will not be hard)
		startButton.setOnAction(e-> {
			ControlTrainTask controlTrainTask54 = new ControlTrainTask(54);

			controlTrainTask54.setOnSucceeded(evt->{
				System.out.println("controlTrainTask has succeeded");
			});

			controlTrainTask54.setOnFailed(evt->{
				System.out.println("controlTrainTask has failed");
			});

			SocketCommunication.executor.submit(controlTrainTask54);
			this.trainTasks.put(54, controlTrainTask54);
			startButton.setDisable(true);
			stopButton.setDisable(false);
		});

		stopButton.setOnAction(e-> {
			SocketCommunication.executor.shutdownNow();
			SocketCommunication.createNewExecutor();
			SocketCommunication.executor.submit(new StopTrainsTask());
			startButton.setDisable(false);
			stopButton.setDisable(true);
		});

		/**Final Scene Setup**/
		HBox mainBottomArea = new HBox(trainRunningArea, sideButtons);
		VBox main = new VBox(topButtons, mainBottomArea);
		return new Scene(main, PROGRAM_WIDTH, PROGRAM_HEIGHT);	
	}

	//There is new sensor information
	public void sensorInfo(int sensorID, String direction, int trainIDNum){		
		ControlTrainTask task = trainTasks.get(trainIDNum);
		if(task != null){
			task.sensorEvent(sensorID, direction);
		}
		else{
			System.out.println("Could not find the specified train in trainTasks");
		}
	}

	private void updateTrainSpeed(String id){
		Collection<ControlTrainTask> tasks = trainTasks.values();

		for(ControlTrainTask t: tasks){
			t.updateSpeed(id);
		}
	}
}
