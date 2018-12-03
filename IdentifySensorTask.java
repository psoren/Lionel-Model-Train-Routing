import java.io.*;
import javafx.concurrent.*;

public class IdentifySensorTask extends Task<Void>{
	
	String sensorID;
	
	public IdentifySensorTask(String sID){
		this.sensorID = sID;
	}
	
	@Override
	protected Void call() throws Exception{
		PrintWriter out = new PrintWriter(TrainsGUI.socket.getOutputStream(), true);
		
		//sensorID is of the form "sensorXX" where XX is the number
		//of the sensor that we need to identify
		int sensorNumberDec = Integer.parseInt(this.sensorID.substring(6, 8));
		
		//convert sensor number to hex
		String sensorNumberHex = Integer.toHexString(sensorNumberDec);
		sensorNumberHex = sensorNumberHex.toUpperCase();
		
		//If the length is short, we need to add a 0 to the front of it
		if(sensorNumberHex.length() == 1){
			sensorNumberHex = "0" + sensorNumberHex;
		}
		
		//Get the correct number for the command
		int commandDecOn =  199 - sensorNumberDec;
		int commandDecOff = commandDecOn + 1;
		
		//Now we need to convert them to hexadecimal
		String commandHexOn = Integer.toHexString(commandDecOn);
		commandHexOn = commandHexOn.toUpperCase();
		//If the length is short, we need to add a 0 to the front of it
		if(commandHexOn.length() == 1){
			commandHexOn = "0" + commandHexOn;
		}
		
		String commandHexOff = Integer.toHexString(commandDecOff);
		commandHexOff = commandHexOff.toUpperCase();
		//If the length is short, we need to add a 0 to the front of it
		if(commandHexOff.length() == 1){
			commandHexOff = "0" + commandHexOff;
		}
		String turnOnLEDCommand = "D131" + sensorNumberHex + "0701" + commandHexOn + "DF";
		String turnOffLEDCommand = "D131" + sensorNumberHex + "0700" + commandHexOff + "DF";

		//Turn on and off the LED to identify the track
		out.println(turnOnLEDCommand);	
		Thread.sleep(1000);
		out.println(turnOffLEDCommand);	

		return null;
	}
}
