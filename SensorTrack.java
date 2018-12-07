//Written by Parker Sorenson
//CS440 Fall 2018

import java.io.FileNotFoundException;
import javafx.geometry.Point2D;

public class SensorTrack extends Track{
	
	//The ID of this track in the Wifi module
	public String lionelID;
	
	public SensorTrack(int x, int y, String imgSrc) throws FileNotFoundException{
		super(x,y,imgSrc);
		this.setHeight(50);
		this.setWidth(50);
		this.numRotations = 12;
		this.frontOrientation = 0;
		this.backOrientation = 0;
		this.length = 5;
		this.lionelID = "";
	}

	@Override
	Point2D getFrontCoords() {
		return this.localToParent(this.getWidth(),0);
	}	
	
	@Override
	Point2D getBackCoords() {
		return this.localToParent(0,0);
	}	
}
