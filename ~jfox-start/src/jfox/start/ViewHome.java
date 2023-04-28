package jfox.start;

import javafx.fxml.FXML;
import jfox.javafx.view.ControllerAbstract;

public class ViewHome extends ControllerAbstract {

	//-------
	// Actions
	//-------

	@FXML
	private void doQuitteer() {
		System.out.println( "Quitter" );
	}

	@FXML
	private void doProbleme() {
		System.out.println( "Problème" );
	}

}
