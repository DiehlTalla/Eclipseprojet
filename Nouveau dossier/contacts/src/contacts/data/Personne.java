package contacts.data;

import java.util.Objects;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class Personne {


	//-------
	// Données observables
	//-------
	
	private final ObjectProperty<Integer>	id			= new SimpleObjectProperty<>();
	private final StringProperty			nom	 		= new SimpleStringProperty();
	private final StringProperty			prenom		= new SimpleStringProperty();
	private final ObjectProperty<Categorie>	categorie	= new SimpleObjectProperty<>();
	private final ObservableList<Telephone>	telephones	= FXCollections.observableArrayList(
			t ->  new Observable[] { t.libelleProperty(), t.numeroProperty() } 
		);
	
	
	//-------
	// Constructeurs
	//-------
	
	public Personne() {
	}
	
	public Personne( int id, String nom, String prenom, Categorie categorie ) {
		setId(id);
		setNom(nom);
		setPrenom(prenom);
		setCategorie(categorie);
	}
	
	
	// Getters & setters

	public final ObjectProperty<Integer> idProperty() {
		return this.id;
	}

	public final Integer getId() {
		return this.idProperty().get();
	}

	public final void setId(final Integer id) {
		this.idProperty().set(id);
	}
	
	public final StringProperty nomProperty() {
		return this.nom;
	}
	
	public final java.lang.String getNom() {
		return this.nomProperty().get();
	}
	
	public final void setNom(final java.lang.String nom) {
		this.nomProperty().set(nom);
	}
	
	public final StringProperty prenomProperty() {
		return this.prenom;
	}
	
	public final java.lang.String getPrenom() {
		return this.prenomProperty().get();
	}
	
	public final void setPrenom(final java.lang.String prenom) {
		this.prenomProperty().set(prenom);
	}

	public final ObjectProperty<Categorie> categorieProperty() {
		return this.categorie;
	}

	public final contacts.data.Categorie getCategorie() {
		return this.categorieProperty().get();
	}

	public final void setCategorie(final contacts.data.Categorie categorie) {
		this.categorieProperty().set(categorie);
	}

	public ObservableList<Telephone> getTelephones() {
		return telephones;
	}
	
	
	//-------
	// hashCode() & equals()
	//-------

	@Override
	public int hashCode() {
		return Objects.hash(id.get() );
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Personne other = (Personne) obj;
		return Objects.equals(id.get(), other.id.get() );
	}
	
}
