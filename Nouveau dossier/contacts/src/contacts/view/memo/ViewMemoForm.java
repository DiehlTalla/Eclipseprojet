package contacts.view.memo;

import java.util.Objects;

import contacts.data.Categorie;
import contacts.view.ManagerGui;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import jfox.javafx.util.UtilFX;
import jfox.javafx.util.converter.ConverterInteger;
import jfox.javafx.view.ControllerAbstract;

public class ViewMemoForm extends ControllerAbstract {

	// -------
	// Composants de la vue
	// -------

	@FXML
	private Label labId;
	@FXML
	private TextField txfTitre;
	@FXML
	private TextArea txaDescription;
	@FXML
	private Button btnValider;

	@FXML
	private CheckBox cbkUrgent;
	private ComboBox<Categorie> cmbCategorie;

	// -------
	// Autres champs
	// -------

	@Inject
	private ManagerGui managerGui;
	@Inject
	private ModelMemo modelMemo;

	// -------
	// Initialisation du Controller
	// -------

	@FXML
	private void initialize() {

		var draft = modelMemo.getDraft();
		cmbCategorie.setItems(modelMemo.getCategories());
		bindBidirectional(cmbCategorie, draft.categorieProperty());
		UtilFX.setCellFactory( cmbCategorie, "libelle" );

		// Id
		bind(labId, draft.idProperty(), new ConverterInteger());

		// Libellé
		bindBidirectional(txfTitre, draft.titreProperty());
		validator.addRuleNotBlank(txfTitre);
		validator.addRuleMaxLength(txfTitre, 50);

		// Date début

		// Bouton VAlider
		btnValider.disableProperty().bind(validator.invalidProperty());
	}

	@Override
	public void refresh() {
		txfTitre.requestFocus();
	}

	// -------
	// Actions
	// -------

	@FXML
	private void doAnnuler() {
		managerGui.showView(ViewMemoList.class);
	}

	@FXML
	private void doValider() {
		modelMemo.saveDraft();
		managerGui.showView(ViewMemoList.class);
	}

	@Override
	public int hashCode() {
		return Objects.hash(labId.getId());
	}

	@FXML
	private void doCategorieSupprimer() {
		cmbCategorie.setValue(null);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ViewMemoForm other = (ViewMemoForm) obj;
		return Objects.equals(labId.getId(), other.labId.getId());
	}

}
