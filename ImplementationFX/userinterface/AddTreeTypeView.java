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
import model.TreeTypeCollection;
import model.TreeType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

public class AddTreeTypeView extends View{
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

        container.getChildren().addAll(createTitle(), createFormContent(), createStatusLog(""));

        getChildren().add(container);
    }

    private Node createTitle() {
        Text titleText = new Text(" Add New Tree Type ");
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

        typeDescriptionField = new TextField();
        typeDescriptionField.setPromptText("Enter A Description");

        costField = new TextField();
        costField.setPromptText("Enter Cost");

        barcodePrefixField = new TextField();
        barcodePrefixField.setPromptText("Enter Barcode Prefix");

        grid.add(new Label("Type Description:"), 0, 0);
        grid.add(typeDescriptionField, 1, 0);
        grid.add(new Label("Cost:"), 0, 1);
        grid.add(costField, 1, 1);
        grid.add(new Label("Barcode Prefix:"), 0, 2);
        grid.add(barcodePrefixField, 1, 2);


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

    private void clearFields() {
        typeDescriptionField.clear();
        costField.clear();
        barcodePrefixField.clear();
    }

    private void handleSubmit() {
        clearErrorMessage();

        String typeDesc = typeDescriptionField.getText().trim();
        String cost = costField.getText().trim();
        String barcodePrefix = barcodePrefixField.getText().trim();

        if (typeDesc.length() > 200) {
            displayErrorMessage("Description must be 20 characters or fewer.");
            return;
        }

        if (barcodePrefix.length() > 20) {
            displayErrorMessage("Barcode Prefix must be 20 characters or fewer.");
            return;
        }

        try {
            Properties treeTypeData = new Properties();
            treeTypeData.setProperty("TypeDescription", typeDesc);
            treeTypeData.setProperty("Cost", cost);
            treeTypeData.setProperty("BarcodePrefix", barcodePrefix);

            myModel.stateChangeRequest("AddTree", treeTypeData);

            clearFields();
        } catch (Exception ex) {
            displayErrorMessage("Error inserting tree type: " + ex.getMessage());
        }
    }



    private void clearForm() {
        clearFields();
        clearErrorMessage();
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
                clearFields();
                displaySuccessMessage(msg);
            }
        }
    }

    @Override
    public Scene createScene() {
        return new Scene(this, 600, 400);
    }
}
