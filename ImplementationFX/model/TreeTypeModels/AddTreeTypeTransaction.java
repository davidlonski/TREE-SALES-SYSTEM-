package model.TreeTypeModels;

import java.util.Properties;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import model.Transaction;
import userinterface.View;
import userinterface.ViewFactory;

public class AddTreeTypeTransaction extends Transaction {
    private TreeType newTreeType;
    private String transactionErrorMessage = "";
    private String transactionSuccessMessage = "";

    public AddTreeTypeTransaction() throws Exception {
        super();
    }

    @Override
    protected void setDependencies() {
        dependencies = new Properties();
        dependencies.setProperty("InsertSuccessful", "");
        dependencies.setProperty("TransactionError", "");
        myRegistry.setDependencies(dependencies);
    }

    protected void processTransaction(Properties props) {
        try {
            // Create a new tree type with the provided properties
            newTreeType = new TreeType(props);

            // Save to database
            newTreeType.save();

            // Create success message
            String treeTypeId = (String)newTreeType.getState("ID");
            String treeTypeDesc = props.getProperty("TypeDescription");
            transactionSuccessMessage = "Successfully added tree type " + treeTypeDesc + " (ID: " + treeTypeId + ") to the database";

            // Show success notification with Done button
            showSuccessNotification(treeTypeDesc, treeTypeId);

        } catch (Exception e) {
            // Handle any errors
            transactionErrorMessage = "ERROR: Failed to add tree type - " + e.getMessage();
            myRegistry.updateSubscribers("TransactionError", this);
        }
    }

    /**
     * Display a success notification with a Done button
     */
    private void showSuccessNotification(String treeTypeDesc, String treeTypeId) {
        // Use Platform.runLater to ensure this runs on the JavaFX thread
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Tree Type Added");
            alert.setHeaderText("Tree Type Added Successfully");
            alert.setContentText("Tree Type " + treeTypeDesc + " (ID: " + treeTypeId + ") has been successfully added to the database.");

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

    @Override
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
        } else if (key.equals("ProcessTreeTypeTransaction")) {
            processTransaction((Properties) value);
        } else if (key.equals("CancelAddTreeTypeTransaction")) {
            myRegistry.updateSubscribers("CancelTransaction", this);
        }

        myRegistry.updateSubscribers(key, this);
    }

    protected Scene createView() {
        Scene currentScene = myViews.get("AddTreeTypeView");

        if (currentScene == null) {
            View newView = ViewFactory.createView("AddTreeTypeView", this);
            currentScene = new Scene(newView);
            myViews.put("AddTreeTypeView", currentScene);

            return currentScene;
        } else {
            return currentScene;
        }
    }
    
}
