import java.io.FileNotFoundException;

public class StraightTrack extends Track{
	public StraightTrack(int x, int y, String imgSrc) throws FileNotFoundException{
		super(x,y,imgSrc);
		this.imageView.setFitHeight(100);
		this.imageView.setFitWidth(50);
		this.type = "Straight";
		this.numRotations = 4;
	}
}
