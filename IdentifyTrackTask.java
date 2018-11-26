import java.io.*;
import javafx.concurrent.*;

public class IdentifyTrackTask extends Task<String>{
	
	String sensorID = "";
	
	@Override
	protected String call() throws Exception{
		PrintWriter out = new PrintWriter(MatchingSensors.socket.getOutputStream(), true);
		
		out.println("the identify track command");
		//or maybe just switch the direction of the track so we know which
		//one it is
		
		return "";
	}
}
