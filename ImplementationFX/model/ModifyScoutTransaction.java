package model;

import javafx.scene.Scene;
import java.util.Properties;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import event.Event;
import exception.InvalidPrimaryKeyException;
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
    public ModifyScoutTransaction() throws Exception {
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
            // Get the scout ID
            String scoutId = (String)scoutToModify.getState("ID");

            // Update the scout properties
            for (String propertyName : props.stringPropertyNames()) {
                scoutToModify.setState(propertyName, props.getProperty(propertyName));
            }

            // Update the status date if status was changed
            String oldStatus = (String)scoutToModify.getState("Status");
            String newStatus = props.getProperty("status");
            if (newStatus != null && !newStatus.equals(oldStatus)) {
                scoutToModify.setState("DateStatusUpdated",
                        new SimpleDateFormat("MM-dd-yyyy").format(new Date()));
            }

            // Save to database
            scoutToModify.save();

            // Create success message
            String scoutName = (String)scoutToModify.getState("FirstName") + " " +
                    (String)scoutToModify.getState("LastName");
            transactionSuccessMessage = "Scout " + scoutName + " (ID: " + scoutId + ") has been successfully updated!";

            // Go back to transaction choice view
            myRegistry.updateSubscribers("TransactionStatusMessage", this);
            myRegistry.updateSubscribers("CancelTransaction", this);

        } catch (Exception e) {
            transactionErrorMessage = "ERROR: Error updating scout: " + e.getMessage();
            myRegistry.updateSubscribers("TransactionStatusMessage", this);
        }
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

            View newView = new userinterface.ModifyScoutView(this, scoutProps);
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