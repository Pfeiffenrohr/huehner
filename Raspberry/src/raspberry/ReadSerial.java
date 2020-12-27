package raspberry;

import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;

import com.pi4j.io.serial.Baud;
import com.pi4j.io.serial.DataBits;
import com.pi4j.io.serial.FlowControl;
import com.pi4j.io.serial.Parity;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialConfig;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataEventListener;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.StopBits;
import com.pi4j.util.CommandArgumentParser;
import java.util.Vector;



//liest die serielle Schnittstelle des Raspberry Pis aus
public class ReadSerial implements Runnable {
	Vector queue = new Vector();
	Hashtable env;
	
	public ReadSerial(Vector queue,Hashtable env)
	{
	this.queue = queue;
	this.env = env;
	}
	
	
	public void run()
	{
	final Serial serial = SerialFactory.createInstance();
	System.out.println("Thread ReadSerial gestartet");
	 
		serial.addListener(new SerialDataEventListener() {
			 String inputstring;
			 int count=0;
	            @Override
	            public void dataReceived(SerialDataEvent event) {
	            	
	            	 try {
	                    
	            		 inputstring=event.getAsciiString();
	            		 queue.addElement(inputstring);
	            		 count++;
	            		 env.put("iterationen", count);
	            		 //System.out.println("Inputstring = "+inputstring);
	                 } catch (IOException e) {
	                     e.printStackTrace();
	                 }
	             }
	         });
		 
		 try {
	            // create serial config object
	            SerialConfig config = new SerialConfig();

	            // set default serial settings (device, baud rate, flow control, etc)
	            //
	            // by default, use the DEFAULT com port on the Raspberry Pi (exposed on GPIO header)
	            // NOTE: this utility method will determine the default serial port for the
	            //       detected platform and board/model.  For all Raspberry Pi models
	            //       except the 3B, it will return "/dev/ttyAMA0".  For Raspberry Pi
	            //       model 3B may return "/dev/ttyS0" or "/dev/ttyAMA0" depending on
	            //       environment configuration.
	 
	            //config.device(SerialPort.getDefaultPort())
	            config.device("/dev/ttyACM0")
	                 // .baud(Baud._38400)
	            .baud(Baud._9600)
	                  .dataBits(DataBits._8)
	                  .parity(Parity.NONE)
	                  .stopBits(StopBits._1)
	                  .flowControl(FlowControl.NONE);

	            // parse optional command argument options to override the default serial settings.
	           
			serial.open(config);
			while (true) {
				try {

					// write a formatted string to the serial transmit buffer
					if (env.get("mTChanged").equals("1"))
					{
						serial.write("mT"+env.get("mT"));
						env.put("mTChanged", "0");
						System.out.println("Send mT"+env.get("mT") + " to arduino" );
					}
					
					if (env.get("messageChanged").equals("1"))
					{
						serial.write((String)env.get("message"));
						env.put("messageChanged", "0");
						System.out.println("Send "  + env.get("message") + " to arduino" );
					}
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}

				} catch (IllegalStateException ex) {
					ex.printStackTrace();
				}

				// wait 1 second before continuing
				try {
					Thread.sleep(1000);
				}
	                catch (Exception ex)
	                {
	                	 System.err.println("Error in ReadSerial Thread"); 	
	                }
		 }
		 }
	        catch(IOException ex) {
	            System.out.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
	            return;
	        }

}
		
	}

	
