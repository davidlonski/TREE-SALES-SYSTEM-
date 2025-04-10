package userinterface;

import impresario.IModel;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import java.util.Properties;
import javafx.scene.Node;

public class TreeView extends View {

    private TextField barcodeField, treeTypeField, notesField, dateStatusUpdatedField;
    private ComboBox<String> statusBox;
    private Button submitButton, cancelButton;
    private MessageView statusLog;

    public TreeView(IModel model) {
        super(model, "TreeView");

        VBox container = new VBox(10);
        container.setPadding(new Insets(15, 5, 5, 5));
        container.getChildren().addAll(createTitle(), createFormContent(), createStatusLog(""));
        getChildren().add(container);
    }

    private Node createTitle() {
        Text titleText = new Text(" Add / Update Tree ");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        return titleText;
    }

    private VBox createFormContent() {
        VBox vbox = new VBox(10);
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25));

        barcodeField = new TextField();
        treeTypeField = new TextField();
        notesField = new TextField();
        dateStatusUpdatedField = new TextField();

        statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Available", "Sold", "Damaged");
        statusBox.setValue("Available");

        grid.add(new Label("Barcode:"), 0, 0);
        grid.add(barcodeField, 1, 0);

        grid.add(new Label("Tree Type ID:"), 0, 1);
        grid.add(treeTypeField, 1, 1);

        grid.add(new Label("Notes:"), 0, 2);
        grid.add(notesField, 1, 2);

        grid.add(new Label("Status:"), 0, 3);
        grid.add(statusBox, 1, 3);

        grid.add(new Label("Date Status Updated:"), 0, 4);
        grid.add(dateStatusUpdatedField, 1, 4);

        submitButton = new Button("Submit");
        cancelButton = new Button("Cancel");

        submitButton.setOnAction(e -> handleSubmit());
        cancelButton.setOnAction(e -> myModel.stateChangeRequest("CancelTree", null));

        HBox buttonBox = new HBox(20, submitButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        vbox.getChildren().addAll(grid, buttonBox);
        return vbox;
    }

    private void handleSubmit() {
        clearErrorMessage();

        try {
            Properties treeData = new Properties();
            treeData.setProperty("barcode", barcodeField.getText().trim());
            treeData.setProperty("treeType", treeTypeField.getText().trim());
            treeData.setProperty("notes", notesField.getText().trim());
            treeData.setProperty("status", statusBox.getValue());
            treeData.setProperty("dateStatusUpdated", dateStatusUpdatedField.getText().trim());

            myModel.stateChangeRequest("TreeData", treeData);
            displayMessage("Tree information submitted.");
        } catch (Exception e) {
            displayErrorMessage("Error submitting tree: " + e.getMessage());
        }
    }

    protected MessageView createStatusLog(String initialMessage) {
        statusLog = new MessageView(initialMessage);
        return statusLog;
    }

    public void displayErrorMessage(String message) {
        statusLog.displayErrorMessage(message);
    }

    public void displayMessage(String message) {
        statusLog.displayMessage(message);
    }

    public void clearErrorMessage() {
        statusLog.clearErrorMessage();
    }

    @Override
    public void updateState(String key, Object value) {
        if (key.equals("TransactionError")) {
            displayErrorMessage((String) value);
        } else if (key.equals("TransactionSuccess")) {
            displayMessage((String) value);
        }
    }

    @Override
    public Scene createScene() {
        VBox container = new VBox(10);
        container.getChildren().addAll(createTitle(), createFormContent(), createStatusLog(""));
        return new Scene(container, 600, 450);
    }
}
