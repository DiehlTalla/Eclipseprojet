package Labyrinthe;

import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import jfox.javafx.view.ControllerAbstract;

public class ViewProbleme extends ControllerAbstract {
	@Inject
	private ManagerGui managerGui;

	@FXML
	private ImageView imvProbleme;
	@Inject
	private ModelLabyrinthe modelLabyrinthe;
	
	@FXML
	private void doAcceuil() {
		managerGui.showView(ViewHome.class);
	}

	@FXML
	private void doSolution() {
		managerGui.showView(ViewSolution.class);

	}
	private void initialize() {
	bindBidirectional( imvProbleme, modelLabyrinthe.imageProblemeProperty() );
	}

}
