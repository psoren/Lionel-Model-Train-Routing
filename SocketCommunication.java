import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.*;
import javafx.concurrent.Task;

public class SocketCommunication {

	static ExecutorService executor;
	static Socket socket = null;

	String hostName = "192.168.99.1";
	int portNumber = 50001;

	static String mostRecentCommand;

	Task<Void> pingSocket;

	public SocketCommunication(TrainsGUI gui){

		//Initialize socket and executor
		try {
			SocketCommunication.socket = new Socket(hostName, portNumber);
		} 
		catch (IOException e) {
			System.err.println("Could not connect to Lionel Wifi");
		}
		SocketCommunication.executor = Executors.newFixedThreadPool(4);

		//Constantly pinging the sensor and getting input back
		pingSocket = new PingSocketTask();
		executor.submit(pingSocket);

		//This will fire whenever the message property of the pingSocketTask changes
		//This happens when we are matching sensors
		//or when a train goes over a sensor
		pingSocket.messageProperty().addListener((obs, oldMsg, newMsg) -> {
			if(mostRecentCommand == "getInfo"){
				String[] messages = newMsg.split(" ");
				gui.matchingSensors.updateUI(messages);
				mostRecentCommand = "";
			}

			//A train has passed over a sensor
			else{
				if(newMsg.length() >= 140){
					int sensorID = Integer.parseInt(newMsg.substring(4, 6), 16);
					int directionNum = Integer.parseInt(newMsg.substring(17,18));
					String direction = "";
					if(directionNum == 1){direction = "right";}
					else if(directionNum == 0){direction = "left";}
					int trainIDNum = Integer.parseInt(newMsg.substring(18,20),16) -1;
					gui.trainRunning.sensorInfo(sensorID, direction, trainIDNum);
				}
			}
		});
	}

	//This method is called when the the executorService is shutdown
	//(When the trains need to be stopped)
	public static void createNewExecutor(){
		executor = Executors.newFixedThreadPool(4);
	}
}
