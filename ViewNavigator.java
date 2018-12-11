package application;

import javafx.fxml.FXMLLoader;

import java.io.IOException;

/**
 * Utility class for controlling navigation between views.
 */
public class ViewNavigator {

	/**
	 * Convenience constants for fxml layouts managed by the navigator.
	 */
	public static final String MAIN    = "/application/main.fxml";
	public static final String VIEW_1 = "/application/view1.fxml";
	public static final String VIEW_2 = "/application/view2.fxml";
	public static final String VIEW_22 = "/application/view22.fxml";
	public static final String VIEW_3 = "/application/view3.fxml";
	public static final String VIEW_4 = "/application/view4.fxml";

	/** The main application layout controller. */
	private static MainController mainController;

	/**
	 * @param mainController the main application layout controller.
	 */
	public static void setMainController(MainController mainController) {
		ViewNavigator.mainController = mainController;
	}

	/**
	 * @param fxml the fxml file to be loaded.
	 */
	public static void loadView(String fxml) {
		try {
			mainController.setView(FXMLLoader.load(ViewNavigator.class.getResource(fxml)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}