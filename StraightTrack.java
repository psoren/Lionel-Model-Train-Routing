import java.io.FileNotFoundException;

public class StraightTrack extends Track{
	public StraightTrack(int x, int y, String imgSrc) throws FileNotFoundException{
		super(x,y,imgSrc);
		this.setHeight(100);
		this.setWidth(50);
		this.type = "Straight";
		this.numRotations = 12;
	}		
}
