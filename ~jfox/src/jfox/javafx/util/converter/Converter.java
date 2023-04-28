package jfox.javafx.util.converter;

import java.text.ParseException;

import javafx.util.StringConverter;
import jfox.localization.BundleMessages;


public abstract class Converter<T> extends StringConverter<T> {

	public final static String MSG_DEFAULT = BundleMessages.getString( "validation.invalid.value" );
	
	//-------
	// Fields
	//-------

	private String 	message;
	private boolean	flagParseError;
	private String	outputText;
	
	
	//-------
	// Construtors
	//-------
	
	public Converter( String message ) {
		if ( message != null ) {
			this.message = message;
		} else {
			this.message = MSG_DEFAULT;
		}
	}
	
	
	//-------
	// Getters & Setters
	//-------
	
	public final String getMessage() {
		return message;
	}
	
	public final String getOutputText() {
		return outputText;
	}
	
	public final boolean hasParseError() {
		return flagParseError;
	}
	
	
	//-------
	// Actions
	//-------
	
	@Override
	public final String toString( T object) {
		if ( object == null ) {
			outputText = null;
		} else {
			outputText = format( object ); 
		}
		return outputText;
	}

	
	@Override
	public final T fromString( String string ) {

		flagParseError = false;
		
		if ( string != null ) {
			string = string.replace( "\u0020", "" ).replace( "\u202f", "" );
		}
		
		if ( string == null || string.isEmpty() ) {
			outputText = null;
			return null;
		}

		try {
			T value = parse( string );
			toString( value );
			return value;
		} catch ( Exception e ) {
			flagParseError = true;
			return null;
		}
	}
	
	public final int compare( String value1, T value2 ) throws ParseException {
		return compare( parse(value1), value2 );
	}
	
	public final int compare( String value1, String value2 ) throws ParseException {
		return compare( parse(value1), parse(value2) );
	}
	
	
	//-------
	// Abstract methods
	//-------
	
	protected abstract String format( T object ); 
	
	protected abstract T parse( String string ) throws ParseException; 
	
	public abstract int compare( T object1, T object2 ); 
	
}
