package contacts.view.systeme;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;




public class ModelInfo {
	
	
	//-------
	// Données observables 
	//-------
	
	private final ObjectProperty<String>	titre	= new SimpleObjectProperty<>();
	private final ObjectProperty<String>	message	= new SimpleObjectProperty<>();
	

	//-------
	// Getters & Setters
	//-------
	
	public Property<String> titreProperty() {
		return titre;
	}
	
	public void setTitre( String titre ) {
		this.titre.set(titre);
	}
	
	public Property<String> messageProperty() {
		return message;
	}
	
	public void setMessage( String message ) {
		this.message.set(message);
	}
	
}
