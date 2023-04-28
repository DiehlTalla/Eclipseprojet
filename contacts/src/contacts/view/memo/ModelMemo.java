package contacts.view.memo;


import contacts.commun.IMapper;
import contacts.dao.DaoMemo;
import contacts.dao.DaoPersonne;
import contacts.data.Categorie;
import contacts.data.Memo;
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


public class ModelMemo  {
	
	
	//-------
	// Données observables 
	//-------
	
	private final ObservableList<Memo> list 	= FXCollections.observableArrayList(); 
	
	private final BooleanProperty 			flagRefreshingList = new SimpleBooleanProperty();
	
	private final Memo					draft 		= new Memo();
	
	private final ObjectProperty<Memo> current 	= new SimpleObjectProperty<>();

	
	//-------
	// Autres champs
	//-------
	
	private Mode			mode = Mode.NEW;

	@Inject
	private IMapper			mapper;
    @Inject
	private DaoMemo	daoMemo;
    @Inject
    private DaoPersonne		daoPersonne;
	private ModelCategorie modelcategorie;
	
	//-------
	// Getters & Setters
	//-------
	
	public ObservableList<Memo> getList() {
		return list;
	}

	public BooleanProperty flagRefreshingListProperty() {
		return flagRefreshingList;
	}
	
	public Memo getDraft() {
		return draft;
	}

	public ObjectProperty<Memo> currentProperty() {
		return current;
	}

	public Memo getCurrent() {
		return current.get();
	}

	public void setCurrent(Memo item) {
		current.set(item);
	}
	
	public Mode getMode() {
		return mode;
	}
	public ObservableList<Categorie> getCategories() {
		return modelcategorie.getList();
		}
		
	
	//-------
	// Actions
	//-------
	
	public void refreshList() {
		// flagRefreshingList vaut true pendant la durée  
		// du traitement de mise à jour de la liste
		flagRefreshingList.set(true);
		list.setAll( daoMemo.listerTout() );
		flagRefreshingList.set(false);
 	}

	public void initDraft(Mode mode) {
		refreshList();
		this.mode = mode;
		if( mode == Mode.NEW ) {
			mapper.update( draft, new Memo() );
			draft.seFlagUrgent( false );
		} else {
			setCurrent( daoMemo.retrouver( getCurrent().getId() ) );
			mapper.update( draft, getCurrent() );
		}
	}
	
	
	public void saveDraft() {

		// Enregistre les données dans la base
		
		if ( mode == Mode.NEW ) {
			// Insertion
			daoMemo.inserer( draft );
			// Actualise le courant
			setCurrent( mapper.update( new Memo(), draft ) );
		} else {
			// modficiation
			daoMemo.modifier( draft );
			// Actualise le courant
			mapper.update( getCurrent(), draft );
		}
	}
	
	
	public void deleteCurrent() {
		
		// Vérifie l'absence de personnes rattachées à la catégorie
		if ( daoPersonne.compterPourCategorie( getCurrent().getId() ) != 0 ) {
			throw new ExceptionValidation( "Suppression impossible.\nDes personnes sont rattachées à cette catégorie." ) ;
		}
		
		// Effectue la suppression
		daoMemo.supprimer( getCurrent().getId() );
		// Détermine le nouveau courant
		setCurrent( UtilFX.findNext( list, getCurrent() ) );
	}
	
}
