package Labyrinthe;

import jfox.javafx.view.ControllerAbstract;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class ViewHome extends ControllerAbstract {
	@Inject
	private ManagerGui managerGui;

	// Actions
	@FXML
	private void doQuitteer() {
		managerGui.exit();
	}

	@FXML
	private void doProbleme() {
		managerGui.showView(ViewProbleme.class);

	}

	@FXML
	ComboBox<Integer> cmbNiveau;

	@Inject
	ModelLabyrinthe modelLabyrinthe;

	@FXML
	private void initialize() {
		cmbNiveau.setItems(modelLabyrinthe.getListeNiveaux());
		bindBidirectional(cmbNiveau, modelLabyrinthe.niveauProperty());
	}

}
