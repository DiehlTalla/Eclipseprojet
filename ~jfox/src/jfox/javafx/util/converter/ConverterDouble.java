package jfox.javafx.util.converter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;


public class ConverterDouble extends Converter<Double> {

	
	//-------
	// Fields
	//-------
	
	private NumberFormat	format = NumberFormat.getInstance();
	
	
	//-------
	// Constructors
	//-------
	
	public ConverterDouble( Locale locale, String pattern, String message ) {
		super(message);
		if ( locale != null ) {
			format = NumberFormat.getInstance( locale );
		}
		if ( format instanceof DecimalFormat && pattern != null ) {
			( (DecimalFormat) format ).applyPattern(pattern);
		}
	}

	public ConverterDouble( Locale locale, String pattern ) {
		this( locale, pattern, null );
	}

	public ConverterDouble( Locale locale ) {
		this( locale, null, null );
	}

	public ConverterDouble( String pattern, String message ) {
 		this( null, pattern, message );
	}

	public ConverterDouble( String pattern ) {
		this( null, pattern, null );
	}

	public ConverterDouble() {
		this( null, null, null );
	}
	
	
	//-------
	// Actions
	//-------
	
	@Override
	protected String format(Double object) {
		return format.format( object );
	}
	
	@Override
	protected Double parse(String string) throws ParseException {
		return format.parse( string ).doubleValue();
	}
	
	@Override
	public int compare( Double value1, Double value2 ) {
		return Double.compare( value1, value2 );
	}
		
}
