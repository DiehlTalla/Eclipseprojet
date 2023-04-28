package jfox.javafx.view;

import java.lang.ref.WeakReference;
import java.time.LocalDate;
import java.util.Objects;
import java.util.function.UnaryOperator;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import jfox.javafx.util.converter.Converter;
import jfox.javafx.util.converter.ConverterLocalDate;
import jfox.javafx.validation.Validator;


public abstract class ControllerAbstract {

	
	// Champs
	
	protected Validator validator = new Validator();
	
	
	// Getters
	
	public Validator getValidation() {
		return validator;
	}
	
	
	// Refresh
	
	public void refresh() {
	}
	
	
	// Bind sans convertisseur
	
	protected <T> void bind( Labeled control, ObservableValue<String> observable ) {
		control.textProperty().bind( observable ) ;
	}
	
	protected void bind( ImageView imageView, Property<Image> property ) {
		imageView.imageProperty().bind( property );
	}
	
	
	// Bind avec convertisseur
	
	protected <T> void bind( Labeled control, ObservableValue<T> observable, Converter<T> converter ) {
		control.textProperty().bind( Bindings.createStringBinding(
				() -> converter.toString(observable.getValue()), observable));
	}
	
	
	
	// Bind bidirectionnel simple (sans convertisseur)
	
	protected void bindBidirectional( Labeled control, Property<String> property ) {
		control.textProperty().bindBidirectional( property );
	}
	
	protected void bindBidirectional( TextInputControl control, Property<String> property ) {
		control.textProperty().bindBidirectional( property );
	}
	
	protected void bindBidirectional( CheckBox control, Property<Boolean> property ) {
		control.selectedProperty().bindBidirectional( property );
	}
	
	protected void bindBidirectional( Toggle control, Property<Boolean> property ) {
		control.selectedProperty().bindBidirectional( property );
	}
	
	protected <T> void bindBidirectional( ChoiceBox<T> control, Property<T> property ) {
		control.valueProperty().bindBidirectional( property );
	}
	
	protected <T> void bindBidirectional( ComboBox<T> control, Property<T> property ) {
		control.valueProperty().bindBidirectional( property );
	}
	
	protected void bindBidirectional( DatePicker datePicker, Property<LocalDate> property ) {
		bindBidirectional(datePicker, property, new ConverterLocalDate() );
	}
	
	protected void bindBidirectional( ImageView imageView, Property<Image> property ) {
		imageView.imageProperty().bindBidirectional( property );
	}
	
	
	// Bind bidirectionnel avec convertisseur
	
	protected <T> void bindBidirectional( Labeled control, Property<T> property, Converter<T> converter ) {
		Objects.requireNonNull( control );
		Objects.requireNonNull( property );
		Objects.requireNonNull( converter );
		control.textProperty().bindBidirectional( property, converter );
		validator.addRuleParseError( control, converter );
		control.focusedProperty().addListener( (obs, ov, nv) -> {
			if ( ! control.isFocused() && ! converter.hasParseError() ) {
				control.setText( converter.toString( property.getValue() ) );
			}
		});
	}
	
	protected <T> void bindBidirectional( TextInputControl control, Property<T> property, Converter<T> converter ) {
		Objects.requireNonNull( control );
		Objects.requireNonNull( property );
		Objects.requireNonNull( converter );
		control.textProperty().bindBidirectional( property, converter );
		validator.addRuleParseError( control, converter );
		control.focusedProperty().addListener( (obs, ov, nv) -> {
			if ( ! control.isFocused() && ! converter.hasParseError() ) {
				control.setText( converter.toString( property.getValue() ) );
			}
		});
	}
	
	protected <T> void bindBidirectional( ChoiceBox<T> choiceBox, Property<T> property, Converter<T> converter ) {
		Objects.requireNonNull( choiceBox );
		Objects.requireNonNull( property );
		Objects.requireNonNull( converter );
		choiceBox.valueProperty().bindBidirectional( property );
		choiceBox.setConverter( converter );
		validator.addRuleParseError( choiceBox, converter );
	}
	
	protected <T> void bindBidirectional( ComboBox<T> comboBox, Property<T> property, Converter<T> converter ) {
		Objects.requireNonNull( comboBox );
		Objects.requireNonNull( property );
		Objects.requireNonNull( converter );
		comboBox.valueProperty().bindBidirectional( property );
		comboBox.setConverter( converter );
		comboBox.getEditor().textProperty().addListener( obs -> {
			converter.fromString(comboBox.getEditor().getText());
			if (  ! converter.hasParseError() ) {
				comboBox.commitValue();
			}
	    });
		validator.addRuleParseError( comboBox, converter );
	}
	
	protected <T> void bindBidirectional( DatePicker datePicker, Property<LocalDate> property, Converter<LocalDate> converter ) {
		Objects.requireNonNull( datePicker );
		Objects.requireNonNull( property );
		datePicker.valueProperty().bindBidirectional( property );
		if( converter == null ) {
			converter = new ConverterLocalDate();
		}
		datePicker.setConverter( converter );
		validator.addRuleParseError( datePicker, converter );
		final var converterFinal = converter;
		
		var filter = new UnaryOperator<Change>() {
			
			private boolean flagWorking;
			
			@Override
			public Change apply(Change c) {
				
				if ( flagWorking ) {
					return null;
				}
				try {
					flagWorking = true;
					datePicker.setValue(  converterFinal.fromString( c.getControlNewText()   )  );
					return c;
				} finally {
					flagWorking = false;
				}
			}
		};
		
//		var filter = new UnaryOperator<Change>() {
//			@Override
//			public Change apply(Change c) {
//				if ( c.isContentChange() ) {
//					converterFinal.fromString( c.getControlNewText() );
//				}
//				return c;
//			}
//		};

		datePicker.getEditor().setTextFormatter(new TextFormatter<LocalDate>(filter));
		
		datePicker.focusedProperty().addListener( obs -> {
			if ( ! datePicker.isFocused() && ! converterFinal.hasParseError() ) {
				datePicker.getEditor().setText( converterFinal.toString( datePicker.getValue() ) );
			}
		});
		
	}

	
	// Bind bidirectionnel pour ToggleGroup

	@SuppressWarnings("unchecked")
	protected <T> void bindBidirectional( final ToggleGroup tgg, final Property<T> property, T...userData ) {
		Objects.requireNonNull( tgg );
		Objects.requireNonNull( property );

		for ( int i=0; i < userData.length && i < tgg.getToggles().size(); ++i ) {
			tgg.getToggles().get(i).setUserData( userData[i] ) ;
		}
		
		final var busy = new SimpleBooleanProperty( false );
		
		tgg.selectedToggleProperty().addListener( obs -> {
			if ( ! busy.getValue() ) {
				busy.setValue(true);
				// Modifie le statut en fonction du bouton radio sélectionné
				property.setValue( (T) tgg.getSelectedToggle().getUserData() );
				busy.setValue(false);
			}
		});
		
		InvalidationListener listenerProperty = obs -> {
			if ( ! busy.getValue() ) {
				busy.setValue(true);
				// Déselectionne le bouton sélectionné s'il y en a un
				var selected = tgg.getSelectedToggle();
				if ( selected != null ) {
					selected.setSelected(false);
				}
				// Sélectionne le bouton radio correspondant au statut
				for ( Toggle t : tgg.getToggles() ) {
					if ( t.getUserData().equals( property.getValue()  )) {
						t.setSelected(true);
						break;
					}
				}
				busy.setValue(false);
			}
		};
		
		property.addListener( listenerProperty );
		listenerProperty.invalidated( property );
	}
	
	
	// Bind bidirectionnel pour ListView et TableView
	
	protected <T> void bindBidirectional( ListView<T> listView, Property<T> property, ObservableValue<Boolean> flagDisable ) {
		new BindingSelection<T>( listView.getSelectionModel(), property, flagDisable,
				() -> {
					var index = listView.getSelectionModel().getSelectedIndex();
					if( index >= 0 ) {
						listView.scrollTo( index );
					}
				}
		);
		listView.getSelectionModel();
	}
	protected <T> void bindBidirectional( ListView<T> listView, Property<T> property ) {
		bindBidirectional(listView, property, null);
	}
	
	protected <T> void bindBidirectional( TableView<T> tableView, Property<T> property, ObservableValue<Boolean> flagDisable ) {
		new BindingSelection<T>( tableView.getSelectionModel(), property, flagDisable,
				() -> {
					var index = tableView.getSelectionModel().getSelectedIndex();
					if( index >= 0 ) {
						tableView.scrollTo( index );
					}
				}
		);
		tableView.getSelectionModel();
	}
	
	protected <T> void bindBidirectional( TableView<T> tableView, Property<T> property ) {
		bindBidirectional(tableView, property, null);
	}
	
	private static class BindingSelection<T> implements InvalidationListener, WeakListener {

		private WeakReference<SelectionModel<T>> refSelectionModel;
		private WeakReference<Property<T>> refProperty;
		private WeakReference<ObservableValue<Boolean>> refFlagDisable;
		private Runnable managerFocus;
		private boolean flagUpdating;
		
		private BindingSelection( 
				SelectionModel<T> selectionModel, 
				Property<T> property, 
				ObservableValue<Boolean> flagDisable,
				Runnable managerFocus ) {
			Objects.requireNonNull( selectionModel );
			Objects.requireNonNull( property );
			refSelectionModel = new WeakReference<>( selectionModel );
			selectionModel.selectedItemProperty().addListener(this);;
			selectionModel.getSelectedItem();
			refProperty = new WeakReference<>( property );
			property.addListener(this);
			property.getValue();
			refFlagDisable = new WeakReference<>( flagDisable );
			if ( flagDisable != null ) {
				flagDisable.addListener( this );
				flagDisable.getValue();
			}
			this.managerFocus = managerFocus;
		}

		@Override
		public void invalidated( Observable source ) {
            if (! flagUpdating ) {
                final SelectionModel<T> selectionModel = refSelectionModel.get();
                final Property<T> property = refProperty.get();
                final ObservableValue<Boolean> flagDisable = refFlagDisable.get();
                if ((selectionModel == null) || (property == null)) {
                    if (selectionModel != null) {
                    	selectionModel.selectedItemProperty().removeListener(this);
                    }
                    if (property != null) {
                    	property.removeListener(this);
                    }
                    if (flagDisable != null) {
                    	flagDisable.removeListener(this);
                    }
                } else {
                    try {
                        flagUpdating = true;
                        if ( source == selectionModel.selectedItemProperty()
                        		&& ( flagDisable == null || ! flagDisable.getValue()  ) ) {
                            property.setValue( selectionModel.getSelectedItem() );
                            property.getValue();
                        }
                        if ( source == property
                        		|| ( source == flagDisable && ! flagDisable.getValue() ) ) {
                        	selectionModel.select( property.getValue() );
                        	selectionModel.getSelectedItem();
                        	Platform.runLater( managerFocus );
                        }
                    } finally {
                        flagUpdating = false;
                    }			
                }			
            }			
		}
		
		@Override
		public boolean wasGarbageCollected() {
	        return ( refSelectionModel.get() == null ) || ( refProperty.get() == null );
		}
	}
	
}
