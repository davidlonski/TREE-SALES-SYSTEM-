package userinterface;

import impresario.IModel;
import javafx.event.*;
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


public class AddScoutView extends View {

    private TextField lastNameField;
    private TextField firstNameField;
    private TextField middleNameField;
    private TextField dobField;
    private TextField phoneNumberField;
    private TextField emailField;
    private TextField troopIdField;
    private TextField dateStatusUpdatedField;

    private Button submitButton;
    private Button cancelButton;

    private MessageView statusLog;


    public AddScoutView(IModel model) {
        super(model, "AddScoutView");


        VBox container = new VBox(10);
        container.setPadding(new Insets(15, 5, 5, 5));

        container.getChildren().add(createTitle());

        container.getChildren().add(createFormContents());

        container.getChildren().add(createButtonPanel());

        container.getChildren().add(createStatusLog(""));

        getChildren().add(container);

        lastNameField.requestFocus();

        myModel.subscribe("InsertSuccessful", this);
    }

    private Node createTitle() {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER);

        Text titleText = new Text("Adding A New Scout");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        container.getChildren().add(titleText);

        return container;
    }

    private Node createFormContents() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label lastNameLabel = new Label("Last Name:");
        grid.add(lastNameLabel, 0, 0);

        lastNameField = new TextField();
        lastNameField.setOnAction(e -> processAction(e));
        grid.add(lastNameField, 1, 0);

        Label firstNameLabel = new Label("First Name:");
        grid.add(firstNameLabel, 0, 1);

        firstNameField = new TextField();
        firstNameField.setOnAction(e -> processAction(e));
        grid.add(firstNameField, 1, 1);

        Label middleNameLabel = new Label("Middle Name:");
        grid.add(middleNameLabel, 0, 2);

        middleNameField = new TextField();
        middleNameField.setOnAction(e -> processAction(e));
        grid.add(middleNameField, 1, 2);

        Label dobLabel = new Label("Date of Birth (MM-DD-YYYY):");
        grid.add(dobLabel, 0, 3);

        dobField = new TextField();
        dobField.setOnAction(e -> processAction(e));
        grid.add(dobField, 1, 3);

        Label phoneLabel = new Label("Phone Number:");
        grid.add(phoneLabel, 0, 4);

        phoneNumberField = new TextField();
        phoneNumberField.setOnAction(e -> processAction(e));
        grid.add(phoneNumberField, 1, 4);

        Label emailLabel = new Label("Email:");
        grid.add(emailLabel, 0, 5);

        emailField = new TextField();
        emailField.setOnAction(e -> processAction(e));
        grid.add(emailField, 1, 5);

        Label troopIdLabel = new Label("Troop ID:");
        grid.add(troopIdLabel, 0, 6);

        troopIdField = new TextField();
        troopIdField.setOnAction(e -> processAction(e));
        grid.add(troopIdField, 1, 6);


        Label dateStatusLabel = new Label("Date Status Updated:");
        grid.add(dateStatusLabel, 0, 7);

        dateStatusUpdatedField = new TextField();

        dateStatusUpdatedField.setText(new SimpleDateFormat("MM-dd-yyyy").format(new Date()));
        dateStatusUpdatedField.setOnAction(e -> processAction(e));
        grid.add(dateStatusUpdatedField, 1, 7);

        return grid;
    }


    private HBox createButtonPanel() {
        HBox btnContainer = new HBox(15);
        btnContainer.setAlignment(Pos.CENTER);

        submitButton = new Button("Submit");
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                processAction(e);
            }
        });

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                processAction(e);
            }
        });

        btnContainer.getChildren().add(submitButton);
        btnContainer.getChildren().add(cancelButton);

        return btnContainer;
    }


    private MessageView createStatusLog(String initialMessage) {
        statusLog = new MessageView(initialMessage);
        return statusLog;
    }


    private void processAction(Event evt) {
        clearErrorMessage();
        if(evt.getSource() == submitButton){
            processTransaction();
        }else if(evt.getSource() == cancelButton){
            cancelTransaction();
        }
    }


    private void cancelTransaction() {
        clearErrorMessage();
        clearFields();
        myModel.stateChangeRequest("CancelAddScoutTransaction", null);
    }

    private void processTransaction() {
        clearErrorMessage();

        if (!validateFormData())
            return;

        Properties scoutData = new Properties();
        scoutData.setProperty("LastName", lastNameField.getText());
        scoutData.setProperty("FirstName", firstNameField.getText());
        scoutData.setProperty("MiddleName", middleNameField.getText());
        scoutData.setProperty("DateOfBirth", dobField.getText());
        scoutData.setProperty("PhoneNumber", phoneNumberField.getText());
        scoutData.setProperty("Email", emailField.getText());
        scoutData.setProperty("TroopID", troopIdField.getText());
        scoutData.setProperty("Status", "Active");
        scoutData.setProperty("DateStatusUpdated", dateStatusUpdatedField.getText());

        myModel.stateChangeRequest("ProcessScoutTransaction", scoutData);

        clearFields();
    }

    private boolean validateFormData() {

        if (lastNameField.getText().isEmpty()) {
            displayErrorMessage("Last Name is required.");
            lastNameField.requestFocus();
            return false;
        }

        if (firstNameField.getText().isEmpty()) {
            displayErrorMessage("First Name is required.");
            firstNameField.requestFocus();
            return false;
        }

        String dobText = dobField.getText();
        if (!dobText.isEmpty()) {

            if (!dobText.matches("\\d{2}-\\d{2}-\\d{4}")) {
                displayErrorMessage("Date of Birth must be in MM-DD-YYYY format.");
                dobField.requestFocus();
                return false;
            }
        } else {
            displayErrorMessage("Date of Birth is required.");
            dobField.requestFocus();
            return false;
        }

        String phoneText = phoneNumberField.getText();
        if (!phoneText.isEmpty() && !phoneText.matches("\\d{3}-\\d{3}-\\d{4}")) {
            displayErrorMessage("Phone Number must be in 123-456-7890 format.");
            phoneNumberField.requestFocus();
            return false;
        }

        String emailText = emailField.getText();
        if (!emailText.isEmpty() && !emailText.contains("@")) {
            displayErrorMessage("Email must be a valid email address.");
            emailField.requestFocus();
            return false;
        }

        if (troopIdField.getText().isEmpty()) {
            displayErrorMessage("Troop ID is required.");
            troopIdField.requestFocus();
            return false;
        }


        String dateStatusText = dateStatusUpdatedField.getText();
        if (!dateStatusText.isEmpty()) {

            if (!dateStatusText.matches("\\d{2}-\\d{2}-\\d{4}")) {
                displayErrorMessage("Date Status Updated must be in MM-DD-YYYY format.");
                dateStatusUpdatedField.requestFocus();
                return false;
            }
        } else {

            dateStatusUpdatedField.setText(new SimpleDateFormat("MM-dd-yyyy").format(new Date()));
        }

        return true;
    }

    public void displayErrorMessage(String message) {
        statusLog.displayErrorMessage(message);
    }

    public void clearErrorMessage() {
        statusLog.clearErrorMessage();
    }

    @Override
    public Scene createScene() {
        return new Scene(this, 500, 600);
    }

    @Override
    public void updateState(String key, Object value) {
        if("InsertSuccessful".equals(key)) {
            clearFields();
            statusLog.displayMessage((String) value);
        }
    }

    private void clearFields() {
        lastNameField.clear();
        firstNameField.clear();
        middleNameField.clear();
        dobField.clear();
        phoneNumberField.clear();
        emailField.clear();
        troopIdField.clear();
        dateStatusUpdatedField.setText(new SimpleDateFormat("MM-dd-yyyy").format(new Date()));

        lastNameField.requestFocus();
    }
}