import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class TrainControlThreads implements Runnable{
	
	static ExecutorService executor;

	public void run(){
		System.out.println("Hello from a thread!");
		try {
			
			String hostName = "192.168.99.1";
			int portNumber = 50001;

			Socket socket = new Socket(hostName, portNumber);
			
			HashMap<String, String> cmds = new HashMap<String, String>();
			cmds.put("thru", "D13D141400009BDF");
			cmds.put("out",  "D13D141401009ADF");

			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

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

		executor = Executors.newFixedThreadPool(2);
		
		TrainControlThreads t1 = new TrainControlThreads();		
		//PingSocketThread p1 = new PingSocketThread();
		//executor.submit(t1, p1);
	}
}
