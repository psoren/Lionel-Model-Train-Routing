import java.io.FileNotFoundException;

//The offshoot track is 30 degrees off of straight track
public class SwitchLeftTrack extends Track{
	public SwitchLeftTrack(int x, int y, String imgSrc) throws FileNotFoundException{
		super(x,y,imgSrc);
		this.setHeight(60);
		this.setWidth(100);
		this.type = "SwitchLeft";
		this.numRotations = 12;
	}		
}
