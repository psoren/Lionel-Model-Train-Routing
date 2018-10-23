import java.io.*;
import javafx.scene.image.*;

public abstract class Track{

	//The type of track that this track is
	String type;

	//The track to the left of this track
	Track leftTrack;

	//The track to the right of this track
	Track rightTrack;

	//The number of rotations for a track to be back at the same position
	int numRotations;

	//The orientation of the track
	int orientation;

	//The image of the track
	ImageView imageView;

	/**
	 * @param x: the x coordinate of the image
	 * @param y: the y coordinate of the image
	 * @param imageSource: the source of the image
	 **/
	public Track(int x, int y, String imageSource) throws FileNotFoundException{
		this.leftTrack = null;
		this.rightTrack = null;
		this.orientation = 0;
		Image image = new Image(new FileInputStream(imageSource));
		this.imageView = new ImageView(image);
		this.imageView.setX(x);
		this.imageView.setY(y);
	}

	//Rotate the track's image by 360/numRotations degrees clockwise
	public void rotateCW(){

		int amtToRotate = 360/this.numRotations;

		if((this.orientation + amtToRotate) % 360 == 0){
			this.orientation = 0;
		}
		else{
			this.orientation += amtToRotate;
		}
		this.imageView.setRotate(this.orientation);
	}
	
	//Rotate the track's image by 360/numRotations degrees counter-clockwise
	public void rotateCCW(){

		int amtToRotate = 360/this.numRotations;

		if(this.orientation == 0){
			this.orientation = 360 - amtToRotate;
		}
		else{
			this.orientation -= amtToRotate;
		}
		this.imageView.setRotate(this.orientation);
	}
	
	public void delete(){
		System.out.println("Delete this track");
	}
}
