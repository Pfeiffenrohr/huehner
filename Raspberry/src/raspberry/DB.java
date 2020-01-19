package raspberry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class DB {
	protected Connection con = null;
    protected boolean debug=false;
	public boolean dataBaseConnect(String username,String password, String connectString) {
		if (debug) if (debug) System.out.println("Verbinde mich zur Datenbank");
		try {
			try {
				Class.forName("org.gjt.mm.mysql.Driver").newInstance(); // DB-
																		// Treiber
																		// laden
			} catch (Exception E) {
				System.err
						.println("Konnte MySQL Datenbank-Treiber nicht laden!");
				return false;
			}
			//String url = "jdbc:mysql://192.168.2.8/budget_test";
			con = DriverManager.getConnection(connectString, username, password); // Verbindung
		      													// herstellen
			if (debug) System.out.println("Verbindung erstellt");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Treiber fuer mySQL nicht gefunden");
			return false;
		}
		return true;
	}
	
	/**
	 * Schlie홺 die Verbindung zum Server. Das Objekt ist danach unbrauchbar.
	 */

	public boolean closeConnection() {
		if (con != null) {
			try {
				con.close();
				if (debug) System.out.println("Verbindung beendet");
			} catch (Exception e) {
				System.err.println("Konnte Verbindung nicht beenden!!");
				return false;
			}
		}
		return true;
	}
	public boolean insertLight(int value,String datum, String zeit) {
		try {
			
			String stm;
			PreparedStatement stmt;
			
			
			
			
			stm = "insert into licht values(null,'"+datum+"','"+zeit+"','"+value+"')";

			stmt = con.prepareStatement(stm);

			if (debug)
				System.out.println(stm);
			stmt.executeUpdate();

		} catch (SQLException e) {
			System.err.println("Konnte Insert-Anweisung nicht ausf체hren" + e);
			return false;
		}
		// if (debug) System.out.println("update-Anweisung ausgef체hrt");
		// return summe/(float)getAnz(tag,monat,year);
		return true;

	}
	
	public boolean insertStatus(String status,String datum, String zeit) {
		try {
			
			String stm;
			PreparedStatement stmt;
			
			
			
			stm = "insert into status values(null,'"+datum+"','"+zeit+"','"+status+"')";

			stmt = con.prepareStatement(stm);

			if (debug)
				System.out.println(stm);
			stmt.executeUpdate();

		} catch (SQLException e) {
			System.err.println("Konnte Insert-Anweisung nicht ausf체hren" + e);
			return false;
		}
		// if (debug) System.out.println("update-Anweisung ausgef체hrt");
		// return summe/(float)getAnz(tag,monat,year);
		return true;

	}

	public boolean insertError(String error,String datum, String zeit) {
		try {
			
			String stm;
			PreparedStatement stmt;
			
			stm = "insert into error values(null,'"+datum+"','"+zeit+"','"+error+"')";

			stmt = con.prepareStatement(stm);

			if (debug)
				System.out.println(stm);
			stmt.executeUpdate();

		} catch (SQLException e) {
			System.err.println("Konnte Insert-Anweisung nicht ausf체hren" + e);
			return false;
		}
		// if (debug) System.out.println("update-Anweisung ausgef체hrt");
		// return summe/(float)getAnz(tag,monat,year);
		return true;

	}


}