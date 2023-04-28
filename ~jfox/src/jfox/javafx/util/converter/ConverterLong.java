package jfox.javafx.util.converter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;


public class ConverterLong extends Converter<Long>  {

	
	//-------
	// Fields
	//-------
	
	private NumberFormat	format = NumberFormat.getInstance();
	
	
	//-------
	// Constructors
	//-------
	
	public ConverterLong( Locale locale, String pattern, String message  ) {
		super( message );
		if ( locale != null ) {
			format = NumberFormat.getInstance( locale );
		}
		if ( format instanceof DecimalFormat && pattern != null ) {
			( (DecimalFormat) format ).applyPattern(pattern);
		}
	}

	public ConverterLong( String pattern, String message ) {
		this( null, pattern, message );
	}

	public ConverterLong( String pattern ) {
		this( null, pattern, null );
	}

	public ConverterLong() {
		this( null, null, null );
	}
	
	
	//-------
	// Actions
	//-------
	
	@Override
	protected String format(Long object) {
		return format.format( object );
	}
	
	@Override
	protected Long parse(String string) throws ParseException {
		return format.parse( string ).longValue();
	}
	
	@Override
	public int compare( Long value1, Long value2 ) {
		return Long.compare( value1, value2 );
	}
}
