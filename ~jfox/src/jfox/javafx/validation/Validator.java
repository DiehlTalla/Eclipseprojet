package jfox.javafx.validation;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import jfox.javafx.util.converter.Converter;
import jfox.localization.BundleMessages;


public class Validator {
	
	
	// Fields
	
	private final Map<Node, ValidationUnit> units = new HashMap<>();
	
	private final BooleanProperty invalid = new SimpleBooleanProperty(false);
	
	private final BooleanProperty flagDisabled = new SimpleBooleanProperty(false);
	
	private boolean flagAlertAlways = true;

	
	// Getters & Setters

	public final ReadOnlyBooleanProperty invalidProperty() {
		return this.invalid;
	}

	public final boolean isInvalid() {
		return this.invalidProperty().get();
	}
	
	public void setDisabled(boolean flagDisabled) {
		this.flagDisabled.set(flagDisabled);
	}
	
	public void setFlagAlertAlways(boolean flagCheckAlways) {
		this.flagAlertAlways = flagCheckAlways;
		for( var unit : units.values()  ) {
			unit.setFlagAlertAlways(flagCheckAlways);
			unit.setFlagAlert(flagCheckAlways);
		}
	}
	
	
	
	// Actions

	public <T> void addRule( Control control, String message, Predicate<T> checker ) { 
		Objects.requireNonNull( control );
		Objects.requireNonNull( checker );
		var unit = units.get(control);
		if ( unit == null ) {
			unit =  new ValidationUnit( control );
			unit.setFlagAlertAlways(flagAlertAlways);
			unit.setFlagAlert(flagAlertAlways);
			var observable = Extractors.getExtractorObs(control).call(control);
			final var unitFinal = unit;
			observable.addListener( (o, ov, nv) -> {
				unitFinal.setFlagAlert(true);
				unitFinal.validate( flagDisabled.get() );
				refreshStatus();
			});
		}
		
		final var checkerFinal = checker;
		try {
			checkerFinal.test(null);
		} catch ( NullPointerException e) {
			checker =  t ->  {
				if ( t == null ) {
					return true;
				}
				if( t instanceof CharSequence 
						&& ((CharSequence) t).length() == 0 ) {
					return true;
				} 
				return checkerFinal.test(t);
			};
		}
		
		unit.addRule( new Rule( message, checker) );
		units.put(control, unit);
	}
	
	
	public void addRuleNotNull( Control control, String message ) {
		Objects.requireNonNull( control );
		if ( message == null || message.isBlank() ) {
			message = BundleMessages.getString( "validation.not.null" );
		}
		addRule( control, message, (Object t) -> t!=null );
	}
	public void addRuleNotNull( Control control ) {
		addRuleNotNull( control, null );
	}
	
	public void addRuleNotBlank( Control control, String message ) {
		Objects.requireNonNull( control );
		if ( message == null || message.isBlank() ) {
			message = BundleMessages.getString( "validation.not.blank" );
		}
		addRule( control, message, (String t) -> t!=null && ! t.isBlank() );
	}
	public void addRuleNotBlank( Control control ) {
		addRuleNotBlank( control, null );
	}
	
	public void addRuleRegex( Control control, String regex, String message ) {
		Objects.requireNonNull( control );
		Objects.requireNonNull( regex );
		if ( message == null || message.isBlank() ) {
			message = BundleMessages.getString( "validation.invalid.value" );
		}
		addRule( control, message, (String t) -> t.matches( regex ) );
	}
	public void addRuleRegex( Control control, String regex ) {
		addRuleRegex( control, regex, null );
	}
	
	public void addRuleEmail( Control control, String message ) {
		if ( message == null || message.isBlank() ) {
			message = BundleMessages.getString( "validation.invalid.email" );
		}
		addRuleRegex( control, "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", message );
	}
	public void addRuleEmail( Control control ) {
		addRuleEmail( control, null );
	}
	
	public void addRuleMinLength( Control control, int minLength, String message ) {
		Objects.requireNonNull( control );
		if ( message == null || message.isBlank() ) {
			message = BundleMessages.getString( "validation.min.length" );
		}
		addRule( control, message, (String t) -> t.length() >= minLength );
	}
	public void addRuleMinLength( Control control, int minLength ) {
		addRuleMinLength( control, minLength, null );
	}
	
	public void addRuleMaxLength( Control control, int maxLength, String message ) {
		Objects.requireNonNull( control );
		if ( message == null || message.isBlank() ) {
			message = BundleMessages.getString( "validation.max.length" );
		}
		addRule( control, message, (String t) -> t.length() <= maxLength );
	}
	public void addRuleMaxLength( Control control, int maxLength ) {
		addRuleMaxLength( control, maxLength, null );
	}

	
	@SuppressWarnings("unchecked")
	public <T> void addRuleMinValue( Control control, T minValue, String message ) {
		Objects.requireNonNull( control );
		Objects.requireNonNull( minValue );
		if ( message == null || message.isBlank() ) {
			message = BundleMessages.getString( "validation.min.value" );
		}
		Converter<T> converter = (Converter<T>) control.getProperties().get( Converter.class );
		if ( converter == null ) {
			if ( minValue instanceof String) {
				final var minValueFinal = (String) minValue;
				addRule( control, message, (String t) -> {
					return minValueFinal.compareToIgnoreCase( t )  <= 0;
				});
			} else if( minValue instanceof Number )  {
				final var minValueFinal = (Number) minValue;
				addRule( control, message, (Number t) -> {
					return minValueFinal.doubleValue() <= t.doubleValue();
				});
			} else if( minValue instanceof Comparable )  {
				final var minValueFinal = (Comparable<T>) minValue;
				addRule( control, message, ( T t) -> {
					return minValueFinal.compareTo( t )  <= 0;
				});
			} else {
				throw new IllegalStateException( "No converter found for this control" );
			}
		} else {
			addRule( control, message, (String t) -> {
				try {
					if ( converter.compare( t, minValue)  >= 0 ) {
						return true;
					}
				} catch (ParseException e) {
				}
				return false;
			});
		}
	}
	public <T> void addRuleMinValue( Control control, T maxValue ) {
		addRuleMinValue( control, maxValue, null );
	}
	
	@SuppressWarnings("unchecked")
	public <T> void addRuleMaxValue( Control control, T maxValue, String message ) {
		Objects.requireNonNull( control );
		Objects.requireNonNull( maxValue );
		if ( message == null || message.isBlank() ) {
			message = BundleMessages.getString( "validation.max.value" );
		}
		Converter<T> converter = (Converter<T>) control.getProperties().get( Converter.class );
		if ( converter == null ) {
			if ( maxValue instanceof String) {
				final var maxValueFinal = (String) maxValue;
				addRule( control, message, (String t) -> {
					return maxValueFinal.compareToIgnoreCase( t )  >= 0;
				});
			} else if( maxValue instanceof Number )  {
				final var maxValueFinal = (Number) maxValue;
				addRule( control, message, (Number t) -> {
					return maxValueFinal.doubleValue() >= t.doubleValue();
				});
			} else if( maxValue instanceof Comparable )  {
				final var maxValueFinal = (Comparable<T>) maxValue;
				addRule( control, message, ( T t) -> {
					return maxValueFinal.compareTo( t )  >= 0;
				});
			} else {
				throw new IllegalStateException( "No converter found for this control" );
			}
		} else {
			addRule( control, message, (String t) -> {
				try {
					if ( converter.compare( t, maxValue) <= 0 ) {
						return true;
					}
				} catch (ParseException e) {
				}
				return false;
			});
		}
	}
	public <T> void addRuleMaxValue( Control control, T maxValue ) {
		addRuleMaxValue( control, maxValue, null );
	}
	
	
	public void addRuleParseError( Control control, Converter<?> converter, String message ) {
		Objects.requireNonNull( control );
		Objects.requireNonNull( converter );
		if ( message == null || message.isBlank() ) {
			message = converter.getMessage();
		}
		control.getProperties().put( Converter.class, converter);
		addRule( control, converter.getMessage(), (Object t) -> ! converter.hasParseError() );
	}
	public <T> void addRuleParseError( Control control, Converter<T> converter ) {
		addRuleParseError(control, converter, null);
	}
	
	
	public void validate() {
		invalid.setValue(false);
		for( var unit :  units.values() ) {
			if ( unit.validate( flagDisabled.get() ) != null ) {
				invalid.setValue(true);
			}
		}
	}
	
	public void validateAndAlert() {
		for( var unit : units.values() ) {
			unit.setFlagAlert(true);;
		}
		validate();
	}
	
	public void reinit() {
		for( var unit : units.values() ) {
			unit.reinitTarget();
		}
	}
	
	public void reset() {
		for( var unit : units.values() ) {
			unit.reinitTarget();
			unit.resetAlert();
		}
	}
	
	
	// Helpers
	
	private void refreshStatus() {
		invalid.setValue(false);
		for( var unit :  units.values() ) {
			if ( unit.isInvalid() ) {
				invalid.setValue(true);
			}
		}
		
	}

}