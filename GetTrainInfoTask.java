import java.io.*;
import java.util.HashMap;
import javafx.concurrent.Task;

public class GetTrainInfoTask extends Task<String>{

	@Override
	protected String call() throws Exception{

		System.out.println("TrainsGUI Threads: ");
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(MatchingSensors.socket.getOutputStream(), true);
		try {
			HashMap<String, String> cmds = new HashMap<String, String>();
			cmds.put("thru", "D13D141400009BDF");
			cmds.put("out",  "D13D141401009ADF");
			cmds.put("idon",  "D1310B0701BCDF");
			cmds.put("idoff", "D1310B0700BDDF");
			cmds.put("get", "D1010004FBDF");
			cmds.put("s1",  "D1300104CBDF");
			cmds.put("s11", "D1300B04C1DF");
			cmds.put("get", "D1010004FBDF");

			String userInput;
			while ((userInput = stdIn.readLine()) != null){

				if(cmds.containsKey(userInput)){
					out.println(cmds.get(userInput));
				}
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
		return "";
	}
}
