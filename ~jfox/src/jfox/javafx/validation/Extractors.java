package jfox.javafx.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.util.Callback;


class Extractors {
	
	//-------
	// Fields
	//-------

//    private static final List<ObservableExtractor> extractorsObs = FXCollections.observableArrayList(); 
//    private static final List<ValueExtractor> extractorsVal = FXCollections.observableArrayList();
    private static final List<ExtractorObs> extractorsObs = new ArrayList<>(); 
    private static final List<ExtractorVal> extractorsVal = new ArrayList<>();

    
    // Private constructor to avoid instanciation
    
    private Extractors() {
    }

    
    
	//-------
    // Actions
	//-------
	

    static final Callback<Control, ObservableValue<?>> getExtractorObs(final Control c) {
        for( var e: extractorsObs ) {
            if ( e.applicability.test(c)) return e.extraction;
        }
        throw new IllegalArgumentException( "No ObservableExtractor found for type " + c.getClass()  );  
    }
	
    static final Callback<Control, Object> getExtractorVal(final Control n) {
        for( var e: extractorsVal ) {
            if ( e.applicability.test(n)) return e.extraction;
        }
        throw new IllegalArgumentException( "No ValueExtractor found for type " + n.getClass()  );  
    }
    
    static ObservableValue<?> getObservable(Control c) {
    	for( ExtractorObs e: extractorsObs ) {
            if ( e.applicability.test(c)) return e.extraction.call(c);
        }
        return null;
    }
    
    static Object getValue(Control n) {
    	for( ExtractorVal e: extractorsVal ) {
            if ( e.applicability.test(n)) return e.extraction.call(n);
        }
        return null;
    }


	//-------
    // Helper methods
	//-------
    
    static void addExtractorObs( Predicate<Control> test, Callback<Control, ObservableValue<?>> extract ) {
        extractorsObs.add( new ExtractorObs(test, extract));
    }
    
    static void addExtractorVal( Predicate<Control> test, Callback<Control, Object> extractor) {
        extractorsVal.add( new ExtractorVal(test, extractor));
    }


	//-------
    // Helper classes
	//-------
    
	private static class ExtractorObs {

        public final Predicate<Control> applicability;
		public final Callback<Control, ObservableValue<?>> extraction;

        public ExtractorObs( Predicate<Control> applicability, Callback<Control, ObservableValue<?>> extraction ) {
            this.applicability = Objects.requireNonNull(applicability);
            this.extraction    = Objects.requireNonNull(extraction);
        }

    }
    
    
    private static class ExtractorVal {

        public final Predicate<Control> applicability;
		public final Callback<Control, Object> extraction;

        public ExtractorVal( Predicate<Control> applicability, Callback<Control, Object> extraction ) {
            this.applicability = Objects.requireNonNull(applicability);
            this.extraction    = Objects.requireNonNull(extraction);
        }

    }
    
    
	//-------
    // Anonymous static methods
	//-------

    static {
        addExtractorObs( c -> c instanceof Labeled, c -> ((Labeled)c).textProperty());
        addExtractorObs( c -> c instanceof TextInputControl, c -> ((TextInputControl)c).textProperty());
        addExtractorObs( c -> c instanceof ComboBox && !((ComboBox<?>)c).isEditable(), c -> ((ComboBox<?>)c).valueProperty());
        addExtractorObs( c -> c instanceof ComboBox && ((ComboBox<?>)c).isEditable(),  c -> ((ComboBox<?>)c).getEditor().textProperty() );
        addExtractorObs( c -> c instanceof ChoiceBox,        c -> ((ChoiceBox<?>)c).valueProperty());
        addExtractorObs( c -> c instanceof CheckBox,         c -> ((CheckBox)c).selectedProperty());
        addExtractorObs( c -> c instanceof Slider,           c -> ((Slider)c).valueProperty());
        addExtractorObs( c -> c instanceof ColorPicker,      c -> ((ColorPicker)c).valueProperty());
//        addObservableExtractor( c -> c instanceof DatePicker,       c -> ((DatePicker)c).valueProperty());
        addExtractorObs( c -> c instanceof DatePicker,       c -> ((DatePicker)c).getEditor().textProperty() );

        addExtractorObs( c -> c instanceof ListView,         c -> ((ListView<?>)c).itemsProperty());
        addExtractorObs( c -> c instanceof TableView,        c -> ((TableView<?>)c).itemsProperty());

        // FIXME: How to listen for TreeView changes???
        //addObservableValueExtractor( c -> c instanceof TreeView,         c -> ((TreeView<?>)c).Property());
    }
    
    static {
        addExtractorVal( n -> n instanceof Labeled, c -> ((Labeled) c).getText());
        addExtractorVal( n -> n instanceof TextInputControl, ta -> ((TextInputControl)ta).getText());
        addExtractorVal( n -> n instanceof CheckBox,         cb -> ((CheckBox)cb).isSelected());
        addExtractorVal( n -> n instanceof ChoiceBox,        cb -> ((ChoiceBox<?>)cb).getValue());
        addExtractorVal( n -> n instanceof ComboBox && !((ComboBox<?>)n).isEditable(), cb -> ((ComboBox<?>)cb).getValue());
        addExtractorVal( n -> n instanceof ComboBox && ((ComboBox<?>)n).isEditable(),  cb -> ((ComboBox<?>)cb).getEditor().getText() );
//        addValueExtractor( n -> n instanceof DatePicker,       dp -> ((DatePicker)dp).getValue());
        addExtractorVal( n -> n instanceof DatePicker,       dp -> ((DatePicker)dp).getEditor().getText() );
        addExtractorVal( n -> n instanceof RadioButton,      rb -> ((RadioButton)rb).isSelected());
        addExtractorVal( n -> n instanceof Slider,           sl -> ((Slider)sl).getValue());
        
        addExtractorVal( n -> n instanceof ListView, lv -> {
            MultipleSelectionModel<?> sm = ((ListView<?>)lv).getSelectionModel();
            return sm.getSelectionMode() == SelectionMode.MULTIPLE ? sm.getSelectedItems() : sm.getSelectedItem();
        });
        addExtractorVal( n -> n instanceof TreeView, tv -> {
            MultipleSelectionModel<?> sm = ((TreeView<?>)tv).getSelectionModel();
            return sm.getSelectionMode() == SelectionMode.MULTIPLE ? sm.getSelectedItems() : sm.getSelectedItem();
        });
        addExtractorVal( n -> n instanceof TableView, tv -> {
            MultipleSelectionModel<?> sm = ((TableView<?>)tv).getSelectionModel();
            return sm.getSelectionMode() == SelectionMode.MULTIPLE ? sm.getSelectedItems() : sm.getSelectedItem();
        });
        addExtractorVal( n -> n instanceof TreeTableView, tv -> {
            MultipleSelectionModel<?> sm = ((TreeTableView<?>)tv).getSelectionModel();
            return sm.getSelectionMode() == SelectionMode.MULTIPLE ? sm.getSelectedItems() : sm.getSelectedItem();
        });
    }


}