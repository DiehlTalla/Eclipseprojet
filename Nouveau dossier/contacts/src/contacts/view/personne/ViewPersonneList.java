package contacts.view.personne;

import contacts.data.Categorie;
import contacts.data.Personne;
import contacts.view.ManagerGui;
import jakarta.inject.Inject;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import jfox.javafx.util.UtilFX;
import jfox.javafx.view.ControllerAbstract;
import jfox.javafx.view.Mode;


public class ViewPersonneList extends ControllerAbstract  {
	
	
	//-------
	// Composants de la vue
	//-------
	
	@FXML
	private TableView<Personne>	tbvPersonnes;
	@FXML
	private TableColumn<Personne, Integer> colId;
	@FXML
	private TableColumn<Personne, String> colNom;
	@FXML
	private TableColumn<Personne, Categorie> colCategorie;
	@FXML
	private Button				btnModifier;
	@FXML
	private Button				btnSupprimer;
	
	
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

	@FXML
	private void initialize() {
		
		// TableView
		tbvPersonnes.setItems( modelPersonne.getList() );
		UtilFX.setValueFactory( colId, "id" );
		UtilFX.setValueFactory( colNom, c -> Bindings.concat( c.getValue().getNom(), " ", c.getValue().getPrenom() ) );
		UtilFX.setValueFactory( colCategorie, "categorie" );
		UtilFX.setCellFactory( colCategorie, "libelle" );
		bindBidirectional( tbvPersonnes, modelPersonne.currentProperty(), modelPersonne.flagRefreshingListProperty() );
		
		// Configuraiton des boutons
		tbvPersonnes.getSelectionModel().selectedItemProperty().addListener(obs -> {
			configurerBoutons();
		});
    	configurerBoutons();
	}

	
	@Override
	public void refresh() {
		modelPersonne.refreshList();
		tbvPersonnes.requestFocus();
	}
	
	
	//-------
	// Actions
	//-------
	
	@FXML
	private void doAjouter() {
		modelPersonne.initDraft( Mode.NEW );;
		managerGui.showView( ViewPersonneForm.class );
	}
	
	@FXML
	private void doModifier() {
		modelPersonne.initDraft( Mode.EDIT );;
		managerGui.showView( ViewPersonneForm.class );
	}
	
	@FXML
	private void doSupprimer() {
		if ( managerGui.showDialogConfirm("Etes-vous sûr de voulir supprimer cette personne ?" ) ) {
			modelPersonne.deleteCurrent();
			refresh();
		}
	}
	
	
	//-------
	// Gestion des évènements
	//-------

	// Clic sur la liste
	@FXML
	private void gererClicSurListe( MouseEvent event ) {
		if (event.getButton().equals(MouseButton.PRIMARY)) {
			if (event.getClickCount() == 2) {
				if ( tbvPersonnes.getSelectionModel().getSelectedIndex() == -1 ) {
					managerGui.showDialogError( "Aucun élément n'est sélectionné dans la liste.");
				} else {
					doModifier();
				}
			}
		}
	}

	
	//-------
	// Méthodes auxiliaires
	//-------
	
	private void configurerBoutons() {
		var flagDisable = tbvPersonnes.getSelectionModel().getSelectedItem() == null;
		btnModifier.setDisable(flagDisable);
		btnSupprimer.setDisable(flagDisable);
	}
	
}
