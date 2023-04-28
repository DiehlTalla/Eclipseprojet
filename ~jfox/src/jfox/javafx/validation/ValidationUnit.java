package jfox.javafx.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;
import jfox.javafx.util.converter.Converter;


class ValidationUnit {
	
	
	// Fields
	
	private static final String CSS_ERROR = "validation-error";
	private static final String CSS_OK = "validation-ok";
	private static final String CSS_TOOLTIP_ERROR = "tooltip-error";
	
	private final Control		target;
	private final Callback<Control, Object>	extractorValue;
	private final ObservableValue<?> observable;
	private final Converter<?>	converter;
	private final List<Rule>	rules	= new ArrayList<>();
	
	private boolean		flagAlertAlways = true;
	private boolean		flagAlert = true;

	private String		message;
	private Tooltip		toolTipDefault;
	private Tooltip		toolTipError;
	
	
	// Constructor
	
	ValidationUnit( Control control ) {
		target = control;
		extractorValue = Extractors.getExtractorVal(control);
		observable = Extractors.getObservable(control);
		converter = (Converter<?>) target.getProperties().get( Converter.class );		toolTipDefault = control.getTooltip();
		toolTipError = new Tooltip( "Error" );
		toolTipError.getStyleClass().add( CSS_TOOLTIP_ERROR );
		if (! target.getStyleClass().contains( CSS_OK ) ) {
			target.getStyleClass().add( CSS_OK );
		}
	}
	
	// Getters & Setters
	
	String getMessage() {
		return message;
	}
	
	boolean isInvalid() {
		return message != null;
	}
	
	void setFlagAlertAlways(boolean flagAlertAlways) {
		this.flagAlertAlways = flagAlertAlways;
	}
	
	void setFlagAlert(boolean flagAlert) {
		this.flagAlert = flagAlert;
	}

	
	// Actions

	void addRule( Rule rule ) {
		Objects.requireNonNull( rule );
		try {
			if( rule.check(null)!= null ) {
				rules.add(0, rule);
				return;
			}
		} catch (NullPointerException e) {
		}
		rules.add( rule  );
	}
	
	String validate( boolean flagDisabled ) {
		message = null;
		if ( ! flagDisabled ) {
			for ( var rule : rules ) {
				try {
					String message = rule.check( extractorValue.call(target) );
					if ( message != null ) {
						this.message = message;
						break;
					}
				} catch (NullPointerException e) {
				}
			}
		}
		decorate( message != null && flagAlert );
		return message;
	}
	
	void reinitTarget() {
		if ( converter != null && converter.hasParseError() && observable instanceof StringProperty ) {
			((StringProperty) observable).set(null);
		}
	}
	
	void resetAlert() {
		flagAlert = flagAlertAlways;
//		decorate(false);
		decorate( message != null && flagAlert );
	}
	
	
	// Helper methods
	
	private void decorate(boolean flagInvalid ) {
		if ( flagInvalid ) {
			toolTipError.setText(message);
			setTooltip(target, toolTipError);
			target.getStyleClass().remove(  CSS_OK  );
			if (! target.getStyleClass().contains( CSS_ERROR ) ) {
				target.getStyleClass().add( CSS_ERROR );
			}
		} else {
			setTooltip(target, toolTipDefault);
			target.getStyleClass().remove(  CSS_ERROR  );
			if (! target.getStyleClass().contains( CSS_OK ) ) {
				target.getStyleClass().add( CSS_OK );
			}
		}
		
	}
	
	private void setTooltip( Control target, Tooltip toolTip ) {
		try {
			target.setTooltip(toolTip);
		} catch (Exception e) {
			if ( target.getParent() instanceof Control ) {
				((Control) target.getParent()).setTooltip(toolTip);
			}
		}
	}
	
}
