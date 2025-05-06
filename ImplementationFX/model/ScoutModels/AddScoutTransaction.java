package model.ScoutModels;

import java.util.Properties;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

// project imports
import model.Transaction;
import userinterface.View;
import userinterface.ViewFactory;

public class AddScoutTransaction extends Transaction {
    private Scout newScout;
    private String transactionErrorMessage = "";
    private String transactionSuccessMessage = "";

    public AddScoutTransaction() throws Exception {
        super();
    }

    protected void setDependencies() {
        dependencies = new Properties();
        dependencies.setProperty("InsertSuccessful", "");
        dependencies.setProperty("TransactionError", "");
        myRegistry.setDependencies(dependencies);
    }

    private void processTransaction(Properties props) {
        try {
            // Create a new scout with the provided properties
            newScout = new Scout(props);

            // Save to database
            newScout.save();

            // Create success message
            String scoutId = (String)newScout.getState("ID");
            String scoutName = props.getProperty("FirstName") + " " + props.getProperty("LastName");
            transactionSuccessMessage = "Successfully added scout " + scoutName + " (ID: " + scoutId + ") to the database";

            // Show success notification with Done button
            showSuccessNotification(scoutName, scoutId);

        } catch (Exception e) {
            // Handle any errors
            transactionErrorMessage = "ERROR: Failed to add scout - " + e.getMessage();
            myRegistry.updateSubscribers("TransactionError", this);
        }
    }

    /**
     * Display a success notification with a Done button
     */
    private void showSuccessNotification(String scoutName, String scoutId) {
        // Use Platform.runLater to ensure this runs on the JavaFX thread
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Scout Added");
            alert.setHeaderText("Scout Added Successfully");
            alert.setContentText("Scout " + scoutName + " (ID: " + scoutId + ") has been successfully added to the database.");

            // Replace OK button with a Done button
            ButtonType doneButton = new ButtonType("Done");
            alert.getButtonTypes().setAll(doneButton);

            // Show the alert and wait for response
            Optional<ButtonType> result = alert.showAndWait();

            // When Done button is clicked, return to main interface
            if (result.isPresent() && result.get() == doneButton) {
                // Return to main menu
                myRegistry.updateSubscribers("InsertSuccessful", this);
                myRegistry.updateSubscribers("CancelTransaction", this);
            }
        });
    }

    public Object getState(String key) {
        if (key.equals("InsertSuccessful")) {
            return transactionSuccessMessage;
        } else if (key.equals("TransactionError")) {
            return transactionErrorMessage;
        }
        return "";
    }

    public void stateChangeRequest(String key, Object value) {
        if (key.equals("DoYourJob")) {
            doYourJob();
        } else if (key.equals("ProcessScoutTransaction")) {
            processTransaction((Properties) value);
        } else if (key.equals("CancelAddScoutTransaction")) {
            myRegistry.updateSubscribers("CancelTransaction", this);
        }

        myRegistry.updateSubscribers(key, this);
    }

    protected Scene createView() {
        Scene currentScene = myViews.get("AddScoutView");

        if (currentScene == null) {
            View newView = ViewFactory.createView("AddScoutView", this);
            currentScene = new Scene(newView);
            myViews.put("AddScoutView", currentScene);

            return currentScene;
        } else {
            return currentScene;
        }
    }
}