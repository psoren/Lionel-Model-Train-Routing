import java.io.*;
import javafx.concurrent.*;

public class IdentifySwitchTask extends Task<String>{

	String switchID = "";

	public IdentifySwitchTask(String sID){
		this.switchID = sID;
	}

	@Override
	protected String call() throws Exception{
		PrintWriter out = new PrintWriter(MatchingSensors.socket.getOutputStream(), true);

		int switchNumberDec = Integer.parseInt(this.switchID.substring(6, 8));
		String switchNumberHex = Integer.toHexString(switchNumberDec);

		//Format it correctly
		switchNumberHex = switchNumberHex.toUpperCase();
		if(switchNumberHex.length() == 1){
			switchNumberHex = "0" + switchNumberHex;
		}

		int thruCommandDec = 175 - switchNumberDec;
		String thruCommandHex = Integer.toHexString(thruCommandDec);		
		thruCommandHex = thruCommandHex.toUpperCase();

		int outCommandDec = thruCommandDec -1;
		String outCommandHex = Integer.toHexString(outCommandDec);
		outCommandHex = outCommandHex.toUpperCase();

		String thruSwitchCmd = "D13D" + switchNumberHex + "140000" + thruCommandHex + "DF";
		String outSwitchCmd = "D13D" + switchNumberHex + "140100" + outCommandHex + "DF";

		//Set switch to thru state, set to out state, set to thru state
		out.println(thruSwitchCmd);
		Thread.sleep(1000);
		out.println(outSwitchCmd);
		Thread.sleep(1000);
		out.println(thruSwitchCmd);
		return "";
	}
}
