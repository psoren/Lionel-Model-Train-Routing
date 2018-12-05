import java.io.*;
import java.util.ArrayList;
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

	/**Constants**/
	private int SNAPTOGETHERDISTANCE = 40;

	/**Static fields**/
	//This is a static field that is set whenever a track is selected
	public static Track selected;
	public static final String selectedStyle = "-fx-border-color: black; -fx-border-width: 2;";
	public static final String unselectedStyle = "-fx-border-style: none";

	/**Non-static fields**/
	//The number of rotations for a track to be back at the same position
	protected int numRotations;

	//The length of this track in inches
	protected int length;

	//The track in front of this track
	public Track frontTrack;

	//The track to the back of this track
	public Track backTrack;

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

		FileInputStream stream = new FileInputStream(imageSource);
		Image image = new Image(stream);		
		ImageView imageView = new ImageView(image);

		this.getChildren().addAll(new Rectangle((int)imageView.getFitWidth(), 
				(int)imageView.getFitHeight(), Color.BLACK), imageView);

		enableDragging(this);

		this.setLayoutX(x);
		this.setLayoutY(y);

		this.addEventHandler(MouseEvent.MOUSE_PRESSED, e ->{

			//this.printDebugInfo();

			//Only allow user to select a track when
			//they are in the trackLayout screen
			if(TrainsGUI.currentScene.equals("trackLayout")){

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

					//If the front of this track is close to the back of the other track
					if(this.getFrontCoords().distance(t.getBackCoords()) < SNAPTOGETHERDISTANCE
							&& this.frontOrientation == t.backOrientation){	
						this.setLayoutX(this.getLayoutX() + t.getBackCoords().getX() - this.getFrontCoords().getX());
						this.setLayoutY(this.getLayoutY() + t.getBackCoords().getY() - this.getFrontCoords().getY());

						this.frontTrack = t;
						t.backTrack = this;

						snapped = true;
					}

					//If the back of this track is close to the front of the other track
					else if(this.getBackCoords().distance(t.getFrontCoords()) < SNAPTOGETHERDISTANCE
							&& this.backOrientation == t.frontOrientation){
						this.setLayoutX(this.getLayoutX() + t.getFrontCoords().getX() - this.getBackCoords().getX());
						this.setLayoutY(this.getLayoutY() + t.getFrontCoords().getY() - this.getBackCoords().getY());

						this.backTrack = t;
						t.frontTrack = this;

						snapped = true;
					}

					else if(t instanceof SwitchRightTrack){
						//If the back of this track is close to the side of the right switch track
						if(this.getBackCoords().distance(((SwitchRightTrack)t).getSideCoords()) < SNAPTOGETHERDISTANCE
								&& this.backOrientation == ((SwitchRightTrack)t).sideOrientation){
							this.setLayoutX(this.getLayoutX() + ((SwitchRightTrack)t).getSideCoords().getX() - this.getBackCoords().getX());
							this.setLayoutY(this.getLayoutY() + ((SwitchRightTrack)t).getSideCoords().getY() - this.getBackCoords().getY());

							this.backTrack = t;
							((SwitchRightTrack)t).sideTrack = this;

							snapped = true;
						}
					}

					else if(t instanceof SwitchLeftTrack){
						//If the back of this track is close to the side of the right switch track
						if(this.getBackCoords().distance(((SwitchLeftTrack)t).getSideCoords()) < SNAPTOGETHERDISTANCE
								&& this.backOrientation == ((SwitchLeftTrack)t).sideOrientation){
							this.setLayoutX(this.getLayoutX() + ((SwitchLeftTrack)t).getSideCoords().getX() - this.getBackCoords().getX());
							this.setLayoutY(this.getLayoutY() + ((SwitchLeftTrack)t).getSideCoords().getY() - this.getBackCoords().getY());

							this.backTrack = t;
							((SwitchLeftTrack)t).sideTrack = this;

							snapped = true;
						}
					}

					//If you are dragging the side of a switchRightTrack towards the back of another track
					else if (this instanceof SwitchRightTrack){
						if(((SwitchRightTrack)this).getSideCoords().distance(t.getBackCoords()) < SNAPTOGETHERDISTANCE){
							this.setLayoutX(this.getLayoutX() - ((SwitchRightTrack)this).getSideCoords().getX() + t.getBackCoords().getX());
							this.setLayoutY(this.getLayoutY() - ((SwitchRightTrack)this).getSideCoords().getY() + t.getBackCoords().getY());

							((SwitchRightTrack)this).sideTrack = t;
							t.backTrack = this;

							snapped = true;
						}
					}

					//If you are dragging the side of a switchLeftTrack towards the back of another track
					else if (this instanceof SwitchLeftTrack){
						if(((SwitchLeftTrack)this).getSideCoords().distance(t.getBackCoords()) < SNAPTOGETHERDISTANCE){
							this.setLayoutX(this.getLayoutX() - ((SwitchLeftTrack)this).getSideCoords().getX() + t.getBackCoords().getX());
							this.setLayoutY(this.getLayoutY() - ((SwitchLeftTrack)this).getSideCoords().getY() + t.getBackCoords().getY());

							((SwitchLeftTrack)this).sideTrack = t;
							t.backTrack = this;

							snapped = true;
						}
					}

					if(snapped){
						this.locked = true;
						t.locked = true;

						this.enableDisconnectTrack();
						t.enableDisconnectTrack();

						break;
					}
				}
			}		
		});
	}

	//Rotate the track's image by 360/numRotations degrees clockwise
	protected void rotateCW(){
		this.frontOrientation += 360/this.numRotations;
		this.frontOrientation %= 360;

		this.backOrientation += 360/this.numRotations;
		this.backOrientation %= 360;

		this.setRotate(this.backOrientation);
	}

	//Rotate the track's image by 360/numRotations degrees counter-clockwise
	protected void rotateCCW(){

		this.frontOrientation -= 360/this.numRotations;
		this.frontOrientation += 360;
		this.frontOrientation %= 360;

		this.backOrientation -= 360/this.numRotations;
		this.backOrientation += 360;
		this.backOrientation %= 360;

		this.setRotate(this.backOrientation);
	}

	//This method is called whenever the user wants to 
	//disconnect the selected track from some other track
	private void disconnect(){
		this.disableDisconnectTrack();
		this.unlockConnectedTracks();
		this.locked = false;
		this.setLayoutX(this.getLayoutX() + 100);
		this.setLayoutY(this.getLayoutY() - 100);
	}

	//This method is called when this track 
	//is deleted or disconnected
	public void unlockConnectedTracks(){
		if(this instanceof SwitchLeftTrack || this instanceof SwitchRightTrack){

			Track sTrack = null;

			if(this instanceof SwitchLeftTrack){
				sTrack = ((SwitchLeftTrack)this).sideTrack;
				((SwitchLeftTrack)this).sideTrack = null;
			}

			if(this instanceof SwitchRightTrack){
				sTrack = ((SwitchRightTrack)this).sideTrack;
				((SwitchRightTrack)this).sideTrack = null;
			}

			if(sTrack != null && sTrack.isIsolated()){
				sTrack.locked = false;
				sTrack.disableDisconnectTrack();
			}
			if(sTrack != null){
				sTrack.resetTracks(this);
			}
		}

		if(this.frontTrack != null){
			this.frontTrack.resetTracks(this);
		}

		if(this.backTrack != null){
			this.backTrack.resetTracks(this);
		}

		//This track is now isolated, unlock it
		if(this.frontTrack != null && this.frontTrack.isIsolated()){
			this.frontTrack.locked = false;
			this.frontTrack.disableDisconnectTrack();
		}

		//This track is now isolated, unlock it
		if(this.backTrack != null && this.backTrack.isIsolated()){
			this.backTrack.locked = false;
			this.backTrack.disableDisconnectTrack();
		}

		this.frontTrack = null;
		this.backTrack = null;

		if(this.isIsolated()){
			this.locked = false;
			this.disableDisconnectTrack();
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
		//If click not on top of track
		if(Track.selected != null){
			Track.selected.setStyle(unselectedStyle);
			Track.selected = null;
		}	
	}

	//This method will be called when the waypoint area is clicked
	//so that we can add that track to that train's waypoint list
	public static Track getClickedTrack(Point2D p){
		for(Track t: TrainsGUI.tracks){
			if(t.contains(t.parentToLocal(p))){
				return t;
			}
		}
		return null;
	}

	//A method to print info about this track
	private void printDebugInfo(){
		if(this.frontTrack != null){System.out.println("this track is connected at the front to a " + this.frontTrack.getClass());}		
		if(this.backTrack != null){System.out.println("this track is connected at the back to a " + this.backTrack.getClass());}

		if(this instanceof SwitchLeftTrack && ((SwitchLeftTrack)this).sideTrack != null){
			System.out.println("this track is connected on the side to a " + ((SwitchLeftTrack)this).sideTrack.getClass());}

		if(this instanceof SwitchRightTrack && ((SwitchRightTrack)this).sideTrack != null){
			System.out.println("this track is connected on the side to a " + ((SwitchRightTrack)this).sideTrack.getClass());}

		ArrayList<Track> reachable = TrackLayout.DFSinit(this);
		System.out.println("You can reach " + reachable.size() + " tracks from this track.\n");
	}

	//Gets the front left of the track in its respective
	//coordinate system and returns it in the parent coordinate system
	abstract Point2D getFrontCoords();

	//Gets the back left of the track in its respective
	//coordinate system and returns it in the parent coordinate system
	abstract Point2D getBackCoords();

	boolean isIsolated(){
		return this.frontTrack == null && this.backTrack == null;
	}

	//A method called when disconnecting tracks
	protected void resetTracks(Track t){
		if(this.frontTrack == t){
			this.frontTrack = null;
		}
		else if(this.backTrack == t){
			this.backTrack = null;
		}	
	}

	public ArrayList<Track> getNeighbors(){
		ArrayList<Track> neighbors = new ArrayList<Track>();

		if(this.frontTrack != null){
			neighbors.add(frontTrack);
		}

		if(this.backTrack != null){
			neighbors.add(backTrack);
		}
		return neighbors;
	}
}
