package model;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.application.Platform;
import java.util.Properties;
import java.sql.SQLException;
import java.util.Optional;

import event.Event;
import exception.InvalidPrimaryKeyException;
import userinterface.View;
import userinterface.ViewFactory;

public class RemoveScoutTransaction extends Transaction {

    private ScoutCollection scoutCollection;
    private Scout scoutToRemove;
    private String transactionErrorMessage = "";
    private String transactionSuccessMessage = "";

    /**
     * Constructor for this class.
     */
    public RemoveScoutTransaction() throws Exception {
        super();
    }

    /**
     * Set dependencies for this transaction
     */
    @Override
    protected void setDependencies() {
        dependencies = new Properties();
        dependencies.setProperty("TransactionStatusMessage", "");
        myRegistry.setDependencies(dependencies);
    }

    /**
     * Create the view for this transaction
     */
    @Override
    protected Scene createView() {
        Scene currentScene = myViews.get("ScoutSearchView");

        if (currentScene == null) {
            // Create the initial search view
            View newView = ViewFactory.createView("ScoutSearchView", this);
            currentScene = new Scene(newView);
            myViews.put("ScoutSearchView", currentScene);
        }

        return currentScene;
    }

    /**
     * Process events generated from views
     */
    @Override
    public void stateChangeRequest(String key, Object value) {
        if (key.equals("DoYourJob")) {
            doYourJob();
        } else if (key.equals("SearchScouts")) {
            searchScouts((String) value);
        } else if (key.equals("ScoutSelected")) {
            String scoutId = (String) value;
            scoutSelected(scoutId);
        } else if (key.equals("RemoveScout")) {
            processRemoval();
        } else if (key.equals("CancelRemoval")) {
            // Go back to scout collection view
            createAndShowScoutCollectionView();
        } else if (key.equals("CancelScoutList")) {
            // Go back to search view
            createAndShowScoutSearchView();
        } else if (key.equals("CancelSearch")) {
            // Cancel the entire transaction
            myRegistry.updateSubscribers("CancelTransaction", this);
        }
    }

    /**
     * Search for scouts with the given last name
     */
    private void searchScouts(String lastName) {
        try {
            scoutCollection = new ScoutCollection();
            scoutCollection.findScoutsWithLastNameLike(lastName);
            createAndShowScoutCollectionView();
        } catch (Exception e) {
            transactionErrorMessage = "ERROR: Could not complete scout search: " + e.getMessage();
            new Event(Event.getLeafLevelClassName(this), "searchScouts",
                    "Error searching for scouts: " + e.getMessage(), Event.ERROR);
            myRegistry.updateSubscribers("TransactionStatusMessage", this);
        }
    }

    /**
     * Handle when a scout is selected from the collection
     */
    private void scoutSelected(String scoutId) {
        try {
            System.out.println("DEBUG: RemoveScoutTransaction - Scout selected with ID: " + scoutId);
            scoutToRemove = new Scout(scoutId);
            createAndShowRemoveScoutView();
        } catch (InvalidPrimaryKeyException e) {
            transactionErrorMessage = "ERROR: Scout with ID " + scoutId + " not found.";
            myRegistry.updateSubscribers("TransactionStatusMessage", this);
        }
    }

    /**
     * Process the removal of the selected scout
     */
    private void processRemoval() {
        try {
            // Get the ID for status messages
            String scoutId = (String)scoutToRemove.getState("ID");
            String scoutName = (String)scoutToRemove.getState("FirstName") + " " +
                    (String)scoutToRemove.getState("LastName");

            // Delete scout using the Scout's method
            scoutToRemove.setInactive();

            // Show success message
            transactionSuccessMessage = "Scout " + scoutName + " (ID: " + scoutId + ") has been successfully removed!";

            // Show success notification with Done button
            showSuccessNotification(scoutName, scoutId);

        } catch (SQLException e) {
            transactionErrorMessage = "ERROR: Database error while removing scout: " + e.getMessage();
            myRegistry.updateSubscribers("TransactionStatusMessage", this);
        }
    }

    /**
     * Display a success notification with a Done button
     */
    private void showSuccessNotification(String scoutName, String scoutId) {
        // Use Platform.runLater to ensure this runs on the JavaFX thread
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Removal Successful");
            alert.setHeaderText("Scout Removed Successfully");
            alert.setContentText("Scout " + scoutName + " (ID: " + scoutId + ") has been successfully removed from the database.");

            // Replace OK button with a Done button
            ButtonType doneButton = new ButtonType("Done");
            alert.getButtonTypes().setAll(doneButton);

            // Show the alert and wait for response
            Optional<ButtonType> result = alert.showAndWait();

            // When Done button is clicked, return to main interface
            if (result.isPresent() && result.get() == doneButton) {
                // Return to main menu
                myRegistry.updateSubscribers("TransactionStatusMessage", this);
                myRegistry.updateSubscribers("CancelTransaction", this);
            }
        });
    }

    /**
     * Create and show the scout search view
     */
    private void createAndShowScoutSearchView() {
        Scene currentScene = myViews.get("ScoutSearchView");

        if (currentScene == null) {
            View newView = ViewFactory.createView("ScoutSearchView", this);
            currentScene = new Scene(newView);
            myViews.put("ScoutSearchView", currentScene);
        }

        swapToView(currentScene);
    }

    /**
     * Create and show the scout collection view
     */
    private void createAndShowScoutCollectionView() {
        Scene currentScene = myViews.get("ScoutCollectionView");

        if (currentScene == null) {
            View newView = ViewFactory.createView("ScoutCollectionView", this);
            currentScene = new Scene(newView);
            myViews.put("ScoutCollectionView", currentScene);
        }

        swapToView(currentScene);
    }

    /**
     * Create and show the remove scout confirmation view
     */
    private void createAndShowRemoveScoutView() {
        Scene currentScene = myViews.get("RemoveScoutView");

        if (currentScene == null) {
            // Convert Scout data to Properties to pass to the RemoveScoutView
            Properties scoutProps = new Properties();
            scoutProps.setProperty("scoutID", (String)scoutToRemove.getState("ID"));
            scoutProps.setProperty("firstName", (String)scoutToRemove.getState("FirstName"));
            scoutProps.setProperty("lastName", (String)scoutToRemove.getState("LastName"));
            scoutProps.setProperty("middleName", (String)scoutToRemove.getState("MiddleName"));
            scoutProps.setProperty("dateOfBirth", (String)scoutToRemove.getState("DateOfBirth"));
            scoutProps.setProperty("phoneNumber", (String)scoutToRemove.getState("PhoneNumber"));
            scoutProps.setProperty("email", (String)scoutToRemove.getState("Email"));
            scoutProps.setProperty("troopID", (String)scoutToRemove.getState("TroopID"));
            scoutProps.setProperty("status", (String)scoutToRemove.getState("Status"));

            View newView = new userinterface.RemoveScoutView(this, scoutProps);
            currentScene = new Scene(newView);
            myViews.put("RemoveScoutView", currentScene);
        }

        swapToView(currentScene);
    }

    /**
     * Return the object state for a given key
     */
    @Override
    public Object getState(String key) {
        if (key.equals("TransactionErrorMessage")) {
            return transactionErrorMessage;
        } else if (key.equals("TransactionStatusMessage")) {
            if (!transactionSuccessMessage.isEmpty()) {
                return transactionSuccessMessage;
            } else if (!transactionErrorMessage.isEmpty()) {
                return transactionErrorMessage;
            }
            return "";
        } else if (key.equals("ScoutList")) {
            return scoutCollection;
        } else if (key.equals("Scout") && scoutToRemove != null) {
            return scoutToRemove;
        } else if (key.equals("Scouts") && scoutCollection != null) {
            return scoutCollection.getState("Scouts");
        }
        return null;
    }
}