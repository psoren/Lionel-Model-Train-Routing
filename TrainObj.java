import java.util.Queue;

public class TrainObj {

	String name;
	String inDir; // initial Direction either R or L
	
	Queue myStops; //List of stops the user would like the train to make
	
	String startLocation; // is a #
	String localTest; //test variable that will just have the name of the most recent sensor visited
	
	double length; // how long is the train 
	int bounds; // the error bounds to be implemented later
	int stopNum; // the number stop the train is at, used to reference the myStops queue
	int nextStop; // the next station the train comes to 
	
	double weight; //how heavy the train is, potentially implemented later if there is time
	double velocity; // How fast the train is moving. In inches per second, LT is 1:48 scale or type (O)
	
	
	public TrainObj (String n, String d, Queue m, int l) {
	
		this.myStops = m;
		this.length = l;
	}
	
	public void setName(String n) {
		this.name = n;
	}
	public String getName() {
		return name;
	}
	
	public void setInDir(String d) {
		this.inDir = d;
	}
	public String getInDir() {
		return inDir;
	}

	public void setLength(int l) {
		this.length = l;
	}
	public double getLength() {
		return length;
	}


	public int getBounds() {
		return bounds;
	}


	public double getWeight() {
		return weight;
	}
	
	public double getVelocity() {
		return velocity;
		
	}
	
	public int getNextStop() {
		return nextStop;
	}
	
	public Queue getMyStops() {
		return null;
	}
	
	public void mvTrLocInd () { // update train location indicators for the front and back
		//TODO 
	}
	
	
}
