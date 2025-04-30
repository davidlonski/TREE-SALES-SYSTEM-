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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class ModifyTreeView extends View {

    private ComboBox<String> typeComboBox;
    private TextField barcodeField;
    private TextField notesField;
    private TextField dateStatusField;

    private Button submitButton;
    private Button cancelButton;

    private MessageView statusLog;

    private Properties treeData;  // Store the original tree data

    public ModifyTreeView(IModel model) {
        super(model, "ModifyTreeView");

        VBox container = new VBox(10);
        container.setPadding(new Insets(15, 5, 5, 5));

        container.getChildren().addAll(createTitle(), createFormContent(), createStatusLog(""));

        getChildren().add(container);

        myModel.subscribe("TransactionStatusMessage", this);
    }

    private Node createTitle() {
        Text titleText = new Text(" Modify Tree ");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        return titleText;
    }

    private VBox createFormContent() {
        VBox vbox = new VBox(10);
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll(
                "Frasier Fir - Regular",
                "Frasier Fir - Premium",
                "Douglass Fir - Regular",
                "Douglass Fir - Premium",
                "Blue Spruce - Regular",
                "Blue Spruce - Premium",
                "Concolor - Regular",
                "Concolor - Premium",
                "Balsam Fir - Regular",
                "Balsam Fir - Premium"
        );
        typeComboBox.setPromptText("Select Tree Type");

        barcodeField = new TextField();
        barcodeField.setEditable(false);  // Read-only barcode
        barcodeField.setDisable(true);

        notesField = new TextField();
        notesField.setPromptText("Enter notes");

        dateStatusField = new TextField();
        dateStatusField.setPromptText("MM-DD-YYYY");

        grid.add(new Label("Tree Type:"), 0, 0);
        grid.add(typeComboBox, 1, 0);
        grid.add(new Label("Barcode:"), 0, 1);
        grid.add(barcodeField, 1, 1);
        grid.add(new Label("Notes:"), 0, 2);
        grid.add(notesField, 1, 2);
        grid.add(new Label("Date Status:"), 0, 3);
        grid.add(dateStatusField, 1, 3);

        submitButton = new Button("Submit");
        submitButton.setOnAction(e -> handleSubmit());

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> {
            clearErrorMessage();
            myModel.stateChangeRequest("CancelTransaction", null);
        });

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(submitButton, cancelButton);

        vbox.getChildren().addAll(grid, buttonBox);
        return vbox;
    }

    private void handleSubmit() {
        clearErrorMessage();

        String type = typeComboBox.getValue();
        String barcode = barcodeField.getText().trim();
        String notes = notesField.getText().trim();
        String dateStatus = dateStatusField.getText().trim();

        if (type == null || barcode.isEmpty() || notes.isEmpty() || dateStatus.isEmpty()) {
            displayErrorMessage("All fields are required.");
            return;
        }

        try {
            // Validate date format
            new SimpleDateFormat("MM-dd-yyyy").parse(dateStatus); // Will throw ParseException if invalid

            Properties updatedTree = new Properties();
            updatedTree.setProperty("Barcode", barcode); // Required to identify which tree
            updatedTree.setProperty("Type", type);
            updatedTree.setProperty("Notes", notes);
            updatedTree.setProperty("Status", "Active");
            updatedTree.setProperty("DateStatus", dateStatus);

            myModel.stateChangeRequest("ModifyTree", updatedTree);

            displaySuccessMessage("Tree updated successfully.");
        } catch (Exception ex) {
            displayErrorMessage("Error updating tree: " + ex.getMessage());
        }
    }

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

    public void clearErrorMessage() {
        statusLog.clearMessage();
    }

    public void populateFields(Properties tree) {
        this.treeData = tree;

        typeComboBox.setValue(tree.getProperty("Type"));
        barcodeField.setText(tree.getProperty("Barcode"));
        notesField.setText(tree.getProperty("Notes", ""));
        dateStatusField.setText(tree.getProperty("DateStatus", new SimpleDateFormat("MM-dd-yyyy").format(new Date())));
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
        } else if ("TreeToModify".equals(key)) {
            populateFields((Properties) value);
        }
    }

    @Override
    public Scene createScene() {
        return new Scene(this, 600, 400);
    }
}

