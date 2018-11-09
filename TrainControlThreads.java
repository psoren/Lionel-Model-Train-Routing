package main;
import java.io.*;
import java.net.*;
import java.util.*;

class TrainControlThreads extends Thread {


        static Socket socket = null;


           public void run() {
                     System.out.println("Hello from a thread!");

		try {

        	TrainControlThreads controller = null;


		HashMap<String, String> cmds = new HashMap<String, String>();
		cmds.put("thru", "D13D141400009BDF");
		cmds.put("out",  "D13D141401009ADF");
		cmds.put("r",    "D127FE1B01BFDF");
		cmds.put("1",    "D127FE19467CDF");
		cmds.put("2",    "D127FE19477BDF");
		cmds.put("3",    "D127FE19487ADF");
		cmds.put("4",    "D127FE194979DF");
		cmds.put("5",    "D127FE194A78DF");
		cmds.put("x",    "D127FEFFFFDDDF");		
		cmds.put("s11",  "D1300B04C1DF");


		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));	

		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

 		this.printMenu();
               
		String userInput;
		while ((userInput = stdIn.readLine()) != null){
	
			if(userInput.equals("quit")){
				break;
			}

			if(cmds.containsKey(userInput)){
				String cmd = cmds.get(userInput);
				out.println(cmd);
			}
			else{
				System.out.println("The command you entered was not recognized.  Try again, or enter quit to quit.");
			}
		}  // end while

              } // try
              catch (Exception e) {
                 System.out.println(e);
              }

                  System.exit(0);
           } // end run




	public static void main(String[] args) throws IOException{

		String hostName = "192.168.99.1";
		int portNumber = 50001;

		socket = new Socket(hostName, portNumber);
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

             (new TrainControlThreads()).start();

                   while ( 1==1 )
			{ 
                              out.println("D129D7DF");
    				char[] buffer = new char[1024];
    				int read;
    				if ((read = in.read(buffer)) != -1) {
        				String output = new String(buffer, 0, read);
        				System.out.println(output);
        				System.out.flush();
    				}
			}  // end while loop

	}  // end main

	/******Train stuff for engine 50******/
	//String togDir  = "D1 27 FE 19 01 C1 DF";
	//String relspd1 = "D1 27 FE 19 46 7C DF";
	//String relspd2 = "D1 27 FE 19 47 7B DF";
	//String relspd3 = "D1 27 FE 19 48 7A DF";
	//String relspd4 = "D1 27 FE 19 49 79 DF";
	//String relspd5 = "D1 27 FE 19 4A 78 DF";
	//String relspd6 = "D1 27 FE 19 4B 77 DF";
	//String stop    = "D1 27 FE FF FF DD DF";

	/*****Switch controls for TMCC ID 20*****/
	//To set switch with id 20 to through and out state respectively
	//String thru = "D1 3D 14 14 00 00 9B DF";
	//String out = "D1 3D 14 14 01 00 9A DF ";


	/*****Check sensor status*****/
	//Getting status of sensor ID 0B (11)
	//String s11info = "D1 30 0B 04 C1 DF";

	//If car is off sensor, the response after 18 hex digits is:
	//D1 32 0B 04 11 01 01 73 39 DF

	//If car is on sensor, the response after 18 hex digits is:
	//D1 32 0B 04 11 01 01 72 3A DF
	//OR
	//D1 32 0B 04 11 01 01 71 3B DF

	//Getting status of sensor ID 0C (12)
	//String s12info = "D1 30 0C 04 C0 DF";

	//If car is off sensor, the response after 18 hex digits is:
	//D1 32 0C 04 12 01 01 72 38 DF
	//OR		
	//D1 32 0C 04 12 01 01 71 39 DF (occasionally)

	//If car is on sensor, the response after 18 hex digits is:
	//D1 32 0C 04 12 01 01 70 3A DF
	//OR
	//D1 32 0C 04 12 01 01 71 39 DF (occasionally)

	//not sure why there is some ambiguity here
	//we also need to figure out how to get the response quicker

	public void printMenu(){
		System.out.println("=============================================");
		System.out.println("Please enter a command:");
		System.out.println("------------------Switches-------------------");
		System.out.println("thrr"
				+ "u: Set state of switch to through state");
		System.out.println("out: Set state of switch to out state");
		System.out.println("-------------------Trains--------------------");
		System.out.println("r: Reverse direction of train");
		System.out.println("1: Set speed of train to 1");
		System.out.println("2: Set speed of train to 2");
		System.out.println("3: Set speed of train to 3");
		System.out.println("4: Set speed of train to 4");
		System.out.println("5: Set speed of train to 5");
		System.out.println("x: Stop train");
		System.out.println("------------------Sensors--------------------");
		System.out.println("s11: Get status of sensor 11");
		System.out.println("-------------------Other---------------------");
		System.out.println("quit: Exit Program");
		System.out.println("=============================================");
	}
}
