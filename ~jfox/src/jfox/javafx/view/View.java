package jfox.javafx.view;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.scene.Parent;
import javafx.scene.Scene;
import jfox.javafx.util.UtilFX;


public class View {
	
	// Champs
	
	private final URL		location;
	private final boolean	flagTransient;
	private Class<? extends IConfigDialog> typeConfigDialog = ConfigDialogDefault.class;

	private Parent		root;
	private ControllerAbstract	controller;
	private Scene		scene;
	private final List<Object>	objectsToClose = new ArrayList<>();

	
	// Constructeurs
	
	public View( URL location, boolean flagTransient, Class<? extends IConfigDialog> typeConfigDialog ) {
		Objects.requireNonNull(location);
		this.location = location;
		this.flagTransient = flagTransient;
		this.typeConfigDialog = typeConfigDialog;
	}
	
	public View( URL location, Class<? extends IConfigDialog> typeConfigDialog ) {
		this( location, false, typeConfigDialog );
	}
	
	public View( URL location, boolean flagTransient ) {
		this( location, flagTransient, null );
	}
	
	public View( URL location ) {
		this( location, false, null );
	}
	
	public View( IEnumView idview, String path, boolean flagTransient, Class<? extends IConfigDialog> typeConfigDialog ) {
		this( getLocation(idview, path), flagTransient, typeConfigDialog);
	}
	
	public View( IEnumView idview, String path, Class<? extends IConfigDialog> typeConfigDialog ) {
		this( getLocation(idview, path), false, typeConfigDialog);
	}
	
	public View( IEnumView idview, String path, boolean flagTransient ) {
		this( idview, path, flagTransient, null );
	}
	
	public View( IEnumView idview, String path ) {
		this( idview, path, false, null );
	}

	private static URL getLocation( IEnumView idview, String path ) {
		Objects.requireNonNull(idview);
		Objects.requireNonNull(path);
		var location = idview.getClass().getResource(path);
		if( location == null ) {
			throw UtilFX.runtimeException( new FileNotFoundException( path ) );
		}
		return location;
	}
	
	
	// Getters & setters
	
	public URL getLocation() {
		return location;
	}

	public boolean isFlagTransient() {
		return flagTransient;
	}
	
	public Class<? extends IConfigDialog> getTypeConfigDialog() {
		return typeConfigDialog;
	}
	
	public Parent getRoot() {
		return root;
	}
	
	public void setRoot(Parent root) {
		this.root = root;
	}
	
	public ControllerAbstract getController() {
		return controller;
	}

	public void setController(ControllerAbstract controller) {
		objectsToClose.remove(this.controller);
		this.controller = controller;
		addObjectToClose(controller);
	}
	
	public Scene getScene() {
		return scene;
	}
	
	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public void addObjectToClose( Object obj ) {
		if ( obj != null ) {
			objectsToClose.add( obj );
		}
	}

	public List<Object> getObjectsToClose() {
		return objectsToClose;
	}

}
