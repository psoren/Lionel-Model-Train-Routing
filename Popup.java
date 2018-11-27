import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class Popup {
	public static void display(String message, String title)
	{
		Stage popupwindow = new Stage();
		popupwindow.initModality(Modality.APPLICATION_MODAL);
		popupwindow.setTitle(title);
		Label label = new Label(message);
		Button button = new Button("Close");
		button.setOnAction(e -> popupwindow.close());
		VBox layout= new VBox(10);

		layout.getChildren().addAll(label, button);
		layout.setAlignment(Pos.CENTER);
		Scene scene1= new Scene(layout, 400, 250);
		popupwindow.setScene(scene1);
		popupwindow.showAndWait();
	}
}