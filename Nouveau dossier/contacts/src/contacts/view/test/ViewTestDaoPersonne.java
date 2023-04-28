package contacts.view.test;

import contacts.dao.DaoPersonne;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import jfox.javafx.util.UtilFX;
import jfox.javafx.view.ControllerAbstract;


public class ViewTestDaoPersonne extends ControllerAbstract {
	
	
	//-------
	// Composants de la vue
	//-------
	
	@FXML
	private TextArea		textArea;
	
	
	//-------
	// Autres champs
	//-------
	
	@Inject
	private DaoPersonne	dao;
	
	private final int		id = 1;	
	
	
	//-------
	// Initialisations
	//-------
	
	@Override
	public void refresh() {
		textArea.setText(null);
	}
	
	
	//-------
	// Actions
	//-------
	
	@FXML
	private void doLister() {
		textArea.clear();
		for (Object item : dao.listerTout() ) {
			textArea.appendText( UtilFX.objectToString( item ) );
			textArea.appendText( "\n"  );
		}
		textArea.appendText( "\n"  );
		textArea.appendText(  "Test n°1 OK \n");;
	}
	
	@FXML
	private void doRetrouver() {
		textArea.clear();
		var item = dao.retrouver( id );
		textArea.appendText( UtilFX.objectToString( item ) );
		textArea.appendText( "\n\n"  );
		textArea.appendText(  "Test n°2 OK \n");;
	}
	
	@FXML
	private void doModifier() {
		textArea.clear();
		var item = dao.retrouver( id );
		dao.modifier( item );
		textArea.appendText( "\n\n"  );
		textArea.appendText(  "Test n°3 OK \n");;
	}
	
	@FXML
	private void doInsererSupprimer() {
		textArea.clear();
		var item = dao.retrouver( id );
		dao.inserer( item );
		dao.supprimer( item.getId() );
		textArea.appendText( UtilFX.objectToString( item ) );
		textArea.appendText( "\n\n"  );
		textArea.appendText(  "Test n°4 OK \n");;
	}
	
}
