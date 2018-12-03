import java.io.*;
import javafx.concurrent.*;

public class PingSocketTask extends Task<Void>{

	String msg = "";
	
	@Override
	public Void call(){
		try{
			PrintWriter out = new PrintWriter(TrainsGUI.socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(TrainsGUI.socket.getInputStream()));
			System.out.println("pinging...");
			while(true){
				out.println("D129D7DF");
				char[] buffer = new char[1024];
				int read;
				if ((read = in.read(buffer)) != -1) {
					String output = new String(buffer, 0, read);
										
					//Clear the current messages
					if(output.equals("D129D7DF")){
						this.updateMessage(msg.trim());
						msg = "";
					}
					//This message is part of a group of messages, append it to msg
					else if(!output.equals("D129D7DF")){
						msg += " " + output;
					}
					System.out.flush();
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
