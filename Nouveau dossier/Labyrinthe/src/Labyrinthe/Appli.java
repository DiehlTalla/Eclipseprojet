package Labyrinthe;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import jfox.context.ContextGlobal;
import jfox.context.IContext;
import jfox.javafx.util.UtilFX;


public class Appli extends Application {

	
	//-------
	// Champs
	//-------
	
	private IContext context;
	
	
	//-------
	// Actions
	//-------
	
	@Override
	public final void start(Stage stagePrimary) {

		try {
			
			// Context
			context = new ContextGlobal();

			// ManagerGui
	    	ManagerGui managerGui = context.getBean( ManagerGui.class );
	    	managerGui.setFactoryController( context::getBean );
			managerGui.setStage( stagePrimary );
			managerGui.configureStage();
			
			// Affiche le stage
			stagePrimary.show();
			
		} catch(Exception e) {
			UtilFX.unwrapException(e).printStackTrace();
	        Alert alert = new Alert(AlertType.ERROR);
	        alert.setHeaderText( "Impossible de démarrer l'application." );
	        alert.showAndWait();
	        Platform.exit();
		}

	}
	
	@Override
	public final void stop() throws Exception {
		if (context != null ) {
			context.close();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}