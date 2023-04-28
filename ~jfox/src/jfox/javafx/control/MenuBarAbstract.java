package jfox.javafx.control;

import java.util.Objects;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;


public class MenuBarAbstract extends MenuBar {

	
	// Actions
	
	
	protected Menu addMenu(
			String title,
			Menu parent,
			ObservableValue<Boolean> falgVisible
	) {
		Objects.requireNonNull( title );
		var menu = new Menu( title );
		if( parent == null ) {
			this.getMenus().add( menu );
		} else {
			parent.getItems().add( menu );
		}
		if( falgVisible != null ) {
			menu.visibleProperty().bind(falgVisible);
		}
		return menu;
	}

	protected Menu addMenu(
			String title,
			Menu parent
	) {
		return addMenu( title, parent, null );
	}

	protected Menu addMenu(
			String title,
			ObservableValue<Boolean> falgVisible
	) {
		return addMenu( title, null, falgVisible );
	}

	protected Menu addMenu(
			String title
	) {
		return addMenu( title, null, null );
	}
	
	protected MenuItem addMenuItem(
			String title,
			Menu parent,
			ObservableValue<Boolean> falgVisible,
			EventHandler<ActionEvent>	handler
	) {
		Objects.requireNonNull( parent );
		Objects.requireNonNull( title );
		var item = new MenuItem( title );
		parent.getItems().add( item );
		if( handler != null ) {
			item.setOnAction(handler ) ;
		}
		if( falgVisible != null ) {
			item.visibleProperty().bind(falgVisible);
		}
		return item;
	}
	
	protected MenuItem addMenuItem(
			String title,
			Menu parent,
			EventHandler<ActionEvent>	handler
	) {
		return addMenuItem( title, parent, null, handler );
	}
	
}
