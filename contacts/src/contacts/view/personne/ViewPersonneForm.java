package contacts.view.personne;

import contacts.data.Categorie;
import contacts.data.Telephone;
import contacts.view.ManagerGui;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import jfox.javafx.control.EditingCell;
import jfox.javafx.util.UtilFX;
import jfox.javafx.util.converter.ConverterInteger;
import jfox.javafx.view.ControllerAbstract;


public class ViewPersonneForm extends ControllerAbstract  {
	
	
	//-------
	// Composants de la vue
	//-------
	
	@FXML
	private Label				labId;
	@FXML
	private TextField			txfNom;
	@FXML	
	private TextField			txfPrenom;
    @FXML
    private ComboBox<Categorie>	cmbCategorie;
	@FXML
	private TableView<Telephone>	tbvTelphones;
	@FXML
	private TableColumn<Telephone, Integer> colId;
	@FXML
	private TableColumn<Telephone, String> colLibelle;
	@FXML
	private TableColumn<Telephone, String> colNumero;
	@FXML
	private Button				btnTelephoneSupprimer;
	@FXML
	private Button				btnValider;

	
	//-------
	// Autres champs
	//-------

	@Inject
	private ManagerGui			managerGui;
	@Inject
	private ModelPersonne		modelPersonne;
    
	
	//-------
	// Initialisations
	//-------
	
	public void initialize() {

		var draft = modelPersonne.getDraft();
		
		// Identifiant
		bind( labId, draft.idProperty(), new ConverterInteger() );
		
		// Nom
		bindBidirectional( txfNom, draft.nomProperty() );
		validator.addRuleNotBlank( txfNom ); 
		validator.addRuleMaxLength( txfNom, 25 ); 
		
		// Prénom
		bindBidirectional( txfPrenom, draft.prenomProperty() );
		validator.addRuleNotBlank( txfPrenom ); 
		validator.addRuleMaxLength( txfPrenom, 25 ); 
        
		// Combo box
		bindBidirectional( cmbCategorie, draft.categorieProperty() );
		cmbCategorie.setItems( modelPersonne.getCategories() );
        UtilFX.setCellFactory(cmbCategorie, "libelle" );
		validator.addRuleNotNull( cmbCategorie ); 
		
		// Liste des téléphones
		tbvTelphones.setItems(  modelPersonne.getDraft().getTelephones() );
		
		UtilFX.setValueFactory( colId, "id" );
		UtilFX.setValueFactory( colLibelle, "libelle" );
		UtilFX.setValueFactory( colNumero, "numero" );

		colLibelle.setCellFactory(  p -> new EditingCell<>() );
		colNumero.setCellFactory(  p -> new EditingCell<>() );

		tbvTelphones.getSelectionModel().selectedItemProperty().addListener( obs -> {
			configurerBoutonsTelephone();
		});
		configurerBoutonsTelephone();
		
		// Bouton Valider
		btnValider.disableProperty().bind( validator.invalidProperty() );
	
	}
	
	@Override
	public void refresh() {
		txfNom.requestFocus();
	}
	
	
	//-------
	// Actions
	//-------
	
	@FXML
	private void doValider() {
		modelPersonne.saveDraft();
		managerGui.showView( ViewPersonneList.class );
	}
	
	@FXML
	private void doAnnuler() {
		managerGui.showView( ViewPersonneList.class );
	}
	
	@FXML
	private void doTelephoneAjouter() {
		modelPersonne.getDraft().getTelephones().add( new Telephone() );
		tbvTelphones.requestFocus();
		var index = tbvTelphones.getItems().size() - 1;
		tbvTelphones.getSelectionModel().select( index );
		tbvTelphones.scrollTo( index );
	}
	
	
	@FXML
	private void doiTelephoneSupprimer() {
		var telephone = tbvTelphones.getSelectionModel().getSelectedItem();
		modelPersonne.getDraft().getTelephones().remove( telephone );
	}

	
	//-------
	// Méthodes auxiliaires
	//-------
	
	private void configurerBoutonsTelephone() {
		var flagDisable = tbvTelphones.getSelectionModel().getSelectedItem() == null;
		btnTelephoneSupprimer.setDisable(flagDisable);
	}
    
}
