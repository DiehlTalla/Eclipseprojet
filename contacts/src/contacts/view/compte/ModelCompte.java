package contacts.view.compte;


import contacts.commun.IMapper;
import contacts.dao.DaoCompte;
import contacts.data.Compte;
import jakarta.inject.Inject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jfox.javafx.util.UtilFX;
import jfox.javafx.view.Mode;


public class ModelCompte {
	
	
	//-------
	// Données observables 
	//-------
	
	private final ObservableList<Compte>	list 	= FXCollections.observableArrayList(); 
	
	private final BooleanProperty			flagRefreshingList = new SimpleBooleanProperty();
	
	private final Compte					draft 	= new Compte();
	
	private final ObjectProperty<Compte>	current	= new SimpleObjectProperty<>();
	
	
	//-------
	// Autres champs
	//-------
	
	private Mode		mode = Mode.NEW;

    @Inject
	private IMapper		mapper;
    @Inject
	private DaoCompte	daoCompte;
	
	
	//-------
	// Getters & Setters
	//-------
	
	public ObservableList<Compte> getList() {
		return list;
	}

	public BooleanProperty flagRefreshingListProperty() {
		return flagRefreshingList;
	}

	public Compte getDraft() {
		return draft;
	}

	public ObjectProperty<Compte> currentProperty() {
		return current;
	}

	public Compte getCurrent() {
		return current.get();
	}

	public void setCurrent(Compte item) {
		current.set(item);
	}
	
	public Mode getMode() {
		return mode;
	}
	
	
	//-------
	// Actions
	//-------
	
	public void refreshList() {
		// flagRefreshingList vaut true pendant la durée  
		// du traitement de mise à jour de la liste
		flagRefreshingList.set(true);
		list.setAll( daoCompte.listerTout() );
		flagRefreshingList.set(false);
 	}

	public void initDraft(Mode mode) {
		this.mode = mode;
		if( mode == Mode.NEW ) {
			mapper.update( draft, new Compte() );
		} else {
			setCurrent( daoCompte.retrouver( getCurrent().getId() ) );
			mapper.update( draft, getCurrent() );
		}
	}
	
	
	public boolean verifierUnicitePseudo( String pseudo) {
		return daoCompte.verifierUnicitePseudo( pseudo, draft.getId() );
	}

	
	public void saveDraft() {

		// Vérifie la validité des données
		
//		StringBuilder message = new StringBuilder();
//		
//		if ( ! daoCompte.verifierUnicitePseudo( draft.getPseudo(), draft.getId() ) ) {
//			message.append( "\nLe pseudo " + draft.getPseudo() + " est déjà utilisé." );
//		}
//		
//		if ( message.length() > 0 ) {
//			throw new ExceptionValidation( message.toString().substring(1) );
//		}
		
		// Enregistre les données dans la base
		
		if ( mode == Mode.NEW ) {
			// Insertion
			daoCompte.inserer( draft );
			// Actualise le courant
			setCurrent( mapper.update( new Compte(), draft ) );
		} else {
			// modficiation
			daoCompte.modifier( draft );
			// Actualise le courant
			mapper.update( getCurrent(), draft );
		}
	}
	
	
	public void deleteCurrent() {
		// Effectue la suppression
		daoCompte.supprimer( getCurrent().getId() );
		// Détermine le nouveau courant
		setCurrent( UtilFX.findNext( list, getCurrent() ) );
	}

}
