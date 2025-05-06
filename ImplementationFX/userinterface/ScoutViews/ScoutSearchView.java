package userinterface.ScoutViews;

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

public class ScoutSearchView extends View {

    private TextField lastNameField;
    private Button searchButton, cancelButton;
    private MessageView statusLog;

    public ScoutSearchView(IModel model) {
        super(model, "ScoutSearchView");

        VBox container = new VBox(10);
        container.setPadding(new Insets(20));
        container.getChildren().addAll(createTitle(), createFormContent(), createStatusLog(""));

        getChildren().add(container);
    }

    private Node createTitle() {
        Text titleText = new Text("Search for a Scout by Last Name");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        return titleText;
    }

    private VBox createFormContent() {
        VBox form = new VBox(10);

        lastNameField = new TextField();
        lastNameField.setPromptText("Enter last name");

        searchButton = new Button("Search");
        searchButton.setOnAction(e -> processSearch());

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction((ActionEvent e) -> cancelAction());

        form.getChildren().addAll(
                new Label("Last Name:"), lastNameField,
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
        String lastName = lastNameField.getText().trim();
        if (lastName.isEmpty()) {
            statusLog.displayErrorMessage("Last name cannot be empty.");
            return;
        }

        // Send the search request to the model
        myModel.stateChangeRequest("SearchScouts", lastName);
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
