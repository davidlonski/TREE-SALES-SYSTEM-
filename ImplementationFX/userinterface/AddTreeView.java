package userinterface;

import impresario.IModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.Properties;

public class AddTreeView extends View {

    private TextField typeField;
    private TextField barcodeField;
    private TextField statusField;
    private TextField dateStatusField;

    private Button submitButton;
    private Button cancelButton;

    private MessageView statusLog;

    public AddTreeView(IModel model) {
        super(model, "AddTreeView");

        VBox container = new VBox(10);
        container.setPadding(new Insets(15, 5, 5, 5));

        container.getChildren().addAll(createTitle(), createFormContent(), createStatusLog(""));

        getChildren().add(container);
    }

    // ---------------------------------------------------------
    private Node createTitle() {
        Text titleText = new Text(" Add New Tree ");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        return titleText;
    }

    // ---------------------------------------------------------
    private VBox createFormContent() {
        VBox vbox = new VBox(10);
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        typeField = new TextField();
        typeField.setPromptText("Tree Type");

        barcodeField = new TextField();
        barcodeField.setPromptText("Barcode Prefix");

        statusField = new TextField();
        statusField.setPromptText("Status");

        dateStatusField = new TextField();
        dateStatusField.setPromptText("Date Status (YYYY-MM-DD)");

        grid.add(new Label("Tree Type:"), 0, 0);
        grid.add(typeField, 1, 0);
        grid.add(new Label("Barcode Prefix:"), 0, 1);
        grid.add(barcodeField, 1, 1);
        grid.add(new Label("Status:"), 0, 2);
        grid.add(statusField, 1, 2);
        grid.add(new Label("Date Status:"), 0, 3);
        grid.add(dateStatusField, 1, 3);

        submitButton = new Button("Submit");
        submitButton.setOnAction(e -> handleSubmit());

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> {
            clearForm();
            myModel.stateChangeRequest("CancelTransaction", null);
        });

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(submitButton, cancelButton);

        vbox.getChildren().addAll(grid, buttonBox);
        return vbox;
    }

    // ---------------------------------------------------------
    private void handleSubmit() {
        clearErrorMessage();

        String type = typeField.getText().trim();
        String barcode = barcodeField.getText().trim();
        String status = statusField.getText().trim();
        String dateStatus = dateStatusField.getText().trim();

        if (type.isEmpty() || barcode.isEmpty() || status.isEmpty() || dateStatus.isEmpty()) {
            displayErrorMessage("All fields are required.");
            return;
        }

        try {
            Properties treeData = new Properties();
            treeData.setProperty("Type", type);
            treeData.setProperty("BarcodePrefix", barcode);
            treeData.setProperty("Status", status);
            treeData.setProperty("DateStatus", dateStatus);

            myModel.stateChangeRequest("AddTree", treeData);

            clearFields();
            displaySuccessMessage("Tree Inserted Successfully!");
        } catch (Exception ex) {
            displayErrorMessage("Error inserting tree: " + ex.getMessage());
        }
    }

    // ---------------------------------------------------------
    private void clearFields() {
        typeField.clear();
        barcodeField.clear();
        statusField.clear();
        dateStatusField.clear();
        statusField.setText("Active");
    }

    private void clearForm() {
        clearFields();
        clearErrorMessage();
    }

    // ---------------------------------------------------------
    protected MessageView createStatusLog(String initialMessage) {
        statusLog = new MessageView(initialMessage);
        return statusLog;
    }

    public void displayErrorMessage(String message) {
        statusLog.displayErrorMessage(message);
    }

    public void displaySuccessMessage(String message) {
        statusLog.displaySuccessMessage(message);
    }

    public void clearSuccessMessage() {
        statusLog.clearMessage();
    }

    public void clearErrorMessage() {
        statusLog.clearMessage();
    }

    @Override
    public void updateState(String key, Object value) {
        if ("TransactionStatusMessage".equals(key)) {
            String msg = (String) value;
            if (msg.toLowerCase().startsWith("error")) {
                displayErrorMessage(msg);
            } else {
                displaySuccessMessage(msg);
            }
        }
    }

    // ---------------------------------------------------------
    @Override
    public Scene createScene() {
        return new Scene(this, 600, 400);
    }
}
