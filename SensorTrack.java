import java.io.FileNotFoundException;

public class SensorTrack extends Track{
	public SensorTrack(int x, int y, String imgSrc) throws FileNotFoundException{
		super(x,y,imgSrc);
		this.imageView.setFitHeight(100);
		this.imageView.setFitWidth(55);
		this.type = "Sensor";
		this.numRotations = 4;
	}
}
