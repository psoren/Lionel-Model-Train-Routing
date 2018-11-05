import java.io.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public abstract class Track extends StackPane{

	//Figure out how to ensure that you cant snap a track
	//to a place that already has a track connected to it

	/**Constants**/
	private int SNAPTOGETHERDISTANCE = 40;

	/**Static fields**/
	//This is a static field that is set whenever a track is selected
	public static Track selected;
	private static final String selectedStyle = "-fx-border-color: black; -fx-border-width: 2;";
	private static final String unselectedStyle = "-fx-border-style: none";
	private static final String sameOrientationStyle = "-fx-border-color: green";
	private static final String differentOrientationStyle = "-fx-border-color: red";

	/**Non-static fields**/
	//The number of rotations for a track to be back at the same position
	protected int numRotations;

	//The track in front of this track
	private Track frontTrack;

	//The track to the back of this track
	private Track backTrack;

	//The orientation of the front of the track (How many degrees off of 0 it is)
	public int frontOrientation;

	//The orientation of the back of the track (How many degrees off of 0 it is)
	public int backOrientation;

	//If this track is "locked onto" another track
	private boolean locked;

	//The context menu for this track
	private ContextMenu contextMenu;

	/**
	 * @param x: the x coordinate of the image
	 * @param y: the y coordinate of the image
	 * @param imageSource: the source of the image
	 **/	
	public Track(int x, int y, String imageSource) throws FileNotFoundException{
		super();
		this.frontTrack = null;
		this.backTrack = null;
		this.locked = false;

		ImageView imageView = new ImageView(new Image(new FileInputStream(imageSource)));

		this.getChildren().addAll(new Rectangle((int)imageView.getFitWidth(), 
				(int)imageView.getFitHeight(), Color.BLACK), imageView);

		enableDragging(this);

		this.setLayoutX(x);
		this.setLayoutY(y);

		this.addEventHandler(MouseEvent.MOUSE_PRESSED, e ->{

			//Unselect the previous track
			if(Track.selected != null){
				selected.setStyle(unselectedStyle);
			}

			//If the track is already selected, unselect it
			if(Track.selected == this){
				Track.selected = null;
			}
			else{
				Track.selected = this;
				Track.selected.setStyle(selectedStyle);
			}
		});

		//Context menu stuff
		this.contextMenu = new ContextMenu();

		MenuItem rotateCW = new MenuItem("Rotate Clockwise");
		rotateCW.setOnAction(e-> this.rotateCW());
		rotateCW.setId("RotCW");

		MenuItem rotateCCW = new MenuItem("Rotate Counter-clockwise");
		rotateCCW.setOnAction(e-> this.rotateCCW());
		rotateCCW.setId("RotCCW");

		MenuItem disconnectTrack = new MenuItem("Disconnect Track");
		disconnectTrack.setOnAction(e-> this.disconnect());
		disconnectTrack.setId("disconnect");

		contextMenu.getItems().addAll(rotateCW, rotateCCW, disconnectTrack);
		disconnectTrack.setDisable(true);

		this.setOnContextMenuRequested(e-> {
			contextMenu.show(this, e.getScreenX(), e.getScreenY());
		});
	}

	private void enableDragging(Track track){
		final ObjectProperty<Point2D> mouseAnchor = new SimpleObjectProperty<>();
		this.setOnMousePressed(e -> {

			//System.out.println("front orientation: " + this.frontOrientation);
			//System.out.println("back orientation: " + this.backOrientation);

			//System.out.println("x: " + this.getLayoutX());
			//System.out.println("y: " + this.getLayoutY());

			mouseAnchor.set(new Point2D(e.getSceneX(), e.getSceneY()));
		});
		this.setOnMouseDragged(e -> {

			if(!this.locked){
				double deltaX = e.getSceneX() - mouseAnchor.get().getX();
				double deltaY = e.getSceneY() - mouseAnchor.get().getY();
				this.relocate(this.getLayoutX() + deltaX, this.getLayoutY() + deltaY);
				mouseAnchor.set(new Point2D(e.getSceneX(), e.getSceneY()));

				//The "snap-to" behavior
				for(Track t: TrainsGUI.tracks){
					boolean snapped = false;

					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					//Dragging towards the side of a SwitchRightTrack
					if(t instanceof SwitchRightTrack && ((SwitchRightTrack) t).sideOrientation == this.backOrientation){

						double x1 = this.getBackLeft().getX();
						double x2 = ((SwitchRightTrack)t).getRightSwitchCoords().getX();

						double y1 = this.getBackLeft().getY();
						double y2 = ((SwitchRightTrack)t).getRightSwitchCoords().getY();

						//The distance between this track and the bottom of the other track
						double d = Math.sqrt(Math.pow((x1-x2), 2) + Math.pow((y1-y2),2));

						if(d < SNAPTOGETHERDISTANCE){
							double x = ((SwitchRightTrack) t).getRightSwitchCoords().getX();
							this.setLayoutX(this.getLayoutX() + x - this.getBackLeft().getX());

							double y = ((SwitchRightTrack) t).getRightSwitchCoords().getY();
							this.setLayoutY(this.getLayoutY() + y - this.getBackLeft().getY());
							snapped = true;

							this.locked = true;
							t.locked = true;

							this.enableDisconnectTrack();
							t.enableDisconnectTrack();

							this.frontTrack = t;
							((SwitchRightTrack) t).sideTrack = this;
							this.highlightTracks();
							break;		
						}
					}

					//Dragging towards the back of another track
					if(this.distanceBetweenThisFrontAndOtherBack(t) < SNAPTOGETHERDISTANCE){
						if(this.frontOrientation == t.backOrientation){
							this.setLayoutX(this.getLayoutX() + t.getBackLeft().getX() - this.getFrontLeft().getX());
							this.setLayoutY(this.getLayoutY() + t.getBackLeft().getY() - this.getFrontLeft().getY());		
							snapped = true;
						}	

						if(t instanceof CurveRightTrack && t.backOrientation == this.frontOrientation){

							double x = ((CurveRightTrack) t).getBackCurveCoords().getX();
							this.setLayoutX(this.getLayoutX() + x - this.getFrontLeft().getX());

							double y = ((CurveRightTrack) t).getBackCurveCoords().getY();
							this.setLayoutY(this.getLayoutY() + y - this.getFrontLeft().getY());
							snapped = true;
						}

						if(t instanceof CurveLeftTrack && t.backOrientation == this.frontOrientation){
							double x = ((CurveLeftTrack) t).getBackCurveCoords().getX();
							this.setLayoutX(this.getLayoutX() + x - this.getFrontLeft().getX());

							double y = ((CurveLeftTrack) t).getBackCurveCoords().getY();
							this.setLayoutY(this.getLayoutY() + y - this.getFrontLeft().getY());
							snapped = true;
						}
					}

					//Dragging towards the front of another track
					else if(this.distanceBetweenThisBackAndOtherFront(t) < SNAPTOGETHERDISTANCE){
						//if the orientations match, snap them together
						if(this.backOrientation == t.frontOrientation){
							this.setLayoutX(this.getLayoutX() + t.getFrontLeft().getX() - this.getBackLeft().getX());
							this.setLayoutY(this.getLayoutY() + t.getFrontLeft().getY() - this.getBackLeft().getY());
							snapped = true;

							//we need to translate it more
							if(t instanceof CurveRightTrack){
								double x = ((CurveRightTrack) t).getFrontCurveCoords().getX();
								this.setLayoutX(this.getLayoutX() + x - this.getBackLeft().getX());

								double y = ((CurveRightTrack) t).getFrontCurveCoords().getY();
								this.setLayoutY(this.getLayoutY() + y - this.getBackLeft().getY());
								snapped = true;
							}

							//if the other track is a curvelefttrack
							if(t instanceof CurveLeftTrack){
								double x = ((CurveLeftTrack) t).getFrontCurveCoords().getX();
								this.setLayoutX(this.getLayoutX() + x - this.getBackLeft().getX());

								double y = ((CurveLeftTrack) t).getFrontCurveCoords().getY();
								this.setLayoutY(this.getLayoutY() + y - this.getBackLeft().getY());
								snapped = true;
							}
						}
					}
					if(snapped){
						this.locked = true;
						t.locked = true;

						this.enableDisconnectTrack();
						t.enableDisconnectTrack();

						this.frontTrack = t;
						t.backTrack = this;
						this.highlightTracks();
						break;
					}
				}		
			}
		});
	}

	//Returns the distance between the top of this track
	//and the bottom of the other one
	private int distanceBetweenThisFrontAndOtherBack(Track track){
		int x1 = (int)this.getFrontLeft().getX();
		int x2 = (int)track.getBackLeft().getX();

		int y1 = (int)this.getFrontLeft().getY();
		int y2 = (int)track.getBackLeft().getY();

		//The distance between this track and the bottom of the other track
		return (int)Math.sqrt(Math.pow((x1-x2), 2) + Math.pow((y1-y2),2));
	}

	//Returns the distance between the top of this track
	//and the bottom of the other one
	private int distanceBetweenThisBackAndOtherFront(Track track){
		int x1 = (int)this.getBackLeft().getX();
		int x2 = (int)track.getFrontLeft().getX();

		int y1 = (int)this.getBackLeft().getY();
		int y2 = (int)track.getFrontLeft().getY();

		//The distance between this track and the bottom of the other track
		return (int)Math.sqrt(Math.pow((x1-x2), 2) + Math.pow((y1-y2),2));
	}

	//A way to highlight the tracks based on their orientation
	private void highlightTracks(){
		//loop through the frontTrack list
		Track fTrack = this.frontTrack;
		while(fTrack != null){

			//if their orientations match, make them green
			if(fTrack.backOrientation == this.frontOrientation){
				this.setStyle(sameOrientationStyle);
				fTrack.setStyle(sameOrientationStyle);
			}
			//Otherwise, make them red
			else{
				this.setStyle(differentOrientationStyle);
				fTrack.setStyle(differentOrientationStyle);
			}
			fTrack = fTrack.frontTrack;
		}

		//loop through the backTrack list
		Track bTrack = this.backTrack;
		while(bTrack != null){
			//if their orientations match, make them green
			if(bTrack.frontOrientation == this.backOrientation){
				this.setStyle(sameOrientationStyle);
				bTrack.setStyle(sameOrientationStyle);
			}
			//Otherwise, make them red
			else{
				this.setStyle(differentOrientationStyle);
				bTrack.setStyle(differentOrientationStyle);
			}
			bTrack = bTrack.frontTrack;
		}
	}

	//Rotate the track's image by 360/numRotations degrees clockwise
	private void rotateCW(){

		this.frontOrientation += 360/this.numRotations;
		this.frontOrientation %= 360;

		this.backOrientation += 360/this.numRotations;
		this.backOrientation %= 360;

		if(this instanceof SwitchRightTrack){
			((SwitchRightTrack)this).sideOrientation  += 360/this.numRotations;
			((SwitchRightTrack)this).sideOrientation %= 360;
		}

		if(this instanceof SwitchLeftTrack){
			((SwitchLeftTrack)this).sideOrientation += 360/this.numRotations;
			((SwitchLeftTrack)this).sideOrientation %= 360;
		}

		this.setRotate(this.backOrientation);
		this.highlightTracks();
	}

	//Rotate the track's image by 360/numRotations degrees counter-clockwise
	private void rotateCCW(){

		this.frontOrientation -= 360/this.numRotations;
		this.frontOrientation += 360;
		this.frontOrientation %= 360;

		this.backOrientation -= 360/this.numRotations;
		this.backOrientation += 360;
		this.backOrientation %= 360;

		if(this instanceof SwitchRightTrack){
			((SwitchRightTrack)this).sideOrientation -= 360/this.numRotations;
			((SwitchRightTrack)this).sideOrientation += 360;
			((SwitchRightTrack)this).sideOrientation %= 360;
		}

		if(this instanceof SwitchLeftTrack){
			((SwitchLeftTrack)this).sideOrientation -= 360/this.numRotations;
			((SwitchLeftTrack)this).sideOrientation += 360;
			((SwitchLeftTrack)this).sideOrientation %= 360;
		}

		this.setRotate(this.backOrientation);
		this.highlightTracks();
	}

	//This method is called whenever the user wants to disconnect the selected
	//track from some other track
	private void disconnect(){
		this.disableDisconnectTrack();
		this.unlockConnectedTracks();
		this.locked = false;
		this.setLayoutX(this.getLayoutX() + 100);
		this.setLayoutY(this.getLayoutY() - 100);
	}

	//This method is called when this track is deleted
	//in order to unlock the tracks that were connected to the now-deleted track
	public void unlockConnectedTracks(){
		Track fTrack = this.frontTrack;
		Track bTrack = this.backTrack;

		//This track is now isolated, unlock it
		if(fTrack != null && fTrack.frontTrack == null){
			fTrack.locked = false;
			fTrack.disableDisconnectTrack();
		}

		//This track is now isolated, unlock it
		if(bTrack != null && bTrack.backTrack == null){
			bTrack.locked = false;
			bTrack.disableDisconnectTrack();
		}
	}

	//Disable the disconnectTrack option in this track's contextMenu
	private void disableDisconnectTrack(){
		ObservableList<MenuItem> list = this.contextMenu.getItems();
		for(int i = 0; i < list.size(); i++){
			MenuItem item = list.get(i);
			if(item.getId().equals("disconnect")){
				item.setDisable(true);
			}	
		}
	}

	//Enable the disconnectTrack option in this track's contextMenu
	private void enableDisconnectTrack(){
		ObservableList<MenuItem> list = this.contextMenu.getItems();
		for(int i = 0; i < list.size(); i++){
			MenuItem item = list.get(i);
			if(item.getId().equals("disconnect")){
				item.setDisable(false);
			}	
		}	
	}

	//When the user clicks the layoutArea, this method is called
	public static void layoutAreaClicked(Point2D p){
		for(Track t: TrainsGUI.tracks){
			if(t.contains(t.parentToLocal(p))){
				return;
			}
		}
		//if click not on top of track
		if(Track.selected != null){
			Track.selected.setStyle(unselectedStyle);
			Track.selected = null;
		}	
	}

	public static void generateGraph(){
		System.out.println("generate graph was called");	
	}

	//The following methods get the coordinates of the track in the parent coordinate system
	private Point2D getFrontLeft(){
		return this.localToParent(this.getWidth(),0);
	}

	private Point2D getFrontRight(){
		return this.localToParent(this.getWidth(), this.getHeight());
	}

	private Point2D getBackLeft(){
		return this.localToParent(0,0);
	}

	private Point2D getBackRight(){
		return this.localToParent(0, this.getHeight());
	}
}
