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
	
	public Point2D getRightSwitchCoords(){		
		return this.localToParent(new Point2D(this.getWidth(), this.getHeight() - 40));
	}
}
