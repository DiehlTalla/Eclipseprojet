package jfox.start;
import jfox.javafx.view.IConfigDialog;
import jfox.javafx.view.IEnumView;
import jfox.javafx.view.View;


public enum EnumView implements IEnumView {

	
	//-------
	// Valeurs
	//-------
	
	Home			( "ViewHome.fxml" ),
	;

	
	//-------
	// Champs
	//-------
	
	private final View	view;

	
	//-------
	// Constructeurs
	//-------
	
	
	EnumView( String path ) {
		view = new View( this, path );
	}
	
	EnumView( String path, boolean flagReuse ) {
		view = new View( this, path, flagReuse);
	}
	
	EnumView( String path, Class<? extends IConfigDialog> typeConfigDialog ) {
		view = new View( this, path, typeConfigDialog );
	}
	
	EnumView( String path, boolean flagReuse, Class<? extends IConfigDialog> typeConfigDialog ) {
		view = new View( this, path, flagReuse, typeConfigDialog );
	}

	
	//-------
	// Getters & setters
	//-------
	
	@Override
	public View getView() {
		return view;
	}
}
