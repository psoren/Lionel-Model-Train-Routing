//Written by Parker Sorenson
//CS440 Fall 2018

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.geometry.Point2D;

//The offshoot track is 30 degrees off of straight track
public class SwitchLeftTrack extends Track{

	//The ID of this track in the Wifi module
	public String lionelID;

	//The orientation of the side track
	public int sideOrientation;

	//The track to the side of this track
	public Track sideTrack;

	public SwitchLeftTrack(int x, int y, String imgSrc) throws FileNotFoundException{
		super(x,y,imgSrc);
		this.setHeight(60);
		this.setWidth(100);
		this.numRotations = 12;
		this.frontOrientation = 0;
		this.backOrientation = 0;
		this.sideOrientation = 330;
		this.sideTrack = null;
		this.length = 15;
		this.lionelID = "";
	}	

	@Override
	public void rotateCW(){
		this.frontOrientation += 360/this.numRotations;
		this.frontOrientation %= 360;

		this.backOrientation += 360/this.numRotations;
		this.backOrientation %= 360;

		this.sideOrientation += 360/this.numRotations;
		this.sideOrientation %= 360;

		this.setRotate(this.backOrientation);
		//this.highlightTracks();
	}

	@Override
	public void rotateCCW(){
		this.frontOrientation -= 360/this.numRotations;
		this.frontOrientation += 360;
		this.frontOrientation %= 360;

		this.backOrientation -= 360/this.numRotations;
		this.backOrientation += 360;
		this.backOrientation %= 360;

		this.sideOrientation -= 360/this.numRotations;
		this.sideOrientation += 360;
		this.sideOrientation %= 360;

		this.setRotate(this.backOrientation);
		//this.highlightTracks();
	}

	@Override
	Point2D getFrontCoords() {
		return this.localToParent(this.getWidth(), this.getHeight()-50);
	}

	@Override
	Point2D getBackCoords() {
		return this.localToParent(0, this.getHeight()-50);
	}
	
	@Override
	//A method called when disconnecting tracks
	protected void resetTracks(Track t){
		if(this.frontTrack != null && this.frontTrack == t){
			this.frontTrack = null;
		}
		else if(this.backTrack != null && this.backTrack == t){
			this.backTrack = null;
		}
		else if(this.sideTrack != null && this.sideTrack == t){
			this.sideTrack = null;
		}	
	}
	
	@Override
	boolean isIsolated(){
		return this.frontTrack == null && this.backTrack == null && this.sideTrack == null;
	}

	public Point2D getSideCoords(){
		return this.localToParent(this.getWidth()-30, 0);
	}
	
	@Override
	public ArrayList<Track> getNeighbors(){
		ArrayList<Track> neighbors = new ArrayList<Track>();
		
		if(this.frontTrack != null){
			neighbors.add(frontTrack);
		}
		
		if(this.backTrack != null){
			neighbors.add(backTrack);
		}
		
		if(this.sideTrack != null){
			neighbors.add(sideTrack);
		}
		return neighbors;
	}
}
