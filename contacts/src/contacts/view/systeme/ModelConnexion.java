package contacts.view.systeme;

import contacts.dao.DaoCompte;
import contacts.data.Compte;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import jfox.exception.ExceptionValidation;


public class ModelConnexion {
	
	
	//-------
	// Données observables 
	//-------
	
	// Vue connexion
	private final Compte	draft = new Compte();

	// Compte connecté
	private final ObjectProperty<Compte>	compteActif = new SimpleObjectProperty<>();

	
	//-------
	// Autres champs
	//-------

	@Inject
	private DaoCompte	daoCompte;
	

	//-------
	// Getters 
	//-------
	
	public Compte getDraft() {
		return draft;
	}
	
	public ObjectProperty<Compte> compteActifProperty() {
		return compteActif;
	}
	
	public Compte getCompteActif() {
		return compteActif.get();
	}
	
	
	//-------
	// Initialisation
	//-------
	
	@PostConstruct
	public void init() {
		draft.setPseudo( "geek" );
		draft.setMotDePasse( "geek" );
	}
	
	
	//-------
	// Actions
	//-------

	public void ouvrirSessionUtilisateur() {

		Compte compte = daoCompte.validerAuthentification(
					draft.pseudoProperty().getValue(), draft.motDePasseProperty().getValue() );
		
		if( compte == null ) {
			throw new ExceptionValidation( "Pseudo ou mot de passe invalide." );
		} else {
			Platform.runLater( () -> compteActif.setValue( compte ) );
		}
	}
	

	public void fermerSessionUtilisateur() {
		compteActif.setValue( null );
	}

}
