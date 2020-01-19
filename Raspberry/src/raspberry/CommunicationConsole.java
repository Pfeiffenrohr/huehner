package raspberry;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.lang.Runnable;
import java.lang.Thread;
import java.net.ServerSocket;
import java.net.Socket;

public class CommunicationConsole implements Runnable{
	private ServerSocket server;
	public Hashtable env;
	
	public CommunicationConsole(Hashtable env)
	{
		this.env=env;
	}
	
	public void run() {
	
		try {
			//int port = Integer.parseInt(strport);
		    int port= 8976;
			server = new ServerSocket(port);
			System.out.println("Communicationconsole is running ... ");
			handleConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void handleConnection() {
		
		
		// System.out.println("Waiting for client message...");
		System.out.println("Handle Connection ..,.");
		//
		// The server do a loop here to accept all connection initiated by the
		// client application.
		//
		while (true) {
			if (((String)env.get("debug")).equals("1"))
			{
			System.out.println("In while");
			}
			try {
				if (((String)env.get("debug")).equals("1"))
			{
				System.out.println("Vor Socket accept ...");
			}
				Socket socket = server.accept();
				socket.setSoTimeout(3600000);
				if (((String)env.get("debug")).equals("1"))
				{
				System.out.println("Start new Connectionhandler ...");
				}
				new ConnectionHandler(socket,env);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

class ConnectionHandler implements Runnable {
	private Socket socket;
	boolean debug = true;
	Hashtable env;

	public ConnectionHandler(Socket socket,Hashtable env) {
		this.socket = socket;
		this.env=env;
		Thread t = new Thread(this);
		
		System.out.println("Starting Thread ...");
		t.start();
	}

	public void run() {
		try {
			//
			// Read a message sent by client application
			//
			
			System.out.println("Thread is running");
			ObjectInputStream ois = new ObjectInputStream(
					socket.getInputStream());
			 ObjectOutputStream oos = new ObjectOutputStream(
                     socket.getOutputStream());
			String message;
			String input  [];

			
			while (true)
			{
				if (((String)env.get("debug")).equals("1"))
				{
			System.out.println("Warte auf Nachricht");
				}
			message = (String) ois.readObject();
			if (((String)env.get("debug")).equals("1"))
			{
			System.out.println("Message =  >" + message+"<");
			}
			input=message.split(" ");
			
			// Vector dat=new Vector();
			if ( input[0].equals("exit"))
			{
				oos.writeObject("Good by");
				ois.close();
				oos.close();
				break;
			}
			
			if ( input[0].equals("i"))
			{
				  Date akt = new Date();
				 long diff = akt.getTime() - ((Date) env.get("startzeit")).getTime();
				 
				 double tag = Math.floor(diff / (1000*60*60*24));
		         diff = diff % (1000*60*60*24);
		         double std = Math.floor(diff / (1000*60*60));
		         diff = diff % (1000*60*60);
		         double min = Math.floor(diff / (1000*60));
		         diff = diff % (1000*60);
		         double sec = Math.floor(diff / 1000);
		         double mSec = diff % 1000;
				
				String str="Laufzeit: Tag: " + tag + " Std: " + std + " Min: " + min + " Sec: " + sec +"\n"						
						+ "Max Queue Size: " + env.get("maxQueue") +"\n"
						+ "Iterationen: " +  env.get("iterationen")+"\n"
						+ "Max Helligkeit: " +  env.get("maxSensor")+"\n"
						+ "Aktuelle Helligkeit: " +  env.get("aktSensor")+"\n"
						+ "Modus: " +  env.get("statMode")+"\n"
						+ "Auf/Zu: " +  env.get("statAuf")+"\n";
				
				oos.writeObject(str);
				continue;
			}
			if ( input[0].equals("debug_ein"))
			{
				
				oos.writeObject("Schalte Debugging ein");
				env.put("debug","0");
				continue;
			
			}
			if ( input[0].equals("debug_aus"))
			{
				
				oos.writeObject("Schalte Debugging aus");
				env.put("debug","10");
				continue;
			
			}
			if ( input[0].equals("set"))
			{
				if  ( input[1].equals("maxTurn"))
				{
					env.put("mTChanged","1");
					env.put("mT",input[2]);
					oos.writeObject("Setzte MaxTern auf "+input[2]);
										
				}
				if  ( input[1].equals("close"))
				{
					env.put("messageChanged","1");
					env.put("message","cl");
					oos.writeObject("Schliesen");
										
				}
				
				if  ( input[1].equals("open"))
				{
					env.put("messageChanged","1");
					env.put("message","op");
					oos.writeObject("Öfnen");
										
				}
				
				if  ( input[1].equals("auto"))
				{
					env.put("messageChanged","1");
					env.put("message","au");
					oos.writeObject("Auto");
										
				}
								
				continue;
			
			}
			
			oos.writeObject("Command unknown");
			}
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	

}