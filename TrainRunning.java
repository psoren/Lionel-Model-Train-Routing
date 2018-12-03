import java.util.*;
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

		VBox sideButtons = new VBox(20);
		sideButtons.getChildren().addAll(startButton, stopButton);
		sideButtons.setAlignment(Pos.BASELINE_CENTER);
		sideButtons.setMinHeight(SIDEBUTTONS_AREA_HEIGHT);
		sideButtons.setMinWidth(SIDEBUTTONS_AREA_WIDTH);
		sideButtons.setStyle(selectionAreaStyle);

		startButton.setOnAction(e-> {
			TrainsGUI.executor.submit(new ControlTrainTask(54));
			startButton.setDisable(true);
			stopButton.setDisable(false);
		});

		stopButton.setOnAction(e-> {
			TrainsGUI.executor.shutdownNow();
			TrainsGUI.createNewExecutor();
			TrainsGUI.executor.submit(new StopTrainsTask());
			startButton.setDisable(false);
			stopButton.setDisable(true);
		});

		/**Final Scene Setup**/
		HBox mainBottomArea = new HBox(trainRunningArea, sideButtons);
		VBox main = new VBox(topButtons, mainBottomArea);
		return new Scene(main, PROGRAM_WIDTH, PROGRAM_HEIGHT);	
	}
}
