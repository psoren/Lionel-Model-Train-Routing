import java.util.*;
import javafx.concurrent.*;

public class ControlTrainTask extends Task<Void>{

	//The id number of this train in decimal
	int trainID;

	public ControlTrainTask(int tID){
		this.trainID = tID;
	}

	@Override
	protected Void call() throws Exception{

		//PrintWriter out = new PrintWriter(TrainsGUI.socket.getOutputStream(), true);

		//This is for train 54
		//Figure out how to send stop command for a given trainID
		String reverse = "D127FE1B01BFDF";
		String absSpd3 = "D127FE1B635DDF";
		String halt = "D127FEFFFFDDDF";
		String thruCmd = "D13D141400009BDF";
		String outCmd = "D13D141401009ADF";

		//1. Get the list of train waypoints
		//For now, only getting the list at index 0

		//If the user has input a waypoint
		if(TrainWaypoint.trainWaypoints.size() >= 1){

			ArrayList<Track> trainWaypoints = TrainWaypoint.trainWaypoints.get(0);

			//Now create paths based on the waypoints
			ArrayList<ArrayList<Track>> trainPaths = new ArrayList<ArrayList<Track>>();
			for(int i = 0; i < trainWaypoints.size(); i++){
				ArrayList<Track> path;
				//If this is the last waypoint, get a path from this waypoint
				//to the first waypoint
				if(i == trainWaypoints.size()-1){
					path = getShortestPath(trainWaypoints.get(i), trainWaypoints.get(0));
				}
				//Otherwise, get the path from the track at index i to the track
				//at index i+1
				else{
					path = getShortestPath(trainWaypoints.get(i), trainWaypoints.get(i+1));
				}
				trainPaths.add(path);	
			}

			//Now we have a list of lists of tracks that are 
			//paths from one waypoint to another
			int currentPathNum = 0;
			while(true){
				ArrayList<Track> currentPath = trainPaths.get(currentPathNum);

				//1. Make sure all switch tracks are oriented correctly
				for(int i = 0; i < currentPath.size();i++){
					if((i < currentPath.size()-1) && currentPath.get(i) instanceof SwitchLeftTrack || 
							currentPath.get(i) instanceof SwitchRightTrack){
						//Then we know there is at least one more track after this track

						//TODO: Make sure this is based on the actual switch number, not
						//switch number 20

						//Then we know that the switchTrack needs to be set to the thru orientation
						if(currentPath.get(i+1) == currentPath.get(i).frontTrack ||
								currentPath.get(i+1) == currentPath.get(i).backTrack){
							//out.println(thruCmd);
						}
						else{
							//out.println(outCmd);
						}
					}
				}

				//2. Move train along currentPath





				if(currentPathNum == trainPaths.size()-1){currentPathNum = 0;}
				else{currentPathNum++;}
			}
		}
		return null;
	}
	
	
	public void sensorEvent(int sensorID, String direction){
		System.out.println("This train: " + this.trainID + " passed over sensor " + sensorID + " going " + direction);
	}
	
	
	

	//This method gets the formatted string of this train's ID
	//in decimal and pads it with 0s if necessary
	private String getFormattedIDDec(){	
		if(Integer.toString(this.trainID).length() == 1){return "0" + this.trainID;}
		else{return Integer.toString(this.trainID);}	
	}

	//This method gets the formatted string of this train's ID
	//in hexadecimal and pads it with 0s if necessary
	private String getFormattedIDHEx(){
		String hexID = Integer.toHexString(this.trainID);
		if(hexID.length() == 1){return "0" + hexID;}
		else{return hexID;}	
	}

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
}

//The hardcoded version of the train route
//while(true){
//	out.println(absSpd3);
//	Thread.sleep(3000);
//	
//	out.println(halt);
//	Thread.sleep(1000);
//	
//	out.println(reverse);
//	Thread.sleep(200);
//	
//	out.println(absSpd3);
//	Thread.sleep(2500);
//	
//	out.println(halt);
//	Thread.sleep(500);
//	
//	out.println(outCmd);
//	Thread.sleep(500);
//			
//	out.println(reverse);
//	Thread.sleep(200);
//	
//	
//	out.println(absSpd3);
//	Thread.sleep(3000);
//	
//	out.println(halt);
//	Thread.sleep(200);
//	
//	out.println(reverse);
//	Thread.sleep(200);
//	
//	out.println(absSpd3);
//	Thread.sleep(3000);
//	
//	out.println(thruCmd);
//	Thread.sleep(200);
//	
//	out.println(halt);
//	Thread.sleep(500);
//	
//	out.println(reverse);
//	Thread.sleep(500);	
//}
