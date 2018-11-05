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
	
	public Point2D getFrontCurveCoords(){		
		return this.localToParent(new Point2D(this.getWidth() - 20,-4));
	}
	
	public Point2D getBackCurveCoords(){		
		return this.localToParent(new Point2D(0, 40));
	}
	
}
