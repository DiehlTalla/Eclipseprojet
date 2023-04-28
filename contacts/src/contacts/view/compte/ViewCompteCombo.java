package contacts.view.compte;

import contacts.commun.Roles;
import contacts.data.Compte;
import contacts.view.ManagerGui;
import jakarta.inject.Inject;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import jfox.javafx.util.BindingPairCheckList;
import jfox.javafx.util.UtilFX;
import jfox.javafx.util.converter.ConverterInteger;
import jfox.javafx.view.ControllerAbstract;
import jfox.javafx.view.Mode;


public class ViewCompteCombo extends ControllerAbstract {

	
	//-------
	// Composants de la vue
	//-------
	
	@FXML
	private ListView<Compte>	lsvComptes;
	@FXML
	private Button				btnAjouter;
	@FXML
	private Button				btnSupprimer;
	
	@FXML
	private Label				labId;
	@FXML
	private TextField			txfPseudo;
	@FXML
	private TextField			txfMotDePasse;
	@FXML
	private TextField			txfEmail;
	@FXML
	private ListView<Pair<String, BooleanProperty>>	lsvRoles;
	
	@FXML
	private Button				btnValider;

	
	//-------
	// Autres champs
	//-------
	
	@Inject
	private ManagerGui			managerGui;
	@Inject
	private ModelCompte			modelCompte;
	
	
	//-------
	// Initialisations
	//-------
	
	@FXML
	private void initialize() {
		
		// Partie liste
		
		// ListView des comptes
		lsvComptes.setItems( modelCompte.getList() );
		UtilFX.setCellFactory( lsvComptes, "pseudo" );
		bindBidirectional( lsvComptes, modelCompte.currentProperty(), modelCompte.flagRefreshingListProperty() );

		// Comportement si modificaiton de la séleciton
		lsvComptes.getSelectionModel().selectedItemProperty().addListener( (obs, ov, nv) -> {
			initDraft();
			configurerBoutons( );
		});
		initDraft();
		configurerBoutons();
		
		
		// Partie formulaire
		
		var draft = modelCompte.getDraft();

		// Id
		bind( labId, draft.idProperty(), new ConverterInteger() );
		
		// Pseudo
		bindBidirectional( txfPseudo, draft.pseudoProperty() );
		validator.addRuleNotBlank( txfPseudo );
		validator.addRuleMinLength( txfPseudo, 3 );
		validator.addRuleMaxLength( txfPseudo, 25 );
		validator.addRule(txfPseudo, "Ce pseudo est déjà utilisé", modelCompte::verifierUnicitePseudo  );
		
		// Mot de passe
		bindBidirectional( txfMotDePasse, draft.motDePasseProperty() );
		validator.addRuleNotBlank( txfMotDePasse );
		validator.addRuleMinLength( txfMotDePasse, 3 );
		validator.addRuleMaxLength( txfMotDePasse, 25 );
		
		// Adresse e-mail
		bindBidirectional( txfEmail, draft.emailProperty() );
		validator.addRuleNotBlank( txfEmail );
		validator.addRuleMaxLength( txfEmail, 100 );
		validator.addRuleEmail( txfEmail );
		
		// ListView des roles
		var binding = new BindingPairCheckList<>( Roles.getRoles(), draft.getRoles() );
    	binding.configureListView( lsvRoles, item -> Roles.getLibelle( item.getKey() ) );
    	
    	// Bouton Valider
    	btnValider.disableProperty().bind( validator.invalidProperty() );
	}
	
	
	@Override
	public void refresh() {
		modelCompte.refreshList();
		lsvComptes.requestFocus();
	}
	
	
	//-------
	// Actions
	//-------

	@FXML
	private void doAjouter() {
		lsvComptes.getSelectionModel().select(null);
		txfPseudo.requestFocus();
	}

	@FXML
	private void doSupprimer() {
		if ( managerGui.showDialogConfirm( "Confirmez-vous la suppresion ?" ) ) {
			modelCompte.deleteCurrent();
			refresh();
		}
	}
	
	@FXML
	private void doAnnuler() {
		if( modelCompte.getCurrent() == null ) {
			initDraft();
		}
		refresh();
	}
	
	@FXML
	private void doValider() {
		modelCompte.saveDraft();
		refresh();
	}


	//-------
	// Méthodes auxiliaires
	//-------
	
	private void initDraft() {
		if ( lsvComptes.getSelectionModel().getSelectedItem() == null ) {
			modelCompte.initDraft( Mode.NEW );
		} else {
			modelCompte.initDraft( Mode.EDIT );
		}
		validator.reset();
	}
	
	private void configurerBoutons() {
		var flagDisable = lsvComptes.getSelectionModel().getSelectedItem() == null;
		btnSupprimer.setDisable( flagDisable );
	}

}
