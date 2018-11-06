import java.io.*;

import java.net.*;
import java.util.*;

import javafx.application.*;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
//import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GetInfo extends Application{

	static Socket socket = null;
	
	public static ArrayList<String> getInfo() throws IOException
	{
		String hostName = "192.168.99.1";
		int portNumber = 50001;
		
		
		
		socket = new Socket(hostName, portNumber);
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		
		// Use arrayList
		ArrayList<String> information = new ArrayList<String>();
		
		while (true)
		{ 
			out.println("D129D7DF");
			char[] buffer = new char[1024];
			int read;
			if ((read = in.read(buffer)) != -1) {
				String output = new String(buffer, 0, read);
				if(!output.equals("D129D7DF"))
				{
					information.add(output);
				}
				
				//System.out.println(output);
				
				System.out.flush();				
			}
			
			out.println("D1010002FDDF");
			char[] bufferN = new char[1024];
			if ( in.read(bufferN) != -1) {

				String info = in.readLine();
				
				int length = info.indexOf("D129D7DF");
				String back = info.substring(0, length);
				String nInfo = back;
				int cut = nInfo.indexOf("DF");
				
				while(cut != -1)
				{
					String update = nInfo.substring(0, cut+2);
					nInfo = nInfo.substring(cut+2);
					information.add(update);
					//System.out.println(update);
					cut = nInfo.indexOf("DF");
				}	
			}	
			break;
		} 
		return information;
	}
	
	// use <String, ArrayList<>>
	public static HashMap<String, ArrayList<String>> analyze() throws IOException
	{
		HashMap<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
		
		ArrayList<String> info = getInfo();
		
		ArrayList<String> sensorID = new ArrayList<String>();
		
		ArrayList<String> switchID = new ArrayList<String>();
		
		ArrayList<String> wifiID = new ArrayList<String>();
		
		for (int i = 0; i < info.size(); i++)
		{
			String com = info.get(i);
			
			
			if(com != null)
			{
				if(com.length() == 18)
				{
					String ID = com.substring(4,6);
					
					wifiID.add(ID);
					result.put("wifi", wifiID);
				}
				
				if(com.length() == 42)
				{
					String ID = com.substring(4, 6);
					
					switchID.add(ID);
					result.put("switch", switchID);
				}
				
				if(com.length() == 58)
				{
					String ID = com.substring(4,6);
					
					sensorID.add(ID);
					result.put("sensor", sensorID);
				}
			}	
			
		}
		
		return result;
	}
	
	public static int HexToDec(String hex)
	{
		char hexChars[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		
		int power = 1;
		
		int result = 0;
		
		for(int i = hex.length() - 1; i >= 0; --i)
		{
			char num = hex.charAt(i);
			
			int base = 0;
			
			for(int j = 0; j < hexChars.length; ++j)
			{
				if(num == hexChars[j])
				{
					base = j;
				}
			}
			
			result += base * power;
			
			power = power * 16;
		}
		
		return result;
	}

	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		HashMap<String, ArrayList<String>> info = new HashMap<String, ArrayList<String>>();
		
		info = analyze();
		
		ArrayList<String> wifiID = info.get("wifi");
		
		ArrayList<String> switchID = info.get("switch");
		
		ArrayList<String> sensorID = info.get("sensor");
		
		ArrayList<Button> buttons = new ArrayList<Button>();
		
		Button btn_1 = new Button();
		int text_1 = HexToDec(wifiID.get(0));
		btn_1.setText("wifi " + text_1);
		buttons.add(btn_1);
		
		for (int i = 0; i < switchID.size(); i++)
		{
			Button btn_2 = new Button();
			int text_2 = HexToDec(switchID.get(i));
		    btn_2.setText("switch " + text_2);
		    buttons.add(btn_2);
		}
		
		for (int i = 0; i < sensorID.size(); i++)
		{
			Button btn_3 = new Button();
			int text_3 = HexToDec(sensorID.get(i));
		    btn_3.setText("sensor " + text_3);
		    buttons.add(btn_3);
		}
//	    btn.setOnAction(new EventHandler<ActionEvent>() 
//	    {
//	    	
//	    	@Override
//	    	public void handle(ActionEvent event) 
//	    	{
//	    		System.out.println("Hello World!");
//	    	}
//	    });
	    
	    GridPane root = new GridPane();
	    
	    int nRows = buttons.size();
	    
	    root.setHgap(nRows);
	    root.setVgap(3);
	    //root.setPadding(new Insets(0, 10, 0, 10));
	    
	    for(int i = 0; i < buttons.size(); i++)
	    {
	    	root.add(buttons.get(i), 2, i);
	    }	    

	    Button check = new Button();
	    check.setText("check");
	    root.add(check, 2, buttons.size());
	    Scene scene = new Scene(root, 1000, 850);
	    
	    primaryStage.setTitle("Check components");
	    primaryStage.setScene(scene);
	    primaryStage.show();
		
	}
	

	public static void main(String[] args) throws IOException
	{
		
//		HashMap<String,ArrayList<String>> info = analyze();
//		
//		System.out.println(Arrays.asList(info));
		
		launch(args);
		
//		System.out.println(HexToDec("123"));
		
	}
}
