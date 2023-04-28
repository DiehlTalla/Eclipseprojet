package jfox.javafx.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class Logger {
	
	
	// Constantes

	private static final String	NAME = "jfox.log";
	private static final String	ROOT = "src";
	
	
	// Champs
	
	private static String path = ROOT + "/" + NAME;
	
	
	// Getters & Setters
	
	public static void setPath( Class<?> clazz ) {
		path = ROOT + "/"  + clazz.getPackageName().replace( '.', '/' ) + "/" + NAME;
	}
	
	public static void setPath( String path ) {
		Logger.path = path;
	}
	

	// Actions
	
	@SuppressWarnings("unchecked")
	public static void log() {

		try {
			
			List<Period> periods = null;
			
			try {
				FileInputStream fileInputStream = new FileInputStream( path );
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
				periods = (List<Period>) objectInputStream.readObject();
				objectInputStream.close();
			} catch ( Exception e) {
			}
			if ( periods == null ) {
				periods = new ArrayList<>();
			}

			String computer = System.getenv( "COMPUTERNAME" );
			String user = System.getProperty( "user.name" );
			Period p = null
					;
			if ( periods.size() > 0 ) {
				p = periods.get( periods.size() - 1 );
			}

			if ( p == null
				|| ( computer != null && ! computer.equals( p.getComputer() ) ) 
				|| ( user != null && ! user.equals( p.getUser() ) )  
			) {
				p = new Period();
				periods.add(  p );
				p.setStart( LocalDate.now() );
				p.setComputer(computer);
				p.setUser( user );
			}
			
			p.setEnd( LocalDate.now() );
			
			FileOutputStream fileOutputStream = new FileOutputStream( path );
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject( periods );
			objectOutputStream.flush();
			objectOutputStream.close();

		} catch (Exception e) {
		}

	}

	public static void log( Class<?> clazz) {
		try {
			setPath(clazz);
			log();
		} catch (Exception e) {
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static List<Period> read() {
		List<Period> periods = null;
		try {
			FileInputStream fileInputStream = new FileInputStream( path );
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			periods = (List<Period>) objectInputStream.readObject();
			objectInputStream.close();
		} catch ( Exception e) {
			e.printStackTrace();
		}
		return periods;
	}
	
	public static void show() {
		List<Period> periods = read();
		if ( periods != null ) {
			for ( Period p : periods ) {
				System.out.printf( "%td/%<tm/%<tY - %td/%<tm/%<tY  %s  %s %n", p.getStart(), p.getEnd(), p.getComputer(), p.getUser() );
			}
		}
	}

	public static void show( Class<?> clazz) {
		setPath(clazz);
		show();
	}
	
	
	// Helper Types
	
	@SuppressWarnings("serial")
	public static class Period implements Serializable {

		// Fields

		private LocalDate start;
		private LocalDate end;
		private String computer;
		private String user;

		// Getters & Setter

		public LocalDate getStart() {
			return start;
		}

		public void setStart(LocalDate start) {
			this.start = start;
		}

		public LocalDate getEnd() {
			return end;
		}

		public void setEnd(LocalDate end) {
			this.end = end;
		}

		public String getComputer() {
			return decrypt( computer );
		}

		public void setComputer(String computer) {
			this.computer = encrypt(computer);
		}

		public String getUser() {
			return decrypt( user );
		}

		public void setUser(String user) {
			this.user = encrypt(user);
		}

		
		// Actions
		
		private static String encrypt(String str) {
			byte[] bytes = str.getBytes();
			for ( int i = 0; i < bytes.length; ++i ) {
				bytes[i] = (byte) ( bytes[i] +  1 ) ; 
			}
			return new String( bytes );
		}

		private static String decrypt(String str) {
			byte[] bytes = str.getBytes();
			for ( int i = 0; i < bytes.length; ++i ) {
				bytes[i] = (byte) ( bytes[i] -  1 ) ; 
			}
			return new String( bytes );
	    }
	}

}
