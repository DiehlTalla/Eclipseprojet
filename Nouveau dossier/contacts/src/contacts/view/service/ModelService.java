package contacts.view.service;

import java.time.LocalDate;

import contacts.data.Personne;
import contacts.data.Service;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ModelService {

	private final ObjectProperty<Service> current = new SimpleObjectProperty<>();

	void initDraft() {
		final BooleanProperty flagsiege = new SimpleBooleanProperty(false);
		final int annee = LocalDate.now().getYear();
		final ObservableList<Personne> list = FXCollections.observableArrayList();

	}

}
