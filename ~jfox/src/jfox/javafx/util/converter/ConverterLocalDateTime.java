package jfox.javafx.util.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;


public class ConverterLocalDateTime extends Converter<LocalDateTime> {

	
	//-------
	// Fields
	//-------
	
	protected DateTimeFormatter	formatterOut = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
	protected DateTimeFormatter	formatterIn;

	private static String patternDefault;
	
	
	//-------
	// Constructors
	//-------

	public ConverterLocalDateTime( String  patternOut, String patternIn, String message ) {
		super(message);
		if ( patternOut == null ) {
			if ( patternDefault == null ) {
				formatterOut = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
				patternOut = formatterOut.format( LocalDateTime.of( 2025, 12, 27, 11, 22, 33 ) );
				patternOut = patternOut.replace("2025", "yyyy").replace("25", "yyyy").replace("12", "MM").replace("27", "dd");
				patternOut = patternOut.replace("11", "HH").replace("22", "mm").replace("33", "ss");
			} else {
				patternOut = patternDefault;
			}
		}
		formatterOut = DateTimeFormatter.ofPattern( patternOut );  
		if ( patternIn == null ) {
			patternIn = patternOut.replace( "yyyy", "y" ).replace( "yy", "y" ).replace( "MM", "M" ).replace( "dd", "d" );
			patternIn = patternIn.replace( "HH", "H" ).replace( "mm", "m" ).replace( "ss", "s" );
		}
		formatterIn = DateTimeFormatter.ofPattern( patternIn );
	}

	public ConverterLocalDateTime( String  pattern, String message ) {
		this( pattern, null, message );
	}

	public ConverterLocalDateTime( String  pattern ) {
		this( pattern, null, null );
	}

	public ConverterLocalDateTime() {
		this( null, null, null );
	}
	
	
	//-------
	// Getters & Setters
	//-------
	
	public static void setPatternDefault(String patternDefault) {
		ConverterLocalDateTime.patternDefault = patternDefault;
	}
	
	
	//-------
	// Actions
	//-------
	
	@Override
	protected String format(LocalDateTime object) {
		return formatterOut.format( object );
	}
	
	@Override
	protected LocalDateTime parse(String string) {
		return  LocalDateTime.from( formatterIn.parse( string ) ) ;
	}
	
	@Override
	public int compare( LocalDateTime value1, LocalDateTime value2 ) {
		int result = 0;
		if ( value2.isAfter( value1 ) ) {
			result = -1;
		} else if ( value2.isBefore( value1 ) ) {
			result = 1;
		}
		return result;
	}

}

