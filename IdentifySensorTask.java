import java.io.*;
import javafx.concurrent.*;

public class IdentifySensorTask extends Task<String>{
	
	String sensorID = "";
	
	@Override
	protected String call() throws Exception{
		PrintWriter out = new PrintWriter(MatchingSensors.socket.getOutputStream(), true);
		
		out.println("the identify sensor command");	
		
		return "";
	}
}
