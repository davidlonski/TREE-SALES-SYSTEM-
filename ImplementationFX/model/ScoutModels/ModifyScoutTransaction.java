package model.ScoutModels;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.application.Platform;
import java.util.Properties;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import event.Event;
import exception.InvalidPrimaryKeyException;
import model.Transaction;
import userinterface.ScoutViews.ModifyScoutView;
import userinterface.View;
import userinterface.ViewFactory;

public class ModifyScoutTransaction extends Transaction {

    private ScoutCollection scoutCollection;
    private Scout scoutToModify;
    private String transactionErrorMessage = "";
    private String transactionSuccessMessage = "";

    /**
     * Constructor for this class.
     */
    public ModifyScoutTransaction() {
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
        System.out.println("ModifyScoutTransaction: Received key = " + key);

        if (key.equals("DoYourJob")) {
            doYourJob();
        } else if (key.equals("SearchScouts")) {
            searchScouts((String) value);
        } else if (key.equals("ScoutSelected")) {
            String scoutId = (String) value;
            scoutSelected(scoutId);
        } else if (key.equals("ModifyScoutData")) {
            processModification((Properties) value);
        } else if (key.equals("CancelModifyScout")) {
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
            System.out.println("ModifyScoutTransaction: Scout ID selected = " + scoutId);
            scoutToModify = new Scout(scoutId);
            createAndShowModifyScoutView();
        } catch (InvalidPrimaryKeyException e) {
            transactionErrorMessage = "ERROR: Scout with ID " + scoutId + " not found.";
            myRegistry.updateSubscribers("TransactionStatusMessage", this);
        }
    }

    /**
     * Process the modification of the selected scout
     */
    private void processModification(Properties props) {
        try {
            System.out.println("DEBUG: Processing scout modification with properties: " + props);

            // Get the scout ID for messages
            String scoutId = (String)scoutToModify.getState("ID");
            System.out.println("DEBUG: Modifying scout with ID: " + scoutId);

            // Map form field names to database field names
            // This is critical if the field names in the form don't match the database field names
            Properties dbFieldProps = new Properties();
            dbFieldProps.setProperty("ID", scoutId); // Preserve the ID

            // Map the form field names to database field names
            if (props.getProperty("firstName") != null)
                dbFieldProps.setProperty("FirstName", props.getProperty("firstName"));

            if (props.getProperty("middleName") != null)
                dbFieldProps.setProperty("MiddleName", props.getProperty("middleName"));

            if (props.getProperty("lastName") != null)
                dbFieldProps.setProperty("LastName", props.getProperty("lastName"));

            if (props.getProperty("dateOfBirth") != null)
                dbFieldProps.setProperty("DateOfBirth", props.getProperty("dateOfBirth"));

            if (props.getProperty("phoneNumber") != null)
                dbFieldProps.setProperty("PhoneNumber", props.getProperty("phoneNumber"));

            if (props.getProperty("email") != null)
                dbFieldProps.setProperty("Email", props.getProperty("email"));

            if (props.getProperty("troopID") != null)
                dbFieldProps.setProperty("TroopID", props.getProperty("troopID"));

            if (props.getProperty("status") != null)
                dbFieldProps.setProperty("Status", props.getProperty("status"));

            // Update DateStatusUpdated if status changed
            String oldStatus = (String)scoutToModify.getState("Status");
            String newStatus = props.getProperty("status");
            if (newStatus != null && !newStatus.equals(oldStatus)) {
                String today = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
                dbFieldProps.setProperty("DateStatusUpdated", today);
                System.out.println("DEBUG: Status changed, updating DateStatusUpdated to " + today);
            } else if (props.getProperty("datestatus") != null) {
                dbFieldProps.setProperty("DateStatusUpdated", props.getProperty("datestatus"));
            }

            // Create a new Scout with the modified properties
            Scout updatedScout = new Scout(dbFieldProps);

            // Display the scout before and after modification for debugging
            System.out.println("DEBUG: Before modification:");
            scoutToModify.display();
            System.out.println("DEBUG: After modification:");
            updatedScout.display();

            // Save the changes to the database
            updatedScout.save();

            // Create success message
            String scoutName = dbFieldProps.getProperty("FirstName") + " " +
                    dbFieldProps.getProperty("LastName");
            transactionSuccessMessage = "Scout " + scoutName + " (ID: " + scoutId + ") has been successfully updated!";
            System.out.println("DEBUG: " + transactionSuccessMessage);

            // Show success notification with Done button
            showSuccessNotification(scoutName, scoutId);

        } catch (Exception e) {
            e.printStackTrace();
            transactionErrorMessage = "ERROR: Error updating scout: " + e.getMessage();
            System.out.println("DEBUG: " + transactionErrorMessage);
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
            alert.setTitle("Update Successful");
            alert.setHeaderText("Scout Updated Successfully");
            alert.setContentText("Scout " + scoutName + " (ID: " + scoutId + ") has been successfully updated!");

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
        myViews.remove("ScoutCollectionView");

        Scene currentScene = myViews.get("ScoutCollectionView");

        if (currentScene == null) {
            View newView = ViewFactory.createView("ScoutCollectionView", this);
            currentScene = new Scene(newView);
            myViews.put("ScoutCollectionView", currentScene);
        }

        swapToView(currentScene);
    }

    /**
     * Create and show the modify scout view
     */
    private void createAndShowModifyScoutView() {
        Scene currentScene = myViews.get("ModifyScoutView");

        if (currentScene == null) {
            // Convert Scout data to Properties to pass to the ModifyScoutView
            Properties scoutProps = new Properties();
            scoutProps.setProperty("scoutID", (String)scoutToModify.getState("ID"));
            scoutProps.setProperty("firstName", (String)scoutToModify.getState("FirstName"));
            scoutProps.setProperty("lastName", (String)scoutToModify.getState("LastName"));
            scoutProps.setProperty("middleName", (String)scoutToModify.getState("MiddleName"));
            scoutProps.setProperty("dateOfBirth", (String)scoutToModify.getState("DateOfBirth"));
            scoutProps.setProperty("phoneNumber", (String)scoutToModify.getState("PhoneNumber"));
            scoutProps.setProperty("email", (String)scoutToModify.getState("Email"));
            scoutProps.setProperty("troopID", (String)scoutToModify.getState("TroopID"));
            scoutProps.setProperty("status", (String)scoutToModify.getState("Status"));
            scoutProps.setProperty("datestatus", (String)scoutToModify.getState("DateStatusUpdated"));

            View newView = new ModifyScoutView(this, scoutProps);
            currentScene = new Scene(newView);
            myViews.put("ModifyScoutView", currentScene);
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
        } else if (key.equals("Scout") && scoutToModify != null) {
            return scoutToModify;
        } else if (key.equals("Scouts") && scoutCollection != null) {
            return scoutCollection.getState("Scouts");
        }
        return null;
    }
}