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
	}		
	
	public Point2D getFrontCurveCoords(){		
		return this.localToParent(new Point2D(this.getWidth(), this.getHeight() - 40));
	}
	
	public Point2D getBackCurveCoords(){		
		return this.localToParent(new Point2D(0, 0));
	}
	
	
	
	
	
	
	
}
