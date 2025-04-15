package userinterface;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.awt.*;
import java.util.Properties;

public class ModifyScoutView extends VBox {

    private TextField firstNameField;
    private TextField lastNameField;
    private TextField middleNameField;
    private TextField dateOfBirthField;
    private TextField phoneNumberField;
    private TextField emailField;
    private TextField troopIDField;
    private TextField statusField;
    private TextField datestatusField;

    private Button submitButton;
    private Button cancelButton;
    private MessageView statusLog;

    private Properties scoutData;

    public ModifyScoutView(Properties scoutProps) {
        this.scoutData = scoutProps;

        setPadding(new Insets(20));
        setSpacing(10);

        Label titleLabel = new Label("Modify Scout Information");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        firstNameField = new TextField(scoutProps.getProperty("firstName", ""));
        lastNameField = new TextField(scoutProps.getProperty("lastName", ""));
        middleNameField = new TextField(scoutProps.getProperty("middleName", ""));
        dateOfBirthField = new TextField(scoutProps.getProperty("dateOfBirth", ""));
        phoneNumberField = new TextField(scoutProps.getProperty("phoneNumber", ""));
        emailField = new TextField(scoutProps.getProperty("email", ""));
        troopIDField = new TextField(scoutProps.getProperty("troopID", ""));
        statusField = new TextField(scoutProps.getProperty("status", ""));
        datestatusField = new TextField(scoutProps.getProperty("datestatus", ""));

        submitButton = new Button("Submit Changes");
        cancelButton = new Button("Cancel");

        HBox buttonBox = new HBox(10, submitButton, cancelButton);

        getChildren().addAll(
                titleLabel,
                new Label("First Name:"), firstNameField,
                new Label("Last Name:"), lastNameField,
                new Label("Middle Name:"), middleNameField,
                new Label("Date of Birth:"), dateOfBirthField,
                new Label("Phone Number:"), phoneNumberField,
                new Label("Email:"), emailField,
                new Label("Troop ID:"), troopIDField,
                new Label("Status:"), statusField,
                new Label("Date Status:"), datestatusField,
                buttonBox
        );

        statusLog = new MessageView();
        getChildren().add(statusLog);

        submitButton.setOnAction(e -> processSubmission());
        cancelButton.setOnAction(e -> clearForm());
    }

    private void processSubmission() {
        Properties updatedProps = new Properties();

        updatedProps.setProperty("firstName", firstNameField.getText().trim());
        updatedProps.setProperty("lastName", lastNameField.getText().trim());
        updatedProps.setProperty("middleName", middleNameField.getText().trim());
        updatedProps.setProperty("dateOfBirth", dateOfBirthField.getText().trim());
        updatedProps.setProperty("phoneNumber", phoneNumberField.getText().trim());
        updatedProps.setProperty("email", emailField.getText().trim());
        updatedProps.setProperty("troopID", troopIDField.getText().trim());
        updatedProps.setProperty("status", statusField.getText().trim());
        updatedProps.setProperty("datestatus", datestatusField.getText().trim());

        // Validate inputs as needed
        if (updatedProps.getProperty("firstName").isEmpty() || updatedProps.getProperty("lastName").isEmpty()) {
            statusLog.displayErrorMessage("First and Last Name are required.");
            return;
        }

        // Send updated data to controller
        // Example: controller.processModifiedScout(updatedProps);
        statusLog.displayMessage("Scout info updated successfully!");
    }

    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        middleNameField.clear();
        dateOfBirthField.clear();
        phoneNumberField.clear();
        emailField.clear();
        troopIDField.clear();
        statusField.clear();
        datestatusField.clear();
        statusLog.clearErrorMessage();
    }
}
