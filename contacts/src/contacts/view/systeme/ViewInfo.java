package contacts.view.systeme;

import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import jfox.javafx.view.ControllerAbstract;


public class ViewInfo extends ControllerAbstract {
	
	
	//-------
	// Composants de la vue
	//-------
	
	@FXML
	private Label		labTitre;
	@FXML
	private Label		labMessage;

	
	//-------
	// Autres champs
	//-------
	
	@Inject
	private ModelInfo	modelInfo;
	
	
	//-------
	// Initialisations
	//-------
	
	@FXML
	private void initialize() {
		
		// Data binding
		bind( labTitre, modelInfo.titreProperty() );
		bind( labMessage, modelInfo.messageProperty() );
		
	}
	

}
