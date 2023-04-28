package contacts.view.categorie;

import contacts.data.Categorie;
import contacts.view.ManagerGui;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import jfox.javafx.util.UtilFX;
import jfox.javafx.view.ControllerAbstract;
import jfox.javafx.view.Mode;


public class ViewCategorieList extends ControllerAbstract {
	
	
	//-------
	// Composants de la vue
	//-------

	@FXML
	private ListView<Categorie>	lsvCategories;
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
	private ModelCategorie		modelCategorie;
	
	
	//-------
	// Initialisation du Controller
	//-------

	@FXML
	private void initialize() {

		// ListView
		lsvCategories.setItems( modelCategorie.getList() );
		UtilFX.setCellFactory( lsvCategories, "libelle" );
		bindBidirectional( lsvCategories, modelCategorie.currentProperty(), modelCategorie.flagRefreshingListProperty() );
		
		// Configuraiton des boutons
		lsvCategories.getSelectionModel().selectedItemProperty().addListener(obs -> {
			configurerBoutons();
		});
		configurerBoutons();
		
	}
	
	@Override
	public void refresh() {
		modelCategorie.refreshList();
		lsvCategories.requestFocus();
	}

	
	//-------
	// Actions
	//-------
	
	@FXML
	private void doAjouter() {
		modelCategorie.initDraft( Mode.NEW );
		managerGui.showView( ViewCategorieForm.class );
	}

	@FXML
	private void doModifier() {
		modelCategorie.initDraft( Mode.EDIT );
		managerGui.showView( ViewCategorieForm.class );
	}

	@FXML
	private void doSupprimer() {
		if ( managerGui.showDialogConfirm( "Confirmez-vous la suppresion ?" ) ) {
			modelCategorie.deleteCurrent();
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
				if ( lsvCategories.getSelectionModel().getSelectedIndex() == -1 ) {
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
		var flagDisable = lsvCategories.getSelectionModel().getSelectedItem() == null;
		btnModifier.setDisable(flagDisable);
		btnSupprimer.setDisable(flagDisable);
	}

}
