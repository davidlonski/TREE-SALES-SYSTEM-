package userinterface.TreeTypeViews;

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


public class TreeTypeSearchView extends View {

    private TextField descriptionField;
    private Button searchButton, cancelButton;
    private MessageView statusLog;

    public TreeTypeSearchView(IModel model) {
        super(model, "TreeTypeSearchView");

        VBox container = new VBox(10);
        container.setPadding(new Insets(20));
        container.getChildren().addAll(createTitle(), createFormContent(), createStatusLog(""));

        getChildren().add(container);
    }

    private Node createTitle() {
        Text titleText = new Text("Search for a Tree Type by Description");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        return titleText;
    }

    private VBox createFormContent() {
        VBox form = new VBox(10);

        descriptionField = new TextField();
        descriptionField.setPromptText("Enter description");

        searchButton = new Button("Search");
        searchButton.setOnAction(e -> processSearch());

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction((ActionEvent e) -> cancelAction());

        form.getChildren().addAll(
                new Label("Description:"), descriptionField,
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
        String description = descriptionField.getText().trim();
        if (description.isEmpty()) {
            statusLog.displayErrorMessage("Description cannot be empty.");
            return;
        }

        // Send the search request to the model
        myModel.stateChangeRequest("SearchTreeTypes", description);
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
        // Handle any updates here. For example, display status messages if needed.
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
