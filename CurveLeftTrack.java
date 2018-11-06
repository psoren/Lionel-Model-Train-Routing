import java.io.FileNotFoundException;

import javafx.geometry.Point2D;

public class CurveLeftTrack extends Track{
	public CurveLeftTrack(int x, int y, String imgSrc) throws FileNotFoundException{
		super(x,y,imgSrc);
		this.setHeight(60);
		this.setWidth(100);
		this.numRotations = 12;
		this.frontOrientation = 330;
		this.backOrientation = 0;
	}

	@Override
	Point2D getFrontCoords() {
		return this.localToParent(this.getWidth()-20, -5);
	}
	
	@Override
	Point2D getBackCoords() {
		return this.localToParent(0, this.getHeight()-50);
	}
}
