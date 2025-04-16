package userinterface;

import impresario.IModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import java.util.Properties;
import javafx.scene.Node;

/**
 * The class containing the ScoutView for adding/updating a Scout.
 */
public class ScoutView extends View {

    private TextField firstNameField, middleNameField, lastNameField, dobField,
            phoneField, emailField, troopIdField, dateStatusUpdatedField;
    private ComboBox<String> statusBox;
    private Button submitButton, cancelButton;
    private MessageView statusLog;

    public ScoutView(IModel model) {
        super(model, "ScoutView");

        VBox container = new VBox(10);
        container.setPadding(new Insets(15, 5, 5, 5));

        container.getChildren().add(createTitle());
        container.getChildren().add(createFormContent());
        container.getChildren().add(createStatusLog(""));

        getChildren().add(container);
    }

    private Node createTitle() {
        Text titleText = new Text(" Add / Update Scout ");
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

        firstNameField = new TextField();
        middleNameField = new TextField();
        lastNameField = new TextField();
        dobField = new TextField();
        phoneField = new TextField();
        emailField = new TextField();
        troopIdField = new TextField();
        dateStatusUpdatedField = new TextField();

        statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Active", "Inactive");
        statusBox.setValue("Active");

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);

        grid.add(new Label("Middle Name:"), 0, 1);
        grid.add(middleNameField, 1, 1);

        grid.add(new Label("Last Name:"), 0, 2);
        grid.add(lastNameField, 1, 2);

        grid.add(new Label("Date of Birth (MM-DD-YYYY):"), 0, 3);
        grid.add(dobField, 1, 3);

        grid.add(new Label("Phone Number:"), 0, 4);
        grid.add(phoneField, 1, 4);

        grid.add(new Label("Email:"), 0, 5);
        grid.add(emailField, 1, 5);

        grid.add(new Label("Troop ID:"), 0, 6);
        grid.add(troopIdField, 1, 6);

        grid.add(new Label("Status:"), 0, 7);
        grid.add(statusBox, 1, 7);

        grid.add(new Label("Date Status Updated:"), 0, 8);
        grid.add(dateStatusUpdatedField, 1, 8);

        submitButton = new Button("Submit");
        cancelButton = new Button("Cancel");

        submitButton.setOnAction(e -> handleSubmit());
        cancelButton.setOnAction(e -> myModel.stateChangeRequest("CancelScout", null));

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(submitButton, cancelButton);

        vbox.getChildren().addAll(grid, buttons);
        return vbox;
    }

    private void handleSubmit() {
        clearErrorMessage();

        try {
            String firstName = firstNameField.getText().trim();
            String middleName = middleNameField.getText().trim();
            String lastName = lastNameField.getText().trim();

            if (firstName.length() > 20) {
                displayErrorMessage("First name cannot exceed 20 characters.");
                return;
            }
            if (middleName.length() > 20) {
                displayErrorMessage("Middle name cannot exceed 20 characters.");
                return;
            }
            if (lastName.length() > 20) {
                displayErrorMessage("Last name cannot exceed 20 characters.");
                return;
            }

            Properties scoutData = new Properties();
            scoutData.setProperty("firstName", firstName);
            scoutData.setProperty("middleName", middleName);
            scoutData.setProperty("lastName", lastName);
            scoutData.setProperty("dateOfBirth", dobField.getText().trim());
            scoutData.setProperty("phoneNumber", phoneField.getText().trim());
            scoutData.setProperty("email", emailField.getText().trim());
            scoutData.setProperty("troopId", troopIdField.getText().trim());
            scoutData.setProperty("status", statusBox.getValue());
            scoutData.setProperty("dateStatusUpdated", dateStatusUpdatedField.getText().trim());

            myModel.stateChangeRequest("ScoutData", scoutData);
            displayMessage("Scout information submitted.");
        } catch (Exception e) {
            displayErrorMessage("Error submitting scout: " + e.getMessage());
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
        return new Scene(container, 600, 500);
    }
}
