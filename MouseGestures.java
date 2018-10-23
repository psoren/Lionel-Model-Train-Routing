import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.shape.*;

public class MouseGestures{

	final DragContext dragContext = new DragContext();

	public void makeDraggable(final Track track){

		track.imageView.addEventHandler(MouseEvent.MOUSE_PRESSED, e ->{
			dragContext.x = e.getSceneX();
			dragContext.y = e.getSceneY();
		});

		track.imageView.addEventHandler(MouseEvent.MOUSE_DRAGGED, e ->{
			double offsetX = e.getSceneX() - dragContext.x;
			double offsetY = e.getSceneY() - dragContext.y;

			track.imageView.setX(offsetX - track.imageView.getFitWidth()/2);
			track.imageView.setY(offsetY - track.imageView.getFitHeight()/2);

			dragContext.x = 0;
			dragContext.y = 0;
		});

		//Context menu stuff
		ContextMenu contextMenu = new ContextMenu();

		MenuItem rotateCW = new MenuItem("Rotate Clockwise");
		rotateCW.setOnAction(e->{
			track.rotateCW();
		});

		MenuItem rotateCCW = new MenuItem("Rotate Counter-clockwise");
		rotateCCW.setOnAction(e->{
			track.rotateCCW();
		});

		MenuItem delete = new MenuItem("Delete");
		delete.setOnAction(e->{
			track.delete();
		});

		contextMenu.getItems().addAll(rotateCW, rotateCCW, delete);

		track.imageView.setOnContextMenuRequested(e-> {
			contextMenu.show(track.imageView, e.getScreenX(), e.getScreenY());
		});
	}

	class DragContext {
		double x;
		double y;
	}

	//pathtransition works with the center of the node => we need to consider that
	public static class MoveToAbs extends MoveTo{
		public MoveToAbs(Node node, double x, double y) {
			super(x - node.getLayoutX() + node.getLayoutBounds().getWidth() / 2, y - node.getLayoutY() + node.getLayoutBounds().getHeight() / 2);
		}
	}

	//pathtransition works with the center of the node => we need to consider that
	public static class LineToAbs extends LineTo{
		public LineToAbs( Node node, double x, double y) {
			super(x - node.getLayoutX() + node.getLayoutBounds().getWidth() / 2, y - node.getLayoutY() + node.getLayoutBounds().getHeight() / 2);
		}
	}
}