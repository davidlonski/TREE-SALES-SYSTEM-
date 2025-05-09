package userinterface.ScoutViews;

import impresario.IModel;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import userinterface.MessageView;
import userinterface.View;

import java.util.Properties;

public class ModifyScoutView extends View {

    private TextField firstNameField, lastNameField, middleNameField, dateOfBirthField;
    private TextField phoneNumberField, emailField, troopIDField, statusField, datestatusField;
    private Button submitButton, cancelButton;
    private MessageView statusLog;
    private Properties scoutData;

    public ModifyScoutView(IModel model, Properties scoutProps) {
        super(model, "ModifyScoutView");
        this.scoutData = scoutProps;

        VBox container = new VBox(10);
        container.setPadding(new Insets(20));
        container.getChildren().addAll(createTitle(), createFormContent(), createStatusLog(""));

        getChildren().add(container);
    }

    private Node createTitle() {
        Text titleText = new Text("Modify Scout Information");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        return titleText;
    }

    private VBox createFormContent() {
        VBox form = new VBox(10);

        firstNameField = new TextField(scoutData.getProperty("firstName", ""));
        lastNameField = new TextField(scoutData.getProperty("lastName", ""));
        middleNameField = new TextField(scoutData.getProperty("middleName", ""));
        dateOfBirthField = new TextField(scoutData.getProperty("dateOfBirth", ""));
        phoneNumberField = new TextField(scoutData.getProperty("phoneNumber", ""));
        emailField = new TextField(scoutData.getProperty("email", ""));
        troopIDField = new TextField(scoutData.getProperty("troopID", ""));
        statusField = new TextField(scoutData.getProperty("status", ""));
        datestatusField = new TextField(scoutData.getProperty("datestatus", ""));

        submitButton = new Button("Submit Changes");
        cancelButton = new Button("Cancel");

        submitButton.setOnAction(e -> processSubmission());
        cancelButton.setOnAction(e -> cancelAction());

        form.getChildren().addAll(
                new Label("First Name:"), firstNameField,
                new Label("Last Name:"), lastNameField,
                new Label("Middle Name:"), middleNameField,
                new Label("Date of Birth:"), dateOfBirthField,
                new Label("Phone Number:"), phoneNumberField,
                new Label("Email:"), emailField,
                new Label("Troop ID:"), troopIDField,
                new Label("Status:"), statusField,
                new Label("Date Status:"), datestatusField,
                new HBox(10, submitButton, cancelButton)
        );

        return form;
    }

    protected MessageView createStatusLog(String initialMessage) {
        statusLog = new MessageView(initialMessage);
        return statusLog;
    }

    private void processSubmission() {
        String firstName = firstNameField.getText().trim();
        String middleName = middleNameField.getText().trim();
        String lastName = lastNameField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            statusLog.displayErrorMessage("First and Last Name are required.");
            return;
        }
        if (firstName.length() > 20) {
            statusLog.displayErrorMessage("First name cannot exceed 20 characters.");
            return;
        }
        if (middleName.length() > 20) {
            statusLog.displayErrorMessage("Middle name cannot exceed 20 characters.");
            return;
        }
        if (lastName.length() > 20) {
            statusLog.displayErrorMessage("Last name cannot exceed 20 characters.");
            return;
        }

        Properties updatedProps = new Properties();
        updatedProps.setProperty("firstName", firstName);
        updatedProps.setProperty("lastName", lastName);
        updatedProps.setProperty("middleName", middleName);
        updatedProps.setProperty("dateOfBirth", dateOfBirthField.getText().trim());
        updatedProps.setProperty("phoneNumber", phoneNumberField.getText().trim());
        updatedProps.setProperty("email", emailField.getText().trim());
        updatedProps.setProperty("troopID", troopIDField.getText().trim());
        updatedProps.setProperty("status", statusField.getText().trim());
        updatedProps.setProperty("datestatus", datestatusField.getText().trim());
        updatedProps.setProperty("scoutID", scoutData.getProperty("scoutID"));

        myModel.stateChangeRequest("ModifyScoutData", updatedProps);
        statusLog.displayMessage("Scout info updated successfully!");
    }

    private void cancelAction() {
        // Handle cancel button: go back to previous view or clear the form
        clearForm();
        myModel.stateChangeRequest("CancelModifyScout", null);  // Or transition back to the previous view
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
        return new Scene(this, 600, 600);
    }
}
