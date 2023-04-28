package contacts.view.memo;

import contacts.data.Memo;
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


public class ViewMemoList extends ControllerAbstract {
	
	
	//-------
	// Composants de la vue
	//-------

	@FXML
	private ListView<Memo>	lsvMemos;
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
	private ModelMemo		modelMemo;
	
	
	//-------
	// Initialisation du Controller
	//-------

	@FXML
	private void initialize() {

		// ListView
		lsvMemos.setItems( modelMemo.getList() );
		UtilFX.setCellFactory( lsvMemos, "titre" );
		bindBidirectional( lsvMemos, modelMemo.currentProperty(), modelMemo.flagRefreshingListProperty() );
		
		// Configuraiton des boutons
		lsvMemos.getSelectionModel().selectedItemProperty().addListener(obs -> {
			configurerBoutons();
		});
		configurerBoutons();
		
	}
	
	@Override
	public void refresh() {
		modelMemo.refreshList();
		lsvMemos.requestFocus();
	}

	
	//-------
	// Actions
	//-------
	
	@FXML
	private void doAjouter() {
		modelMemo.initDraft( Mode.NEW );
		managerGui.showView( ViewMemoForm.class );
	}

	@FXML
	private void doModifier() {
		modelMemo.initDraft( Mode.EDIT );
		managerGui.showView( ViewMemoForm.class );
	}

	@FXML
	private void doSupprimer() {
		if ( managerGui.showDialogConfirm( "Confirmez-vous la suppresion ?" ) ) {
			modelMemo.deleteCurrent();
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
				if ( lsvMemos.getSelectionModel().getSelectedIndex() == -1 ) {
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
		var flagDisable = lsvMemos.getSelectionModel().getSelectedItem() == null;
		btnModifier.setDisable(flagDisable);
		btnSupprimer.setDisable(flagDisable);
	}

}
