import java.io.FileNotFoundException;

import javafx.geometry.Point2D;

//The offshoot track is 30 degrees off of straight track
public class SwitchRightTrack extends Track{

	//The orientation of the side track
	public int sideOrientation;

	//The track to the side of this track
	public Track sideTrack;

	public SwitchRightTrack(int x, int y, String imgSrc) throws FileNotFoundException{
		super(x,y,imgSrc);
		this.setHeight(60);
		this.setWidth(100);
		this.numRotations = 12;
		this.frontOrientation = 0;
		this.backOrientation = 0;
		this.sideOrientation = 30;
		this.sideTrack = null;
	}	

	@Override
	public void rotateCW(){		
		this.frontOrientation += 360/this.numRotations;
		this.frontOrientation %= 360;

		this.backOrientation += 360/this.numRotations;
		this.backOrientation %= 360;

		this.sideOrientation  += 360/this.numRotations;
		this.sideOrientation %= 360;

		this.setRotate(this.backOrientation);
		this.highlightTracks();
	}

	@Override 
	public void rotateCCW(){
		this.frontOrientation -= 360/this.numRotations;
		this.frontOrientation += 360;
		this.frontOrientation %= 360;

		this.backOrientation -= 360/this.numRotations;
		this.backOrientation += 360;
		this.backOrientation %= 360;

		this.sideOrientation -= 360/this.numRotations;
		this.sideOrientation += 360;
		this.sideOrientation %= 360;

		this.setRotate(this.backOrientation);
		this.highlightTracks();
	}

	@Override
	Point2D getFrontCoords() {
		return this.localToParent(this.getWidth(),0);
	}
	
	@Override
	Point2D getBackCoords() {
		return this.localToParent(0,0);
	}
	
	Point2D getSideCoords(){
		return this.localToParent(this.getWidth() + 3, this.getHeight()-37);
	}
}
