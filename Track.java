import java.io.*;
import javafx.beans.property.*;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public abstract class Track extends StackPane{

	/**Static fields**/
	//This is a static field that is set whenever a track is selected
	public static Track selected;

	private String selectedStyle = "-fx-border-color: black; -fx-border-width: 2;";
	private String unselectedStyle = "-fx-border-style: none";
	private String sameOrientationStyle = "-fx-border-color: green";
	private String differentOrientationStyle = "-fx-border-color: red";

	/**Non-static fields**/
	//The type of track that this track is
	protected String type;

	//The number of rotations for a track to be back at the same position
	protected int numRotations;

	//The track in front of this track
	private Track frontTrack;

	//The track to the back of this track
	private Track backTrack;

	//The orientation of the front of the track (How many degrees off of 0 it is)
	private int frontOrientation;

	//The orientation of the back of the track (How many degrees off of 0 it is)
	private int backOrientation;

	//If this track is "locked onto" another track
	private boolean locked;

	/**
	 * @param x: the x coordinate of the image
	 * @param y: the y coordinate of the image
	 * @param imageSource: the source of the image
	 **/	
	public Track(int x, int y, String imageSource) throws FileNotFoundException{
		super();
		this.frontTrack = null;
		this.backTrack = null;
		this.frontOrientation = 0;
		this.backOrientation = 0;
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
		ContextMenu contextMenu = new ContextMenu();

		MenuItem rotateCW = new MenuItem("Rotate Clockwise");
		rotateCW.setOnAction(e->{
			this.rotateCW();
		});

		MenuItem rotateCCW = new MenuItem("Rotate Counter-clockwise");
		rotateCCW.setOnAction(e->{
			this.rotateCCW();
		});

		contextMenu.getItems().addAll(rotateCW, rotateCCW);

		this.setOnContextMenuRequested(e-> {
			contextMenu.show(this, e.getScreenX(), e.getScreenY());
		});
	}

	private void enableDragging(Track track){
		final ObjectProperty<Point2D> mouseAnchor = new SimpleObjectProperty<>();
		this.setOnMousePressed(e -> {
			mouseAnchor.set(new Point2D(e.getSceneX(), e.getSceneY()));
		});
		this.setOnMouseDragged(e -> {
			double deltaX = e.getSceneX() - mouseAnchor.get().getX();
			double deltaY = e.getSceneY() - mouseAnchor.get().getY();
			this.relocate(this.getLayoutX() + deltaX, this.getLayoutY() + deltaY);
			mouseAnchor.set(new Point2D(e.getSceneX(), e.getSceneY()));

			//The "snap-to" behavior
			for(Track t: TrainsGUI.tracks){

				//Setting the position of the selected track to be
				//"at the end" of the other track that is close
				if(this.distanceBetweenThisTopAndOtherBottom(t) < 40){
					//Set the location of the track
					//This is somewhat complicated because we have to account
					//for the different coordinate systems of the main canvas 
					//and each track

					//if the orientations match and the track to be connected to
					//does not have backTrack, snap them together


					//Figure out how to ensure that you cant snap a track
					//to a place that already has a track connected to it
					if(this.frontOrientation == t.frontOrientation){

						//we dont want to have two tracks connected to the same place
						this.setLayoutX(this.getLayoutX() + t.getBackLeft().getX() - this.getFrontLeft().getX());
						this.setLayoutY(this.getLayoutY() + t.getBackLeft().getY() - this.getFrontLeft().getY());

						this.locked = true;
						t.locked = true;

						this.frontTrack = t;
						t.backTrack = this;
						this.highlightTracks();
						break;
					}	
				}
				else if(this.distanceBetweenThisBottomAndOtherTop(t) < 40){
					//if the orientations match, snap them together
					if(this.frontOrientation == t.frontOrientation){
						this.setLayoutX(this.getLayoutX() + t.getFrontLeft().getX() - this.getBackLeft().getX());
						this.setLayoutY(this.getLayoutY() + t.getFrontLeft().getY() - this.getBackLeft().getY());

						this.locked = true;
						t.locked = true;

						this.backTrack = t;
						t.frontTrack = this;
						this.highlightTracks();
						break;
					}
				}
			}
		});
	}

	//Returns the distance between the top of this track
	//and the bottom of the other one
	private int distanceBetweenThisTopAndOtherBottom(Track track){
		int x1 = (int)this.getFrontLeft().getX();
		int x2 = (int)track.getBackLeft().getX();

		int y1 = (int)this.getFrontLeft().getY();
		int y2 = (int)track.getBackLeft().getY();

		//The distance between this track and the bottom of the other track
		return (int)Math.sqrt(Math.pow((x1-x2), 2) + Math.pow((y1-y2),2));
	}

	//Returns the distance between the top of this track
	//and the bottom of the other one
	private int distanceBetweenThisBottomAndOtherTop(Track track){
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
			if(fTrack.frontOrientation == this.frontOrientation){
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
			if(bTrack.frontOrientation == this.frontOrientation){
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
	//change this so it accounts for the change in height
	private void rotateCW(){
		int amtToRotate = 360/this.numRotations;
		//if((this.orientation + amtToRotate) % 360 == 0){this.orientation = 0;}
		//else{this.orientation += amtToRotate;}
		//this.setRotate(this.orientation);
		this.frontOrientation += amtToRotate;
		if(this.frontOrientation == 360){
			this.frontOrientation = 0;
		}
		this.setRotate(this.frontOrientation);
		this.highlightTracks();
	}

	//Rotate the track's image by 360/numRotations degrees counter-clockwise
	private void rotateCCW(){
		int amtToRotate = 360/this.numRotations;
		//if(this.orientation == 0){this.orientation = 360 - amtToRotate;}
		//else{this.orientation -= amtToRotate;}
		//this.setRotate(this.orientation);
		this.frontOrientation -= amtToRotate;
		if(this.frontOrientation == -360){
			this.frontOrientation = 0;
		}
		this.setRotate(this.frontOrientation);
		this.highlightTracks();
	}

	//This method is called when this track is deleted
	//in order to unlock the tracks that were connected to the now-deleted track
	public void unlockConnectedTracks(){
		Track fTrack = this.frontTrack;
		Track bTrack = this.backTrack;

		//This track is now isolated, unlock it
		if(fTrack != null && fTrack.frontTrack == null){
			fTrack.locked = false;
		}

		//This track is now isolated, unlock it
		if(bTrack != null && bTrack.backTrack == null){
			bTrack.locked = false;
		}
	}

	//The following methods get the coordinates of the track in the parent coordinate system
	private Point2D getFrontLeft(){
		return this.localToParent(0,0);
	}

	private Point2D getFrontRight(){
		return this.localToParent((int)this.getWidth(), 0);
	}

	private Point2D getBackLeft(){
		return this.localToParent(0, (int)this.getHeight());
	}

	private Point2D getBackRight(){
		return this.localToParent((int)this.getWidth(), this.getHeight());
	}
}
