package raspberry;

import com.pi4j.io.serial.*;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.util.Console;

import java.util.Vector;
import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;

import common.FileHandling;

public class Hühnerstall {

	Vector queue = new Vector();
	Hashtable env = new Hashtable();

	public static void main(String args[]) throws InterruptedException,
			IOException {
		final Serial serial = SerialFactory.createInstance();
		// final String filename = "/tmp/arduino.out";
		FileHandling fh = new FileHandling();
		if (args.length < 1) {
			System.err
					.println("Configfile is missing. Ussage huehnerstall <configfile>");
			System.exit(1);
		}
		String configfile = args[0];
		System.out.println("Read configfile " + configfile);
		Hashtable config = fh.readFileAsHashtable(configfile);
		Hühnerstall huehner = new Hühnerstall();
		huehner.init(config);
	}

	private void init(Hashtable config) {
		env = config;
		ReadSerial serial = new ReadSerial(queue, env);
		CommunicationConsole cons = new CommunicationConsole(env);

		env.put("debug", "10"); // kein Debug
		env.put("maxQueue", 0);
		env.put("iterationen", 0);
		env.put("aktSensor", 0);
		env.put("maxSensor", 0);
		env.put("mTChanged", "0");
		env.put("mT", "10");
		env.put("messageChanged", "0");
		env.put("message", "");
		env.put("statMode", "unknown");
		env.put("statAuf", "unknown");

		// Startzeit
		Date start = new Date();

		env.put("startzeit", start);

		Thread read = new Thread(serial);
		Thread conn = new Thread(cons);

		serial.queue = queue;
		read.start();
		conn.start();

		parseQueue(queue);

	}

	private void parseQueue(Vector queue) {
		String str;
		FileHandling fh = new FileHandling();
		final String filename = "/tmp/arduino.txt";
		String debug = null;
		int maxQueue = 0;
		int maxSensor = 0;
		// DB db = new DB();
		DBConnect connect = new DBConnect(env);

		while (true) {
			try {
				// System.out.println("Parse Queue");
				debug = (String) env.get("debug");
				// System.out.println("Debug = "+debug);
				if (queue.size() > maxQueue) {
					maxQueue = queue.size();
					env.put("maxQueue", maxQueue);
				}
				if (debug.equals("0")) {
					System.out.println("Länge der Queue: " + queue.size());
				}
				while (!queue.isEmpty()) {

					str = (String) queue.elementAt(0);
					// System.out.println(str);
					// Ermittle Sensorwet
					if (str.startsWith("Sensorwert")) {
						try {

							// System.out.println(">"+str.trim()+"<");
							String[] werte = str.split("\n");
							// System.out.println(">"+werte[1].trim()+"<");
							Integer val = new Integer(werte[1].trim());
							env.put("aktSensor", val.intValue());
							connect.insertLight(val.intValue());
							if (val.intValue() > maxSensor) {
								maxSensor = val.intValue();
								env.put("maxSensor", maxSensor);
							}
						} catch (Exception ex) {
							System.err.println(ex.getStackTrace().toString());
						}
					}
					if (str.contains("Error")) {
						// TODO Fehler in die Datenbank eintragen.
						String[] werte = str.split(" ");

						connect.insertError(werte[1]);
						fh.writeFile("/tmp/arduinoError.txt", "error", true);

					}

					if (str.contains("Maxwert")) {

						connect.insertStatus("offen");

					}

					if (str.contains("Minwert")) {

						connect.insertStatus("geschlossen");

					}

					if (str.startsWith("Status")) {
						String[] werte = str.split(" ");
						if (werte[1].equals("stat")) {
							env.put("statMode", werte[2].trim());
							if (werte[2].trim().equals("manuell"))
							{
								fh.writeFile("/tmp/arduinoManuell.txt", "manuell", true);
							}
							else
							{
								if (fh.file_exists("/tmp/arduinoManuell.txt"))
								{
									fh.deleteFile("/tmp/arduinoManuell.txt");
								}
							}
						}
						if (werte[1].equals("auf")) {
							env.put("statAuf", werte[2].trim());
						}
					}

					if (str.startsWith("Liessensor") && !debug.equals("0")) {
						queue.removeElementAt(0);
						continue;
					}
					if (str.startsWith("Iterationen") && !debug.equals("0")) {
						queue.removeElementAt(0);
						continue;
					}
					if (str.startsWith("Sensorwert") && !debug.equals("0")) {
						queue.removeElementAt(0);
						continue;
					}

					if (str.startsWith("Message") && !debug.equals("0")) {
						queue.removeElementAt(0);
						continue;
					}
					if (str.startsWith("Status") && !debug.equals("0")) {
						queue.removeElementAt(0);
						continue;
					}
					if (str.startsWith(" ") && !debug.equals("0")) {
						queue.removeElementAt(0);
						continue;
					}

					System.out.println(str.trim());

					fh.writeFile(filename,
							new Date().toString() + " " + str.trim(), true);
					// System.err.println(queue.elementAt(0));

					queue.removeElementAt(0);
				}
			} catch (Exception ex) {
				fh.writeFile(filename, ex.toString(), true);
				System.err.println(ex);
			}

			try {
				Thread.sleep(10000);
			} catch (Exception ex) {
				System.err.println(ex);
			}
		}

	}

}
