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
import model.TreeType;
import model.TreeTypeCollection;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

public class ModifyTreeView extends View {

    private TextField barcodeField, notesField, statusField, dateStatusUpdatedField;
    private ComboBox<TreeType> typeComboBox;
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
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        barcodeField = new TextField(treeData.getProperty("Barcode", ""));
        barcodeField.setEditable(false);

        // ComboBox for TreeType
        typeComboBox = new ComboBox<>();
        typeComboBox.setPromptText("Select Tree Type");

        // Populate ComboBox from DB
        TreeTypeCollection treeTypes = new TreeTypeCollection();
        treeTypes.findAllTreeTypes();
        Vector<TreeType> types = (Vector<TreeType>) treeTypes.getState("TreeTypes");
        if (types != null) {
            typeComboBox.getItems().addAll(types);
        }

        // Match the current type
        String currentTypeDescription = treeData.getProperty("TreeType");
        if (currentTypeDescription != null && !currentTypeDescription.isEmpty()) {
            for (TreeType t : typeComboBox.getItems()) {
                if (currentTypeDescription.equals(t.getState("TypeDescription"))) {
                    typeComboBox.setValue(t);
                    break;
                }
            }
        }

        // Customize display of type descriptions
        typeComboBox.setCellFactory(lv -> new ListCell<TreeType>() {
            @Override
            protected void updateItem(TreeType item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getState("TypeDescription").toString());
            }
        });

        typeComboBox.setButtonCell(new ListCell<TreeType>() {
            @Override
            protected void updateItem(TreeType item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getState("TypeDescription").toString());
            }
        });

        notesField = new TextField(treeData.getProperty("Notes", ""));
        statusField = new TextField(treeData.getProperty("Status", ""));
        dateStatusUpdatedField = new TextField(treeData.getProperty("DateStatusUpdated", ""));

        submitButton = new Button("Submit Changes");
        cancelButton = new Button("Cancel");

        submitButton.setOnAction(e -> processSubmission());
        cancelButton.setOnAction(e -> cancelAction());

        grid.add(new Label("Barcode:"), 0, 0);
        grid.add(barcodeField, 1, 0);
        grid.add(new Label("Tree Type:"), 0, 1);
        grid.add(typeComboBox, 1, 1);
        grid.add(new Label("Notes:"), 0, 2);
        grid.add(notesField, 1, 2);
        grid.add(new Label("Status:"), 0, 3);
        grid.add(statusField, 1, 3);
        grid.add(new Label("Date Status Updated:"), 0, 4);
        grid.add(dateStatusUpdatedField, 1, 4);

        HBox buttonBox = new HBox(10, submitButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        form.getChildren().addAll(grid, buttonBox);
        return form;
    }

    protected MessageView createStatusLog(String initialMessage) {
        statusLog = new MessageView(initialMessage);
        return statusLog;
    }

    private void processSubmission() {
        TreeType selectedType = typeComboBox.getValue();
        String barcode = barcodeField.getText().trim();
        String notes = notesField.getText().trim();
        String status = statusField.getText().trim();
        String dateStatusUpdated = dateStatusUpdatedField.getText().trim();

        if (barcode.isEmpty() || selectedType == null) {
            statusLog.displayErrorMessage("Barcode and Tree Type are required.");
            return;
        }

        if (notes.length() > 200) {
            statusLog.displayErrorMessage("Notes cannot exceed 200 characters.");
            return;
        }

        Properties updatedProps = new Properties();
        updatedProps.setProperty("TreeType", selectedType.getState("ID").toString());
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
        typeComboBox.setValue(null);
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
