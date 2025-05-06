package userinterface.TreeTypeViews;

import impresario.IModel;
import javafx.geometry.Insets;
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

public class ModifyTreeTypeView extends View {

    private TextField typeDescriptionField;
    private TextField costField;
    private TextField barcodePrefixField;
    private TextField idField;

    private Button submitButton, cancelButton;
    private MessageView statusLog;

    private Properties treeTypeData;

    public ModifyTreeTypeView(IModel model, Properties props) {
        super(model, "ModifyTreeTypeView");
        this.treeTypeData = props;

        VBox container = new VBox(10);
        container.setPadding(new Insets(20));
        container.getChildren().addAll(createTitle(), createFormContent(), createStatusLog(""));

        getChildren().add(container);
    }

    private Node createTitle() {
        Text titleText = new Text("Modify Tree Type Information");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        return titleText;
    }

    private VBox createFormContent() {
        VBox form = new VBox(10);
        GridPane grid = new GridPane();
        grid.setAlignment(javafx.geometry.Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // ID (non-editable)
        Label idLabel = new Label("ID:");
        grid.add(idLabel, 0, 0);

        idField = new TextField(treeTypeData.getProperty("ID", ""));
        idField.setEditable(false);
        grid.add(idField, 1, 0);

        // Type Description
        Label typeDescriptionLabel = new Label("Type Description:");
        grid.add(typeDescriptionLabel, 0, 1);

        typeDescriptionField = new TextField(treeTypeData.getProperty("TypeDescription", ""));
        grid.add(typeDescriptionField, 1, 1);

        // Cost
        Label costLabel = new Label("Cost ($):");
        grid.add(costLabel, 0, 2);

        costField = new TextField(treeTypeData.getProperty("Cost", ""));
        grid.add(costField, 1, 2);

        // Barcode Prefix
        Label barcodePrefixLabel = new Label("Barcode Prefix:");
        grid.add(barcodePrefixLabel, 0, 3);

        barcodePrefixField = new TextField(treeTypeData.getProperty("BarcodePrefix", ""));
        grid.add(barcodePrefixField, 1, 3);

        submitButton = new Button("Submit Changes");
        submitButton.setOnAction(e -> processSubmission());

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> cancelAction());

        HBox buttonBox = new HBox(20, submitButton, cancelButton);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        form.getChildren().addAll(grid, buttonBox);
        return form;
    }

    protected MessageView createStatusLog(String initialMessage) {
        statusLog = new MessageView(initialMessage);
        return statusLog;
    }

    private void processSubmission() {
        clearErrorMessage();

        String typeDescription = typeDescriptionField.getText().trim();
        String cost = costField.getText().trim();
        String barcodePrefix = barcodePrefixField.getText().trim();

        // Validate fields
        if (typeDescription.isEmpty()) {
            displayErrorMessage("Type Description is required");
            return;
        }

        if (cost.isEmpty()) {
            displayErrorMessage("Cost is required");
            return;
        }

        // Validate cost format
        try {
            double costValue = Double.parseDouble(cost);
            if (costValue < 0) {
                displayErrorMessage("Cost must be a positive number");
                return;
            }
        } catch (NumberFormatException e) {
            displayErrorMessage("Cost must be a valid number");
            return;
        }

        if (barcodePrefix.isEmpty()) {
            displayErrorMessage("Barcode Prefix is required");
            return;
        }

        if (typeDescription.length() > 200) {
            displayErrorMessage("Description must be 200 characters or fewer");
            return;
        }

        if (barcodePrefix.length() > 20) {
            displayErrorMessage("Barcode Prefix must be 20 characters or fewer");
            return;
        }

        // Create properties object with the updated data
        Properties props = new Properties();
        props.setProperty("ID", idField.getText());
        props.setProperty("TypeDescription", typeDescription);
        props.setProperty("Cost", cost);
        props.setProperty("BarcodePrefix", barcodePrefix);

        // Send to model
        myModel.stateChangeRequest("ModifyTreeTypeData", props);
    }

    private void cancelAction() {
        myModel.stateChangeRequest("CancelModifyTreeType", null);
    }

    private void clearForm() {
        typeDescriptionField.clear();
        costField.clear();
        barcodePrefixField.clear();
        clearErrorMessage();
    }

    public void displayErrorMessage(String message) {
        statusLog.displayErrorMessage(message);
    }

    public void displayMessage(String message) {
        statusLog.displayMessage(message);
    }

    public void clearErrorMessage() {
        statusLog.clearMessage();
    }

    @Override
    public void updateState(String key, Object value) {
        if ("TransactionStatusMessage".equals(key)) {
            String msg = (String) value;
            if (msg.toLowerCase().startsWith("error")) {
                statusLog.displayErrorMessage(msg);
            } else {
                statusLog.displayMessage(msg);
            }
        }
    }

    @Override
    public Scene createScene() {
        return new Scene(this, 500, 350);
    }
}
