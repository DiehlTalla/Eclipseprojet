package jfox.javafx.util.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;


public class ConverterLocalDate extends Converter<LocalDate> {

	
	//-------
	// Fields
	//-------
	
	protected DateTimeFormatter	formatterOut;;
	protected DateTimeFormatter	formatterIn;

	private static String patternDefault;
	
	
	//-------
	// Constructors
	//-------

	public ConverterLocalDate( String  patternOut, String patternIn, String message ) {
		super(message);
		if ( patternOut == null ) {
			if ( patternDefault == null ) {
				formatterOut = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
				patternOut = formatterOut.format( LocalDate.of( 2025, 12, 27 ) );
				patternOut = patternOut.replace("2025", "yyyy").replace("25", "yyyy").replace("12", "MM").replace("27", "dd");
			} else {
				patternOut = patternDefault;
			}
		}
		formatterOut = DateTimeFormatter.ofPattern( patternOut );  
		if ( patternIn == null ) {
			patternIn = patternOut.replace( "yyyy", "y" ).replace( "yy", "y" ).replace( "MM", "M" ).replace( "dd", "d" );
		}
		formatterIn = DateTimeFormatter.ofPattern( patternIn );
	}

	public ConverterLocalDate( String  pattern, String message ) {
		this( pattern, null, message );
	}

	public ConverterLocalDate( String  pattern ) {
		this( pattern, null, null );
	}

	public ConverterLocalDate() {
		this( null, null, null );
	}
	
	
	//-------
	// Getters & Setters
	//-------
	
	public static void setPatternDefault(String patternDefault) {
		ConverterLocalDate.patternDefault = patternDefault;
	}
	
	
	//-------
	// Actions
	//-------
	
	@Override
	protected String format(LocalDate object) {
		return formatterOut.format( object );
	}
	
	@Override
	protected LocalDate parse(String string) {
		return  LocalDate.from( formatterIn.parse( string ) ) ;
	}
	
	@Override
	public int compare( LocalDate value1, LocalDate value2 ) {
		int result = 0;
		if ( value2.isAfter( value1 ) ) {
			result = -1;
		} else if ( value2.isBefore( value1 ) ) {
			result = 1;
		}
		return result;
	}

}

