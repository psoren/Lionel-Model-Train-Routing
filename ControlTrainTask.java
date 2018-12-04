import java.io.*;
import java.util.*;
import javafx.concurrent.*;

public class ControlTrainTask extends Task<Void>{

	//TODO: This is for train 54
	//Figure out how to send stop command for a given trainID
	private String reverse = "D127FE1B01BFDF";
	private String spd3 = "D127FE1B635DDF";
	private String spd2 = "D127FE1B625EDF";
	private String spd1 = "D127FE1B615FDF";
	private String halt = "D127FEFFFFDDDF";
	private String thruCmd = "D13D141400009BDF";
	private String outCmd = "D13D141401009ADF";

	//The speed of the train
	private String speed;

	//The id number of this train in decimal
	int trainID;

	//Initialized to -1 because no sensor has a value of -1
	int lastSensorPassed = -1;

	public ControlTrainTask(int tID){
		this.trainID = tID;
		this.speed = this.spd1;
	}

	PrintWriter out;

	ArrayList<ArrayList<Track>> trainPaths;

	//I made it synchronized because otherwise we
	//had to constantly print to System.out
	//otherwise it would not stop when it went over the sensor
	//Not sure why it works with synchronized and doesn't without
	@Override
	protected synchronized Void call() throws Exception{

		out = new PrintWriter(SocketCommunication.socket.getOutputStream(), true);

		//1. Get the list of train waypoints
		//For now, only getting the list at index 0
		//TODO: If there is more than one train, we will be
		//getting this based on which train we want to control

		//If the user has input waypoints
		if(TrainWaypoint.trainWaypoints.size() >= 1){

			ArrayList<Track> trainWaypoints = TrainWaypoint.trainWaypoints.get(0);

			//Now create paths based on the waypoints
			trainPaths = createTrainPaths(trainWaypoints);

			//Now we have a list of lists of tracks that are 
			//paths from one waypoint to another
			int currentPathNum = 0;
			boolean starting = true;

			while(true){

				System.out.println("On path " + currentPathNum);

				//Get the current path
				ArrayList<Track> currentPath = trainPaths.get(currentPathNum);

				//2. Check if direction of train needs to be changed
				if(!starting){
					if(changeTrainDirection(currentPathNum)){
						System.out.println("We need to change direction");
						out.println(reverse);
						Thread.sleep(500);}
					else{System.out.println("We do not need to change direction");}
				}

				//3. Move train along currentPath until we reach a sensor 
				//that tells us that we have reached the end of the path)

				//TODO: Have this work with non-sensor endpoints
				int endSensorLionelID = 0;
				if(!(currentPath.get(currentPath.size()-1) instanceof SensorTrack)){
					System.out.println("All endpoints need to be sensors (for now)");
				}
				else{
					//Get the sensor at the end of this waypoint
					SensorTrack endSensorTrack = (SensorTrack)currentPath.get(currentPath.size()-1);
					endSensorLionelID = Integer.parseInt(endSensorTrack.lionelID.substring(6, 8));
				}

				//Not sure what to do if there are multiple switchTracks in the same path
				//To work around this, simply make sure you do not input the waypoints in that way
				//If there is a switchTrack in the middle of this path,
				//we have to account for that

				boolean leftSwitchTrackInPath = false;
				boolean rightSwitchTrackInPath = false;

				for(Track t: currentPath){
					if(t instanceof SwitchLeftTrack){
						leftSwitchTrackInPath = true;
						break;
					}
					if(t instanceof SwitchRightTrack){
						rightSwitchTrackInPath = true;
						break;
					}
				}

				if(leftSwitchTrackInPath){
					runSwitchLeftTrackPath(currentPath, endSensorLionelID);
				}

				else if(rightSwitchTrackInPath){
					runSwitchRightTrackPath(currentPath, endSensorLionelID);
				}
				else{
					orientSwitches(currentPath);
					System.out.println("All switches are oriented correctly");
					runStraightTrackPath(endSensorLionelID);
				}

				if(currentPathNum == trainPaths.size()-1){
					out.println(reverse);
				}

				if(currentPathNum == trainPaths.size()-1){currentPathNum = 0;}
				else{currentPathNum++;}
				starting = false;
			}
		}
		return null;
	}

	//This method orients the switches for a given path
	private void orientSwitches(ArrayList<Track> currentPath){
		//1. Make sure all switch tracks are oriented correctly
		for(int i = 0; i < currentPath.size();i++){
			if((i < currentPath.size()-1) && currentPath.get(i) instanceof SwitchLeftTrack || 
					currentPath.get(i) instanceof SwitchRightTrack){
				//Then we know there is at least one more track after this track

				//TODO: Make sure this is based on the actual switch number, 
				//not switch number 20

				//The switchTrack needs to be set to the thru orientation
				if(currentPath.get(i+1) == currentPath.get(i).frontTrack ||
						currentPath.get(i+1) == currentPath.get(i).backTrack){
					out.println(thruCmd);
				}
				else{
					out.println(outCmd);
				}
			}
		}
		System.out.println("Switches have been oriented");
	}

	//This runs the trains on a path without a switchTrack
	private void runStraightTrackPath(int endSensor) throws Exception{
		out.println(this.speed);

		//Move train until we hit the sensor				
		while(this.lastSensorPassed != endSensor){
			Thread.sleep(10);
		}
		out.println(halt);
		Thread.sleep(500);
	}

	//This runs the trains on a path with a switchRightTrack
	private void runSwitchRightTrackPath(ArrayList<Track> path, int endSensor) throws Exception{
		//The only cases in which we have to do something complicated is when
		//1. You are going from the sideTrack to the frontTrack
		//2. You are going from the frontTrack to the sideTrack

		System.out.println("runSwitchRightTrackPath was called");

		if(path.get(0) instanceof SwitchRightTrack || 
				path.get(path.size()-1) instanceof SwitchRightTrack){
			System.out.println("You cannot make switches to be endpoints of paths");
			return;
		}

		for(int i = 0; i < path.size();i++){
			if(path.get(i) instanceof SwitchRightTrack){
				
				//Going from sideTrack to frontTrack
				if(path.get(i-1) == ((SwitchRightTrack)path.get(i)).sideTrack &&
						path.get(i+1) == ((SwitchRightTrack)path.get(i)).frontTrack){

					System.out.println("Going from sideTrack to frontTrack");
					int currentSensorPassed = this.lastSensorPassed;
					out.println(this.speed);

					//Move train until we hit the sensor				
					while(currentSensorPassed == this.lastSensorPassed){
						Thread.sleep(10);
					}

					out.println(halt);
					Thread.sleep(500);
					out.println(thruCmd);
					Thread.sleep(500);
					out.println(reverse);
					Thread.sleep(500);
					out.println(this.speed);	
				}

				//Going from frontTrack to sideTrack
				else if(path.get(i-1) == ((SwitchRightTrack)path.get(i)).frontTrack &&
						path.get(i+1) == ((SwitchRightTrack)path.get(i)).sideTrack){

					System.out.println("Going from frontTrack to sideTrack");
					int currentSensorPassed = this.lastSensorPassed;
					out.println(reverse);
					Thread.sleep(500);
					out.println(this.speed);

					//Move train until we hit the sensor				
					while(currentSensorPassed == this.lastSensorPassed){
						Thread.sleep(10);
					}

					out.println(halt);
					Thread.sleep(500);
					out.println(outCmd);
					Thread.sleep(500);
					out.println(reverse);
					Thread.sleep(500);
					out.println(this.speed);		
				}
				//Going straight through it
				else{
					System.out.println("It is a switchTrack, but we are going straight through it");
					orientSwitches(path);
					out.println(this.speed);
					runStraightTrackPath(endSensor);
				}
			}
		}
		//Move train until we hit the sensor				
		while(this.lastSensorPassed != endSensor){
			Thread.sleep(10);
		}
		out.println(halt);
		Thread.sleep(500);
	}

	//This runs the trains on a path with a switchLeftTrack
	private void runSwitchLeftTrackPath(ArrayList<Track> path, int endSensor) throws Exception{
		//This method is exactly the same as the method above it
		//but with SwitchLeftTracks
	}

	//This method determines if the train needs to change direction based on
	//the placement of the tracks in the current path and tthe previous path
	private boolean changeTrainDirection(int currentPathNum){
		ArrayList<Track> currentPath = this.trainPaths.get(currentPathNum);
		ArrayList<Track> previousPath;
		//If the current path is the first, we need to get the path at
		//the end of trainPaths
		if(currentPathNum == 0){
			previousPath = this.trainPaths.get(trainPaths.size()-1);
		}
		else{
			previousPath = this.trainPaths.get(currentPathNum-1);
		}
		return (previousPath.get(previousPath.size()-2) == currentPath.get(1));
	}

	//This method is called when this train goes over a sensor
	public void sensorEvent(int sensorID, String direction){
		this.lastSensorPassed = sensorID;
		System.out.println("-----------------------------");
		System.out.println("This train: " + this.trainID + " passed over sensor " + sensorID + " going " + direction);
		System.out.println("-----------------------------");
		
		
		//Draw this on the GUI
		
		
	}

	//This method is called whenever the user changes the speed in the UI
	public void updateSpeed(String id){

		if(id.equals("slow")){
			this.speed = this.spd1;
		}
		else if(id.equals("medium")){
			this.speed = this.spd2;
		}
		else if(id.equals("fast")){
			this.speed = this.spd3;
		}
		else{
			System.out.println("Something was wrong when the speed was set.");
		}
	}

	//This method gets the formatted string of this train's ID
	//in decimal and pads it with 0s if necessary
	/*private String getFormattedIDDec(){	
		if(Integer.toString(this.trainID).length() == 1){return "0" + this.trainID;}
		else{return Integer.toString(this.trainID);}	
	}*/

	//This method gets the formatted string of this train's ID
	//in hexadecimal and pads it with 0s if necessary
	/*private String getFormattedIDHEx(){
		String hexID = Integer.toHexString(this.trainID);
		if(hexID.length() == 1){return "0" + hexID;}
		else{return hexID;}	
	}*/

	//This method takes in two tracks and returns the path based on the
	//shortest distance between those two paths
	//starting at one waypoint
	//and ending at another waypoint
	//(Uses Dijkstra's Shortest Path Algorithm)
	//if there was not a path, we get null
	private ArrayList<Track> getShortestPath(Track start, Track end){

		ArrayList<Track> allVertices = new ArrayList<Track>();
		HashMap<Track, Integer> distances = new HashMap<Track, Integer>();
		HashMap<Track, Track> previous = new HashMap<Track, Track>();

		for(Track t: TrainsGUI.tracks){
			distances.put(t, Integer.MAX_VALUE);
			previous.put(t, null);
			allVertices.add(t);
		}
		distances.put(start, 0);

		while(!allVertices.isEmpty()){
			//get the element of vertices with the smallest distance
			int smallestDistance = Integer.MAX_VALUE;
			Track closestTrack = null;

			for(Track t: allVertices){
				if(distances.get(t) <= smallestDistance){
					smallestDistance = distances.get(t);
					closestTrack = t;
				}
			}

			//remove the closest track from the list of vertices
			allVertices.remove(closestTrack);
			if(closestTrack == end){
				ArrayList<Track> path = new ArrayList<Track>();
				Track u = end;
				if(previous.get(end) != null || end == start){
					while(u != null){
						path.add(u);
						u = previous.get(u);
					}
				}
				Collections.reverse(path);
				return path;
			}

			//for all neighbors of closestTrack
			for(Track t: closestTrack.getNeighbors()){
				int alt = distances.get(closestTrack) + t.length;
				if(alt < distances.get(t)){
					distances.put(t, alt);
					previous.put(t,closestTrack);
				}
			}
		}
		return null;
	}

	//Creates paths from a given set of waypoints
	private ArrayList<ArrayList<Track>> createTrainPaths(ArrayList<Track> trainWaypoints){
		trainPaths = new ArrayList<ArrayList<Track>>();
		for(int i = 0; i < trainWaypoints.size(); i++){
			ArrayList<Track> path;
			//If this is the last waypoint, get a path from this waypoint
			//to the first waypoint
			if(i == trainWaypoints.size()-1){
				path = getShortestPath(trainWaypoints.get(i), trainWaypoints.get(0));
			}
			//Otherwise, get the path from the track at index i to the track at index i+1
			else{
				path = getShortestPath(trainWaypoints.get(i), trainWaypoints.get(i+1));
			}
			trainPaths.add(path);	
		}
		return trainPaths;
	}
}
