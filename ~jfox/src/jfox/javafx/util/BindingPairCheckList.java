package jfox.javafx.util;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;
import javafx.util.Pair;
import javafx.util.StringConverter;


public class BindingPairCheckList<T> {
	
	
	// Champs
	
	private final ObservableList<Pair<T, BooleanProperty>> list = FXCollections.observableArrayList();
	
	private final List<CheckBoxListCell<?>> cells = new ArrayList<>();
	private final List<CheckBox> checkBoxes = new ArrayList<>();
	
	
	// Constructeur
	
	public BindingPairCheckList( List<T> keys, ObservableList<T> observableList ) {

    	for ( T key : keys  ) {
    		list.add( new Pair<T, BooleanProperty>(key, new SimpleBooleanProperty() ) );
    	}

    	// De la liste vers courant
		for ( var pair : list) {
			pair.getValue().addListener((obs) -> {
				if (pair.getValue().get()) {
					if (!observableList.contains(pair.getKey())) {
						observableList.add(pair.getKey());
					}
				} else {
					observableList.remove(pair.getKey());
				}
			});
		}    		
    	
    	// De courant vers la liste
		var items = new ArrayList<>( observableList ); 
		observableList.clear();
    	observableList.addListener(
        	(ListChangeListener<T>)	c -> {
    			c.next();
    			for ( var pair : list ) {
    				if ( c.getAddedSubList().contains( pair.getKey() ) ) {
    					pair.getValue().set( true );
    				}
    				if ( c.getRemoved().contains( pair.getKey() ) ) {
    					pair.getValue().set( false );
    				}
    			}
        });
    	observableList.addAll( items );
	}

	
	public void configureListView( 
			ListView<Pair<T, BooleanProperty>> listView,
			Callback<Pair<T, BooleanProperty>, String> callback
	) {
		
		StringConverter<Pair<T, BooleanProperty>> converter = null;
		if ( callback != null ) {
	    	converter = new StringConverter<>() {
				@Override
				public String toString(Pair<T, BooleanProperty> pair) {
						return callback.call(pair);
				}
				@Override
				public Pair<T, BooleanProperty> fromString(String string) {
					return null;
				}
			};
			
		}
    	
		listView.setItems( list );
		
		final var converter2 = converter;
		listView.setCellFactory( lsv -> {
			var cell = new CheckBoxListCell<Pair<T, BooleanProperty>>(
	        		item -> item.getValue(),
	        		converter2
			);
			cells.add( cell );
			return cell;
		});
	}

	
	public void configureListView( 
			ListView<Pair<T, BooleanProperty>> listView
	) {
		configureListView( listView, null );
	}

	
	public void setReadOnly( boolean flagReadOnly ) {
		
		Platform.runLater( () -> {
			if ( checkBoxes.isEmpty() && ! cells.isEmpty() ) {
				for ( var cell : cells ) {
					for( var node : cell.getChildrenUnmodifiable() ) {
						if ( node instanceof CheckBox ) {
							checkBoxes.add( (CheckBox) node );
						}
					}
				}
			}
			for( var cb : checkBoxes ) {
				cb.setMouseTransparent( flagReadOnly );
				cb.setFocusTraversable( ! flagReadOnly );
			}
		});
	}
}
