import java.io.FileNotFoundException;

public class SensorTrack extends Track{
	public SensorTrack(int x, int y, String imgSrc) throws FileNotFoundException{
		super(x,y,imgSrc);
		this.setHeight(50);
		this.setWidth(55);
		this.type = "Sensor";
		this.numRotations = 12;
	}
}
