package jfox.javafx.util.converter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;


public class ConverterLocalTime extends Converter<LocalTime> {

	
	//-------
	// Fields
	//-------
	
	protected DateTimeFormatter	formatterOut = DateTimeFormatter.ofLocalizedTime( FormatStyle.SHORT );
	protected DateTimeFormatter	formatterIn;

	private static String patternDefault;

	
	//-------
	// Constructors
	//-------

	public ConverterLocalTime( String  patternOut, String patternIn, String message ) {
		super(message);
		if ( patternOut == null ) {
			if ( patternDefault == null ) {
				formatterOut = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
				patternOut = formatterOut.format( LocalTime.of( 11,22, 33 ));
				patternOut = patternOut.replace("11", "HH").replace("22", "mm").replace("33", "ss");
			} else {
				patternOut = patternDefault;
			}
		}
		formatterOut = DateTimeFormatter.ofPattern( patternOut );  
		if ( patternIn == null ) {
			patternIn = patternOut.replace( "HH", "H" ).replace( "mm", "m" ).replace( "ss", "s" );
		}
		formatterIn = DateTimeFormatter.ofPattern( patternIn );
	}
	

	public ConverterLocalTime( String  pattern, String message ) {
		this( pattern, null, message );
	}

	public ConverterLocalTime( String  pattern ) {
		this( pattern, null, null );
	}

	public ConverterLocalTime() {
		this( null, null, null );
	}
	
	
	//-------
	// Getters & Setters
	//-------
	
	public static void setPatternDefault(String patternDefault) {
		ConverterLocalTime.patternDefault = patternDefault;
	}
	
	
	//-------
	// Actions
	//-------
	
	@Override
	protected String format(LocalTime object) {
		return formatterOut.format( object );
	}
	
	@Override
	protected LocalTime parse(String string) {
		return  LocalTime.from( formatterIn.parse( string ) ) ;
	}
	
	@Override
	public int compare( LocalTime value1, LocalTime value2 ) {
		int result = 0;
		if ( value2.isAfter( value1 ) ) {
			result = -1;
		} else if ( value2.isBefore( value1 ) ) {
			result = 1;
		}
		return result;
	}

}

