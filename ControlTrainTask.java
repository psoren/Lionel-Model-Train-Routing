import java.io.*;
import javafx.concurrent.*;

public class ControlTrainTask extends Task<Void>{

	//The id number of this train in decimal
	int trainID;

	public ControlTrainTask(int tID){
		this.trainID = tID;
	}

	@Override
	protected Void call() throws Exception{
		PrintWriter out = new PrintWriter(MatchingSensors.socket.getOutputStream(), true);

		//This is for train 54


		//figure out how to send stop command for a given trainID



		String reverse = "D127FE 1B 01 BF DF";




		return null;
	}

	private String getFormattedIDDec(){	
		if(Integer.toString(this.trainID).length() == 1){
			return "0" + this.trainID;
		}
		else{
			return Integer.toString(this.trainID);
		}	
	}

	private String getFormattedIDHEx(){

		String hexID = Integer.toHexString(this.trainID);
		
		if(hexID.length() == 1){
			return "0" + hexID;
		}
		else{
			return hexID;
		}	
	}

}
