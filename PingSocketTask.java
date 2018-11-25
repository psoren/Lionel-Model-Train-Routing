import java.io.*;
import javafx.concurrent.Task;

public class PingSocketTask extends Task<Void>{

	@Override
	public Void call(){
		try{
			PrintWriter out = new PrintWriter(MatchingSensors.socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(MatchingSensors.socket.getInputStream()));
			
			System.out.println("pinging...");
			while(true){
				out.println("D129D7DF");
				char[] buffer = new char[1024];
				int read;
				if ((read = in.read(buffer)) != -1) {
					String output = new String(buffer, 0, read);
					if(!output.equals("D129D7DF") && !output.equals("D13681100F020028DF")){
						this.updateMessage(output);
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
