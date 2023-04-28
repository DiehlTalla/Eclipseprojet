package contacts.view;

import javafx.scene.Scene;
import javafx.stage.Stage;
import jfox.javafx.view.IConfigDialog;
import jfox.javafx.view.View;


public class ConfigDialog implements IConfigDialog {


	//-------
	// Actions
	//-------

	@Override
	public void configureStage( Stage stage ) {
		
		// Configure le stage
		stage.sizeToScene();
		stage.setResizable( false );
		
	}


	@Override
	public Scene createScene( View view ) {
		Scene scene = new Scene( view.getRoot() );
		scene.getStylesheets().add( this.getClass().getResource("application.css").toExternalForm() );
		return scene;
	}

}
