package jfox.javafx.control;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;

public class  EditingCell<T> extends TableCell<T, String> {
	 
    private TextField textField;
    private Property<String> boundToCurrently = null;

    public EditingCell() {
      String strCss;
      // Padding in Text field cell is not wanted - we want the Textfield itself to "be"
      // The cell.  Though, this is aesthetic only.  to each his own.  comment out
      // to revert back.  
      strCss = "-fx-padding: 0;";


      this.setStyle(strCss);

      textField = new TextField();

      // 
      // Default style pulled from caspian.css. Used to play around with the inset background colors
      // ---trying to produce a text box without borders
      strCss = "" +
                //"-fx-background-color: -fx-shadow-highlight-color, -fx-text-box-border, -fx-control-inner-background;" +
                "-fx-background-color: -fx-control-inner-background;" +
                //"-fx-background-insets: 0, 1, 2;" +
                "-fx-background-insets: 0;" +
                //"-fx-background-radius: 3, 2, 2;" +
                "-fx-background-radius: 0;" +
                "-fx-padding: 3 5 3 5;" +   /*Play with this value to center the text depending on cell height??*/
                //"-fx-padding: 0 0 0 0;" +
                "-fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);" +
                "-fx-cursor: text;" +
                "";
      // Focused and hover states should be set in the CSS.  This is just a test
      // to see what happens when we set the style in code
      textField.focusedProperty().addListener(new ChangeListener<Boolean>() {

        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            TextField tf = (TextField)getGraphic();
            String strStyleGotFocus = "-fx-background-color: purple, -fx-text-box-border, -fx-control-inner-background;" +
                        "-fx-background-insets: -0.4, 1, 2;" +
                        "-fx-background-radius: 3.4, 2, 2;";
            String strStyleLostFocus = //"-fx-background-color: -fx-shadow-highlight-color, -fx-text-box-border, -fx-control-inner-background;" +
                               "-fx-background-color: -fx-control-inner-background;" +
                             //"-fx-background-insets: 0, 1, 2;" +
                               "-fx-background-insets: 0;" +
                             //"-fx-background-radius: 3, 2, 2;" +
                               "-fx-background-radius: 0;" +
                               "-fx-padding: 3 5 3 5;" +   /**/
                               //"-fx-padding: 0 0 0 0;" +
                               "-fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);" +
                               "-fx-cursor: text;" +
                               "";
            if(newValue.booleanValue()) {
                tf.setStyle(strStyleGotFocus);
              	getTableView().getSelectionModel().select( getTableRow().getIndex() );
            } else {
                tf.setStyle(strStyleLostFocus);             
            }
        }
      });
      textField.hoverProperty().addListener(new ChangeListener<Boolean>() {

        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            TextField tf = (TextField)getGraphic();
            String strStyleGotHover = "-fx-background-color: derive(purple,90%), -fx-text-box-border, derive(-fx-control-inner-background, 10%);" +
                        "-fx-background-insets: 1, 2.8, 3.8;" +
                        "-fx-background-radius: 3.4, 2, 2;";
            String strStyleLostHover = //"-fx-background-color: -fx-shadow-highlight-color, -fx-text-box-border, -fx-control-inner-background;" +
                               "-fx-background-color: -fx-control-inner-background;" +
                             //"-fx-background-insets: 0, 1, 2;" +
                               "-fx-background-insets: 0;" +
                             //"-fx-background-radius: 3, 2, 2;" +
                               "-fx-background-radius: 0;" +
                               "-fx-padding: 3 5 3 5;" +   /**/
                               //"-fx-padding: 0 0 0 0;" +
                               "-fx-prompt-text-fill: derive(-fx-control-inner-background,-30%);" +
                               "-fx-cursor: text;" +
                               "";
            String strStyleHasFocus = "-fx-background-color: purple, -fx-text-box-border, -fx-control-inner-background;" +
                        "-fx-background-insets: -0.4, 1, 2;" +
                        "-fx-background-radius: 3.4, 2, 2;";
            if(newValue.booleanValue()) {
              tf.setStyle(strStyleGotHover);
            }
            else {
              if(!tf.focusedProperty().get()) {
                tf.setStyle(strStyleLostHover);
              }
              else {
                tf.setStyle(strStyleHasFocus);
              }
            }

        }
      });
      textField.setStyle(strCss);
      this.setGraphic(textField);
    }

    @Override
    protected void updateItem(String item, boolean empty) {
      super.updateItem(item, empty);        
      if(!empty) {
        // Show the Text Field
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        // Retrieve the actual String Property that should be bound to the TextField
        // If the TextField is currently bound to a different StringProperty
        // Unbind the old property and rebind to the new one
        ObservableValue<String> ov = getTableColumn().getCellObservableValue(getIndex());
       Property<String> sp = (Property<String>)ov;

        if(this.boundToCurrently==null) {
            this.boundToCurrently = sp;
            this.textField.textProperty().bindBidirectional(sp);
        }
        else {
            if(this.boundToCurrently != sp) {
              this.textField.textProperty().unbindBidirectional(this.boundToCurrently);
              this.boundToCurrently = sp;
              this.textField.textProperty().bindBidirectional(this.boundToCurrently);
            }
        }
      }
      else {
        this.setContentDisplay(ContentDisplay.TEXT_ONLY);
      }
    }

}

