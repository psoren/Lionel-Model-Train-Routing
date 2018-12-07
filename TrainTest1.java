

import java.io.*;
import java.net.*;

class TrainTest1 extends Thread {
	
	static Socket socket = null;

	public static void main(String[] args) throws IOException{
		
		String hostName = "192.168.99.1";
		int portNumber = 50001;

		socket = new Socket(hostName, portNumber);
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
//		(new TrainControl()).start();
		
		
		while (true)
		{ 
			out.println("D129D7DF");
			out.println("D127FE1B4779DF");
			char[] buffer = new char[1024];
			int read;
			if ((read = in.read(buffer)) != -1) {
				String output = new String(buffer, 0, read);
				//System.out.println(output);
				if(output.substring(4, 6).equals("0C"))
				{
					System.out.println(output);
					out.println("D127FEFFFFDDDF");
					out.println("D127FE1B01BFDF");
					//out.println("D127FE1B01BFDF");
					
				}
				System.out.flush();

				
			}
			
//			out.println("D127FE1B4779DF");
		}  // end while loop

	}  // end main
}
