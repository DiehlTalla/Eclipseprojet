package contacts.view.personne;

import contacts.commun.IMapper;
import contacts.dao.DaoPersonne;
import contacts.data.Categorie;
import contacts.data.Personne;
import contacts.view.categorie.ModelCategorie;
import jakarta.inject.Inject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jfox.exception.ExceptionValidation;
import jfox.javafx.util.UtilFX;
import jfox.javafx.view.Mode;


public class ModelPersonne {
	
	
	//-------
	// Données observables 
	//-------
	
	private final ObservableList<Personne> list		= FXCollections.observableArrayList();
	
	private final BooleanProperty 			flagRefreshingList = new SimpleBooleanProperty();
	
	private final Personne					draft 		= new Personne();
	
	private final ObjectProperty<Personne> 	current 	= new SimpleObjectProperty<>();
	
	
	//-------
	// Autres champs
	//-------
	
	private Mode			mode = Mode.NEW;

	@Inject
	private IMapper			mapper;
    @Inject
	private ModelCategorie	modelCategorie;
    @Inject
	private DaoPersonne		daoPersonne;

    
	//-------
	// Getters & Setters
	//-------
	
	public ObservableList<Personne> getList() {
		return list;
	}

	public BooleanProperty flagRefreshingListProperty() {
		return flagRefreshingList;
	}
	
	public Personne getDraft() {
		return draft;
	}

	public ObjectProperty<Personne> currentProperty() {
		return current;
	}

	public Personne getCurrent() {
		return current.get();
	}

	public void setCurrent(Personne item) {
		current.set(item);
	}
	
	public Mode getMode() {
		return mode;
	}
	
	public ObservableList<Categorie> getCategories() {
		return modelCategorie.getList();
	}

	
	//-------
	// Actions
	//-------
	
	public void refreshList() {
		// flagRefreshingList vaut true pendant la durée  
		// du traitement de mise à jour de la liste
		flagRefreshingList.set(true);
		list.setAll( daoPersonne.listerTout() );
		flagRefreshingList.set(false);
 	}

	public void initDraft(Mode mode) {
		this.mode = mode;
		modelCategorie.refreshList();
		if( mode == Mode.NEW ) {
			mapper.update( draft, new Personne() );
		} else {
			setCurrent( daoPersonne.retrouver( getCurrent().getId() ) );
			mapper.update( draft, getCurrent() );
		}
	}

	
	public void saveDraft() {
		
		// Vérifie la validité des données
		
		StringBuilder message = new StringBuilder();

		for( int i=draft.getTelephones().size()-1; i >= 0; --i ) {
			var t = draft.getTelephones().get(i);
			if ( t.getId() == null && t.getLibelle() == null && t.getNumero() == null ) {
				draft.getTelephones().remove(i);
			}
		}
		for( var t : draft.getTelephones() ) {
			if ( t.getLibelle() == null || t.getLibelle().isEmpty() )  {
				message.append( "\nLe libellé d'un téléphone ne doit pas être vide" );
			}
			if ( t.getNumero() == null || t.getNumero().isEmpty() )  {
				message.append( "\nLe numéro d'un téléphone ne doit pas être vide" );
			}
		}
		
		if ( message.length() > 0 ) {
			throw new ExceptionValidation( message.toString().substring(1) );
		}
		
		// Enregistre les données dans la base
		
		if ( mode == Mode.NEW ) {
			// Insertion
			daoPersonne.inserer( draft );
			// Actualise le courant
			setCurrent( mapper.update( new Personne(), draft ) );
		} else {
			// modficiation
			daoPersonne.modifier( draft );
			// Actualise le courant
			mapper.update( getCurrent(), draft );
		}
	}
	

	public void deleteCurrent() {
		// Effectue la suppression
		daoPersonne.supprimer( getCurrent().getId() );
		// Détermine le nouveau courant
		setCurrent( UtilFX.findNext( list, getCurrent() ) );
	}
	
}