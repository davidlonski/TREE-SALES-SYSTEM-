package userinterface.TreeViews;

import impresario.IModel;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.event.ActionEvent;
import userinterface.MessageView;
import userinterface.View;

public class TreeSearchView extends View {

    private TextField barcodeField;
    private Button searchButton, cancelButton;
    private MessageView statusLog;

    public TreeSearchView(IModel model) {
        super(model, "TreeSearchView");

        VBox container = new VBox(10);
        container.setPadding(new Insets(20));
        container.getChildren().addAll(createTitle(), createFormContent(), createStatusLog(""));

        getChildren().add(container);
    }

    private Node createTitle() {
        Text titleText = new Text("Search for a Tree by Barcode");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        return titleText;
    }

    private VBox createFormContent() {
        VBox form = new VBox(10);

        barcodeField = new TextField();
        barcodeField.setPromptText("Enter barcode");

        searchButton = new Button("Search");
        searchButton.setOnAction(e -> processSearch());

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction((ActionEvent e) -> cancelAction());

        form.getChildren().addAll(
                new Label("Barcode:"), barcodeField,
                new HBox(10, searchButton, cancelButton)
        );

        return form;
    }

    protected MessageView createStatusLog(String initialMessage) {
        statusLog = new MessageView(initialMessage);
        return statusLog;
    }

    private void processSearch() {
        clearErrorMessage();
        String barcode = barcodeField.getText().trim();
        if (barcode.isEmpty()) {
            statusLog.displayErrorMessage("Barcode cannot be empty.");
            return;
        }

        // Send the search request to the model
        myModel.stateChangeRequest("SearchTrees", barcode);
    }

    public void clearErrorMessage() {
        statusLog.clearErrorMessage();
    }

    private void cancelAction() {
        // Cancel button should transition back to the transaction choice view
        myModel.stateChangeRequest("CancelSearch", null);
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
        return new Scene(this, 400, 200);
    }
}
