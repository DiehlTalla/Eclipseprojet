package Labyrinthe;

import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import jfox.javafx.view.ControllerAbstract;

public class ViewSolution extends ControllerAbstract {
	
	@Inject
	private ManagerGui managerGui;

	@FXML
	private ImageView imvSolution;
	@Inject
	private ModelLabyrinthe modelLabyrinthe;

	@FXML
	private void doProbleme() {
		managerGui.showView(ViewProbleme.class);

	}
	@FXML
	private void doQuitteer() {
		managerGui.exit();
	}
	private void initialize() {
		bindBidirectional( imvSolution, modelLabyrinthe.imageProblemeProperty() );
		}

	
	

	
	

	
	
}

