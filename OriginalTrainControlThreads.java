

import java.io.*;
import java.net.*;
import java.util.*;

class OriginalTrainControlThreads extends Thread {

	static Socket socket = null;

	public void run() {
		System.out.println("Hello from a thread!");

		try {
			HashMap<String, String> cmds = new HashMap<String, String>();
			cmds.put("s1",  "D1300104CBDF");
			cmds.put("s11", "D1300B04C1DF");
			cmds.put("get", "D1010004FBDF");


			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));	

			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

			this.printMenu();

			String userInput;
			while ((userInput = stdIn.readLine()) != null){

				if(cmds.containsKey(userInput)){
					String cmd = cmds.get(userInput);
					out.println(cmd);
				}
			}

		}
		catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void main(String[] args) throws IOException{

		String hostName = "192.168.99.1";
		int portNumber = 50001;

		socket = new Socket(hostName, portNumber);
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		(new OriginalTrainControlThreads()).start();

		while (true)
		{ 
			out.println("D129D7DF");
			char[] buffer = new char[1024];
			int read;
			if ((read = in.read(buffer)) != -1) {
				String output = new String(buffer, 0, read);

				if(!output.equals("D129D7DF")){
					System.out.println(output);
				}
				System.out.flush();
			}
		}

	}
	public void printMenu(){
		System.out.println("s11: Get status of sensor 11");
	}
}