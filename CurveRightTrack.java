//Written by Parker Sorenson
//CS440 Fall 2018

import java.io.FileNotFoundException;
import javafx.geometry.Point2D;

public class CurveRightTrack extends Track{
	public CurveRightTrack(int x, int y, String imgSrc) throws FileNotFoundException{
		super(x,y,imgSrc);
		this.setHeight(60);
		this.setWidth(100);
		this.numRotations = 12;
		this.frontOrientation = 30;
		this.backOrientation = 0;
		this.length = 15;
	}	
	
	@Override
	Point2D getFrontCoords() {
		return this.localToParent(this.getWidth(),this.getHeight()-40);
	}
	
	@Override
	Point2D getBackCoords() {
		return this.localToParent(0,0);
	}
}
