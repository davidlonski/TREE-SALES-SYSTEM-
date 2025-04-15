package userinterface;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Properties;

public class AddTreeView extends VBox {

    private TextField typeField;
    private TextField ageField;
    private TextField heightField;
    private TextField barcodeField;
    private TextField statusField;
    private TextField datestatusField;
    private Button submitButton;
    private Button cancelButton;
    private MessageView statusLog;

    public AddTreeView() {
        setPadding(new Insets(20));
        setSpacing(10);

        Label titleLabel = new Label("Add New Tree");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        typeField = new TextField();
        typeField.setPromptText("Tree Type");

        ageField = new TextField();
        ageField.setPromptText("Tree Age");

        heightField = new TextField();
        heightField.setPromptText("Tree Height (in meters)");

        barcodeField = new TextField();
        barcodeField.setPromptText("BarcodePrefix");

        statusField = new TextField();
        statusField.setPromptText("Status");

        datestatusField = new TextField();
        datestatusField.setPromptText("Date Status");

        submitButton = new Button("Submit");
        cancelButton = new Button("Cancel");

        HBox buttonBox = new HBox(10, submitButton, cancelButton);

        getChildren().addAll(titleLabel,
                new Label("Type:"), typeField,
                new Label("Age:"), ageField,
                new Label("Height:"), heightField,
                new Label("BarcodePrefix:"), barcodeField,
                new Label("Status:"), statusField,
                new Label("Date Status:"), datestatusField,
                buttonBox
        );

        statusLog = new MessageView();
        getChildren().add(statusLog);

        submitButton.setOnAction(e -> processAction());
        cancelButton.setOnAction(e -> clearForm());
    }

    private void processAction() {
        String type = typeField.getText().trim();
        String age = ageField.getText().trim();
        String height = heightField.getText().trim();
        String barcode = barcodeField.getText().trim();
        String status = statusField.getText().trim();
        String dateStatus = datestatusField.getText().trim();

        if (type.isEmpty() || age.isEmpty() || height.isEmpty() || barcode.isEmpty() || status.isEmpty() || dateStatus.isEmpty()) {
            statusLog.displayErrorMessage("Please fill in all fields.");
            return;
        }

        // Add validation logic here if needed (e.g., numeric check for age/height)

        Properties treeProps = new Properties();
        treeProps.setProperty("type", type);
        treeProps.setProperty("age", age);
        treeProps.setProperty("height", height);
        treeProps.setProperty("barcodeprefix", barcode);
        treeProps.setProperty("status", status);
        treeProps.setProperty("datestatus", dateStatus);

        // Send to controller
        // Example: TreeController.processNewTree(treeProps);
        statusLog.displayMessage("Tree submitted successfully!");
    }

    private void clearForm() {
        typeField.clear();
        ageField.clear();
        heightField.clear();
        barcodeField.clear();
        statusField.clear();
        datestatusField.clear();
        statusLog.clearErrorMessage();
    }
}
