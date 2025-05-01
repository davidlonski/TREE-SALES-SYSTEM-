package userinterface;

import impresario.IModel;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.Properties;

public class ModifyTreeView extends View {

    private TextField barcodeField, treeTypeField, notesField;
    private TextField statusField, dateStatusUpdatedField;
    private Button submitButton, cancelButton;
    private MessageView statusLog;
    private Properties treeData;

    public ModifyTreeView(IModel model, Properties treeProps) {
        super(model, "ModifyTreeView");
        this.treeData = treeProps;

        VBox container = new VBox(10);
        container.setPadding(new Insets(20));
        container.getChildren().addAll(createTitle(), createFormContent(), createStatusLog(""));

        getChildren().add(container);
    }

    private Node createTitle() {
        Text titleText = new Text("Modify Tree Information");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        return titleText;
    }

    private VBox createFormContent() {
        VBox form = new VBox(10);

        barcodeField = new TextField(treeData.getProperty("Barcode", ""));
        barcodeField.setEditable(false); // Barcode shouldn't be editable
        treeTypeField = new TextField(treeData.getProperty("TreeType", ""));
        notesField = new TextField(treeData.getProperty("Notes", ""));
        statusField = new TextField(treeData.getProperty("Status", ""));
        dateStatusUpdatedField = new TextField(treeData.getProperty("DateStatusUpdated", ""));

        submitButton = new Button("Submit Changes");
        cancelButton = new Button("Cancel");

        submitButton.setOnAction(e -> processSubmission());
        cancelButton.setOnAction(e -> cancelAction());

        form.getChildren().addAll(
                new Label("Barcode:"), barcodeField,
                new Label("Tree Type:"), treeTypeField,
                new Label("Notes:"), notesField,
                new Label("Status:"), statusField,
                new Label("Date Status Updated:"), dateStatusUpdatedField,
                new HBox(10, submitButton, cancelButton)
        );

        return form;
    }

    protected MessageView createStatusLog(String initialMessage) {
        statusLog = new MessageView(initialMessage);
        return statusLog;
    }

    private void processSubmission() {
        String barcode = barcodeField.getText().trim();
        String treeType = treeTypeField.getText().trim();
        String notes = notesField.getText().trim();
        String status = statusField.getText().trim();
        String dateStatusUpdated = dateStatusUpdatedField.getText().trim();

        if (barcode.isEmpty() || treeType.isEmpty()) {
            statusLog.displayErrorMessage("Barcode and Tree Type are required.");
            return;
        }

        if (notes != null && notes.length() > 200) {
            statusLog.displayErrorMessage("Notes cannot exceed 200 characters.");
            return;
        }

        Properties updatedProps = new Properties();
        updatedProps.setProperty("TreeType", treeType);
        updatedProps.setProperty("Barcode", barcode);
        updatedProps.setProperty("Notes", notes);
        updatedProps.setProperty("Status", status);
        updatedProps.setProperty("DateStatusUpdated", dateStatusUpdated);

        myModel.stateChangeRequest("ModifyTreeData", updatedProps);
        statusLog.displayMessage("Tree info updated successfully!");
    }

    private void cancelAction() {
        clearForm();
        myModel.stateChangeRequest("CancelModifyTree", null);
    }

    private void clearForm() {
        barcodeField.clear();
        treeTypeField.clear();
        notesField.clear();
        statusField.clear();
        dateStatusUpdatedField.clear();
        statusLog.clearErrorMessage();
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
        return new Scene(this, 600, 400);
    }
}

