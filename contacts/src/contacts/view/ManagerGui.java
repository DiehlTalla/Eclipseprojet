package contacts.view;

import contacts.view.systeme.ViewConnexion;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import jfox.javafx.view.ManagerGuiAbstract;
import jfox.javafx.view.View;


public class ManagerGui extends ManagerGuiAbstract {

	
	//-------
	// Actions
	//-------

	@Override
	public void configureStage()  {
		
		// Choisit la vue à afficher
		showView( ViewConnexion.class );
		
		// Configure le stage
		stage.setTitle( "Gestion de contacts" );
		stage.setWidth(600);
		stage.setHeight(440);
		stage.setMinWidth(340);
		stage.setMinHeight(300);
//		stage.sizeToScene();
		stage.setResizable( true );
		stage.getIcons().add(new Image(getClass().getResource("icone.png").toExternalForm()));
		
		// Configuration par défaut pour les boîtes de dialogue
		typeConfigDialogDefault = ConfigDialog.class;
	}

	
	@Override
	public Scene createScene( View view ) {
		BorderPane paneMenu = new BorderPane( view.getRoot() );
		paneMenu.setTop( (Node) factoryController.call( MenuBarAppli.class ) );
		Scene scene = new Scene( paneMenu );
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		return scene;
	}
	
}