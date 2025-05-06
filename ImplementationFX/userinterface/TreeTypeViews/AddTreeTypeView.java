package userinterface.TreeTypeViews;

import impresario.IModel;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import userinterface.MessageView;
import userinterface.View;

import java.util.Properties;

public class AddTreeTypeView extends View {
    private TextField typeDescriptionField;
    private TextField costField;
    private TextField barcodePrefixField;

    private Button submitButton;
    private Button cancelButton;

    private MessageView statusLog;

    public AddTreeTypeView(IModel model) {
        super(model, "AddTreeTypeView");

        VBox container = new VBox(10);
        container.setPadding(new Insets(15, 5, 5, 5));

        container.getChildren().add(createTitle());
        container.getChildren().add(createFormContents());
        container.getChildren().add(createButtonPanel());
        container.getChildren().add(createStatusLog(""));

        getChildren().add(container);

        typeDescriptionField.requestFocus();

        myModel.subscribe("TransactionStatusMessage", this);
    }

    private Node createTitle() {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER);

        Text titleText = new Text("Adding A New Tree Type");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        container.getChildren().add(titleText);

        return container;
    }

    private Node createFormContents() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label typeDescLabel = new Label("Type Description:");
        grid.add(typeDescLabel, 0, 0);

        typeDescriptionField = new TextField();
        typeDescriptionField.setOnAction(e -> processAction(e));
        grid.add(typeDescriptionField, 1, 0);

        Label costLabel = new Label("Cost:");
        grid.add(costLabel, 0, 1);

        costField = new TextField();
        costField.setOnAction(e -> processAction(e));
        grid.add(costField, 1, 1);

        Label barcodePrefixLabel = new Label("Barcode Prefix:");
        grid.add(barcodePrefixLabel, 0, 2);

        barcodePrefixField = new TextField();
        barcodePrefixField.setOnAction(e -> processAction(e));
        grid.add(barcodePrefixField, 1, 2);

        return grid;
    }

    private HBox createButtonPanel() {
        HBox btnContainer = new HBox(15);
        btnContainer.setAlignment(Pos.CENTER);

        submitButton = new Button("Submit");
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                processAction(e);
            }
        });

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                processAction(e);
            }
        });

        btnContainer.getChildren().add(submitButton);
        btnContainer.getChildren().add(cancelButton);

        return btnContainer;
    }

    private MessageView createStatusLog(String initialMessage) {
        statusLog = new MessageView(initialMessage);
        return statusLog;
    }

    private void processAction(Event evt) {
        clearErrorMessage();
        if(evt.getSource() == submitButton){
            processTransaction();
        }else if(evt.getSource() == cancelButton){
            cancelTransaction();
        }
    }

    private void cancelTransaction() {
        clearErrorMessage();
        clearFields();
        myModel.stateChangeRequest("CancelAddTreeTypeTransaction", null);
    }

    private void processTransaction() {
        clearErrorMessage();

        if (!validateFormData())
            return;

        Properties treeTypeData = new Properties();
        treeTypeData.setProperty("TypeDescription", typeDescriptionField.getText());
        treeTypeData.setProperty("Cost", costField.getText());
        treeTypeData.setProperty("BarcodePrefix", barcodePrefixField.getText());

        myModel.stateChangeRequest("ProcessTreeTypeTransaction", treeTypeData);

        clearFields();
    }

    private boolean validateFormData() {
        if (typeDescriptionField.getText().isEmpty()) {
            displayErrorMessage("Type Description is required.");
            typeDescriptionField.requestFocus();
            return false;
        }

        if (costField.getText().isEmpty()) {
            displayErrorMessage("Cost is required.");
            costField.requestFocus();
            return false;
        }

        try {
            Double.parseDouble(costField.getText());
        } catch (NumberFormatException e) {
            displayErrorMessage("Cost must be a valid number.");
            costField.requestFocus();
            return false;
        }

        if (barcodePrefixField.getText().isEmpty()) {
            displayErrorMessage("Barcode Prefix is required.");
            barcodePrefixField.requestFocus();
            return false;
        }

        return true;
    }

    public void displayErrorMessage(String message) {
        statusLog.displayErrorMessage(message);
    }

    public void clearErrorMessage() {
        statusLog.clearErrorMessage();
    }

    @Override
    public Scene createScene() {
        return new Scene(this, 500, 400);
    }

    @Override
    public void updateState(String key, Object value) {
        if ("TransactionStatusMessage".equals(key)) {
            String msg = (String) value;
            if (msg.toLowerCase().startsWith("error")) {
                displayErrorMessage(msg);
            } else {
                clearFields();
                statusLog.displayMessage(msg);
            }
        }
    }

    private void clearFields() {
        typeDescriptionField.clear();
        costField.clear();
        barcodePrefixField.clear();
        typeDescriptionField.requestFocus();
    }
}
