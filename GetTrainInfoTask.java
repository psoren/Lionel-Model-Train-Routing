import java.io.*;
import javafx.concurrent.*;

public class GetTrainInfoTask extends Task<String>{
	@Override
	protected String call() throws Exception{
		PrintWriter out = new PrintWriter(MatchingSensors.socket.getOutputStream(), true);
		out.println("D1010004FBDF");
		return "";
	}
}
