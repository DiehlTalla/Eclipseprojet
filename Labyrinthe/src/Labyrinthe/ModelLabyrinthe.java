package Labyrinthe;

import jakarta.annotation.PostConstruct;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import jfox.exception.ExceptionValidation;

public class ModelLabyrinthe {
	// Données observables
	private final ObservableList<Integer> listeNiveaux = FXCollections.observableArrayList();
	private final ObjectProperty<Integer> niveau = new SimpleObjectProperty<>();

	public ObservableList<Integer> getListeNiveaux() {
		return listeNiveaux;
	}

	public ObjectProperty<Integer> niveauProperty() {
		return niveau;
	}

	@PostConstruct
	public void init() {
		listeNiveaux.addAll(1, 2, 3, 4, 5);
		niveau.addListener(obs -> chargerImages());
		niveau.set(1);
	}

	private final ObjectProperty<Image> imageProbleme = new SimpleObjectProperty<>();

	public ObjectProperty<Image> imageProblemeProperty() {
		return imageProbleme;
	}
	private final ObjectProperty<Image> imageSolution = new SimpleObjectProperty<>();

	public ObjectProperty<Image> imageSolutionProperty() {
		return imageSolution;
	}


	public void chargerImages() {
		String chemin;
		chemin = "images/probleme" + niveau.getValue() + ".png";
		imageProbleme.set(new Image(getClass().getResource(chemin).toExternalForm()));
		String chemin2;
		chemin2 = "images/solution" + niveau.getValue() + ".png";
		imageProbleme.set(new Image(getClass().getResource(chemin2).toExternalForm()));
	}

}
