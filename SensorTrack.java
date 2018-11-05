import java.io.FileNotFoundException;

public class SensorTrack extends Track{
	public SensorTrack(int x, int y, String imgSrc) throws FileNotFoundException{
		super(x,y,imgSrc);
		this.setHeight(55);
		this.setWidth(50);
		this.numRotations = 12;
		this.frontOrientation = 0;
		this.backOrientation = 0;
	}
}
