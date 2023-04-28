package jfox.javafx.view;

import javafx.scene.Scene;
import javafx.stage.Stage;


public class ConfigDialogDefault implements IConfigDialog {


	// Actions

	@Override
	public void configureStage( Stage stage ) {
		
		// Configure le stage
		stage.sizeToScene();
		stage.setResizable( false );

	}


	@Override
	public Scene createScene( View view ) {
		Scene scene = new Scene( view.getRoot() );
		return scene;
	}

}
