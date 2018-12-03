import java.io.*;
import javafx.concurrent.*;

public class StopTrainsTask extends Task<Void>{

	@Override
	protected Void call() throws Exception{
		PrintWriter out = new PrintWriter(SocketCommunication.socket.getOutputStream(), true);
		String halt = "D127FEFFFFDDDF";
		out.println(halt);
		return null;	
	}
}
