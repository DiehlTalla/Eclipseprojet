package jfox.javafx.validation;

import java.util.function.Predicate;


@SuppressWarnings( { "rawtypes", "unchecked"} )
class Rule {
	
	
	// Fields
	
	private String			message;
	private Predicate		checker;
	
	
	// Constructor

	Rule( String message, Predicate checker ) {
		this.message = message;
		this.checker = checker;
	}

	
	// Actions
	
	String check( Object value ) {
		if ( checker.test( value ) ) {
			return null;
		} else {
			return message;
		}
	}


}
