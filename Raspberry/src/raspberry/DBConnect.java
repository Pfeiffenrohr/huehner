package raspberry;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import common.FileHandling;

public class DBConnect {
	Hashtable config;
	Vector datastore;
	
	public DBConnect(Hashtable config) {
		
		this.config=config;
	    init(config);
	}

	/*public static void main(String[] args) {
		
		FileHandling fh = new FileHandling();
		
		if ( args.length < 1)
		{
			System.err.println("Configfile is missing. Ussage huehnerstall <configfile>");
			System.exit(1);
		}
		String configfile = args [0];
		System.out.println("Read configfile "+configfile);
		Hashtable config = fh.readFileAsHashtable(configfile);	
		
		
		// TODO Auto-generated method stub
		
		DBConnect rasp = new DBConnect(config);
		rasp.init(config);
		rasp.insertLight(10);
		rasp.insertStatus("geschlossen");
		rasp.insertError("schliessen");
		

	}*/
	
	public void init(Hashtable config)
	{
			//read datastore (if exists :)
		FileHandling fh = new FileHandling();
		
		String file=(String)config.get("datastorefile");
		System.out.println( "Lese Datastore" );
		if (! fh.file_exists(file))
		{
			datastore= new Vector();
			System.out.println("!!Warning datastore does not exist");
		}
		else
		{
			InputStream fis = null;

			try
			{
			  fis = new FileInputStream( (String)config.get("datastorefile") );

			  ObjectInputStream o = new ObjectInputStream( fis );
			  datastore = (Vector) o.readObject();
			 
			  System.out.println( datastore.size()+ " Elemente eingelesen!" );
			 
			}
			catch ( IOException e ) { System.err.println( e ); }
			catch ( ClassNotFoundException e ) { System.err.println( e ); }
			finally { try { fis.close(); } catch ( Exception e ) { } }
			
		}
	}
	
	public void writeDatastore() {
		
		OutputStream fos = null;

		try
		{
		  fos = new FileOutputStream((String) config.get("datastorefile") );
		  ObjectOutputStream o = new ObjectOutputStream( fos );
		  o.writeObject(datastore );
		 
		}
		catch ( IOException e ) { System.err.println( e ); }
		finally { try { fos.close(); } catch ( Exception e ) { e.printStackTrace(); } }
	    
	}
	
	public void insertLight(int value)

	{
		DB db = new DB();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formaterDate = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat formaterTime = new SimpleDateFormat("HHmmss");
		
		String datum = formaterDate.format(cal.getTime());
		String zeit = formaterTime.format(cal.getTime());
		if (! db.dataBaseConnect((String)config.get("db_user"),(String)config.get("db_password"),(String)config.get("db_connectstring")))
		{
		    System.err.println("Error!! Database not available!");
			Hashtable hash = new Hashtable();
			hash.put("mode", "licht");
			hash.put("value", value);
			hash.put("zeit", zeit);
			hash.put("datum", datum);
			datastore.addElement(hash);
			writeDatastore();
			//put in backlog
			return;
		}
		db.insertLight(value,datum,zeit);
		
		if (datastore.size()>0)
		{
			for (int i=0; i < datastore.size();i++)
			{
				Hashtable hash = (Hashtable) datastore.elementAt(i);
				if ( ((String) hash.get("mode")).equals("licht")) 
				{
					db.insertLight((int)hash.get("value"),(String)hash.get("datum"),(String)hash.get("zeit"));	
					datastore.removeElementAt(i);
					i--;
				}
			}
			writeDatastore();
		}
	
		db.closeConnection();
		
	}
	
	public void insertStatus(String value)

	{
		DB db = new DB();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formaterDate = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat formaterTime = new SimpleDateFormat("HHmmss");
		
		String datum = formaterDate.format(cal.getTime());
		String zeit = formaterTime.format(cal.getTime());
		if (! db.dataBaseConnect((String)config.get("db_user"),(String)config.get("db_password"),(String)config.get("db_connectstring")))
		{
		    System.err.println("Error!! Database not available!");
			Hashtable hash = new Hashtable();
			hash.put("mode", "status");
			hash.put("value", value);
			hash.put("zeit", zeit);
			hash.put("datum", datum);
			datastore.addElement(hash);
			writeDatastore();
			//put in backlog
			return;
		}
		db.insertStatus(value,datum,zeit);
		
		if (datastore.size()>0)
		{
			for (int i=0; i < datastore.size();i++)
			{
				Hashtable hash = (Hashtable) datastore.elementAt(i);
				if ( ((String) hash.get("mode")).equals("status")) 
				{
					db.insertStatus((String)hash.get("value"),(String)hash.get("datum"),(String)hash.get("zeit"));	
					datastore.removeElementAt(i);
					i--;
				}
			}
			writeDatastore();
		}
		
		db.closeConnection();
		
	}

	public void insertError(String value)

	{
		DB db = new DB();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formaterDate = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat formaterTime = new SimpleDateFormat("HHmmss");
		
		String datum = formaterDate.format(cal.getTime());
		String zeit = formaterTime.format(cal.getTime());
		if (! db.dataBaseConnect((String)config.get("db_user"),(String)config.get("db_password"),(String)config.get("db_connectstring")))
		{
		    System.err.println("Error!! Database not available!");
			Hashtable hash = new Hashtable();
			hash.put("mode", "error");
			hash.put("value", value);
			hash.put("zeit", zeit);
			hash.put("datum", datum);
			datastore.addElement(hash);
			writeDatastore();
			//put in backlog
			return;
		}
		db.insertError(value,datum,zeit);
		
		if (datastore.size()>0)
		{
			for (int i=0; i < datastore.size();i++)
			{
				Hashtable hash = (Hashtable) datastore.elementAt(i);
				if ( ((String) hash.get("mode")).equals("error")) 
				{
					db.insertError((String)hash.get("value"),(String)hash.get("datum"),(String)hash.get("zeit"));	
					datastore.removeElementAt(i);
					i--;
				}
			}
			writeDatastore();
		}
		
		db.closeConnection();
		
	}
}

