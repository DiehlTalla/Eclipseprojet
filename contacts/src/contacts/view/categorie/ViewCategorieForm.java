package contacts.view.categorie;

import java.time.LocalDate;

import contacts.view.ManagerGui;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import jfox.javafx.util.converter.ConverterInteger;
import jfox.javafx.view.ControllerAbstract;


public class ViewCategorieForm extends ControllerAbstract {

	
	//-------
	// Composants de la vue
	//-------
	
	@FXML
	private Label			labId;
	@FXML
	private TextField		txfLibelle;
	@FXML
	private DatePicker		dtpDebut;
	@FXML
	private Button			btnValider;


	//-------
	// Autres champs
	//-------
	
	@Inject
	private ManagerGui		managerGui;
	@Inject
	private ModelCategorie	modelCategorie;


	//-------
	// Initialisation du Controller
	//-------

	@FXML
	private void initialize() {
		
		var draft = modelCategorie.getDraft();

		// Id
		bind( labId, draft.idProperty(), new ConverterInteger() );
		
		// Libellé
		bindBidirectional( txfLibelle, draft.libelleProperty()  );
		validator.addRuleNotBlank(txfLibelle);
		validator.addRuleMaxLength(txfLibelle, 25 );
		
		// Date début
		bindBidirectional( dtpDebut, draft.debutProperty() );
		validator.addRuleMinValue( dtpDebut, LocalDate.of(1900, 1, 1) );
		
		// Bouton VAlider
		btnValider.disableProperty().bind( validator.invalidProperty() );
	}
	
	
	@Override
	public void refresh() {
		txfLibelle.requestFocus();
	}
	
	
	//-------
	// Actions
	//-------
	
	@FXML
	private void doAnnuler() {
		managerGui.showView( ViewCategorieList.class );
	}
	
	@FXML
	private void doValider() {
		modelCategorie.saveDraft();
		managerGui.showView( ViewCategorieList.class );
	}

}
