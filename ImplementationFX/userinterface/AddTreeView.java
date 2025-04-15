package userinterface;

import model.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import impresario.IModel;

import java.util.Properties;

public class AddTreeView extends View {

    private TextField typeField;
    private TextField barcodeField;
    private TextField statusField;
    private TextField datestatusField;
    private Button submitButton;
    private Button cancelButton;
    private MessageView statusLog;

    public AddTreeView(IModel model) {
        super(model, "AddTreeView");

        VBox container = new VBox(10);
        container.setPadding(new Insets(20));
        container.getChildren().addAll(createTitle(), createFormContent(), createStatusLog(""));

        getChildren().add(container);
    }

    // -----------------------------
    private Node createTitle() {
        Text titleText = new Text(" Add New Tree ");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        return titleText;
    }

    // -----------------------------
    private Node createFormContent() {
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(10));

        typeField = new TextField();
        typeField.setPromptText("Tree Type");

        barcodeField = new TextField();
        barcodeField.setPromptText("Barcode Prefix");

        statusField = new TextField();
        statusField.setPromptText("Status");

        datestatusField = new TextField();
        datestatusField.setPromptText("Date Status");

        submitButton = new Button("Submit");
        cancelButton = new Button("Cancel");

        submitButton.setOnAction(e -> processAction());
        cancelButton.setOnAction(e -> clearForm());

        HBox buttonBox = new HBox(10, submitButton, cancelButton);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        formBox.getChildren().addAll(
                new Label("Type:"), typeField,
                new Label("Barcode Prefix:"), barcodeField,
                new Label("Status:"), statusField,
                new Label("Date Status:"), datestatusField,
                buttonBox
        );

        return formBox;
    }

    // -----------------------------
    protected MessageView createStatusLog(String initialMessage) {
        statusLog = new MessageView(initialMessage);
        return statusLog;
    }

    // -----------------------------
    private void processAction() {
        String type = typeField.getText().trim();
        String barcode = barcodeField.getText().trim();
        String status = statusField.getText().trim();
        String dateStatus = datestatusField.getText().trim();

        if (type.isEmpty() || barcode.isEmpty() || status.isEmpty() || dateStatus.isEmpty()) {
            statusLog.displayErrorMessage("Please fill in all fields.");
            return;
        }

        Properties treeProps = new Properties();
        treeProps.setProperty("Type", type);
        treeProps.setProperty("BarcodePrefix", barcode);
        treeProps.setProperty("Status", status);
        treeProps.setProperty("DateStatus", dateStatus);

        myModel.stateChangeRequest("AddTree", treeProps);
        statusLog.displayMessage("Tree submitted successfully!");
    }

    // -----------------------------
    private void clearForm() {
        typeField.clear();
        barcodeField.clear();
        statusField.clear();
        datestatusField.clear();
        statusLog.clearErrorMessage();
    }

    // -----------------------------
    @Override
    public void updateState(String key, Object value) {
        if (key.equals("TransactionStatusMessage")) {
            String msg = (String) value;
            if (msg.toLowerCase().startsWith("error")) {
                statusLog.displayErrorMessage(msg);
            } else {
                statusLog.displayMessage(msg);
            }
        }
    }

    // -----------------------------
    @Override
    public Scene createScene() {
        return new Scene(this, 600, 400);
    }
}
