package model.TreeTypeModels;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.application.Platform;
import java.util.Properties;
import java.util.Optional;

import event.Event;
import exception.InvalidPrimaryKeyException;
import userinterface.View;
import userinterface.ViewFactory;
import model.*;

public class ModifyTreeTypeTransaction extends Transaction {

    private TreeTypeCollection treeTypeCollection;
    private TreeType treeTypeToModify;
    private String transactionErrorMessage = "";
    private String transactionSuccessMessage = "";

    /**
     * Constructor for this class.
     */
    public ModifyTreeTypeTransaction() throws Exception {
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
        Scene currentScene = myViews.get("TreeTypeSearchView");

        if (currentScene == null) {
            // Create the initial search view
            View newView = ViewFactory.createView("TreeTypeSearchView", this);
            currentScene = new Scene(newView);
            myViews.put("TreeTypeSearchView", currentScene);
        }

        return currentScene;
    }

    /**
     * Process events generated from views
     */
    @Override
    public void stateChangeRequest(String key, Object value) {
        System.out.println("ModifyTreeTypeTransaction: Received key = " + key);

        if (key.equals("DoYourJob")) {
            doYourJob();
        } else if (key.equals("SearchTreeTypes")) {
            searchTreeTypes((String) value);
        } else if (key.equals("TreeTypeSelected")) {
            String treeTypeId = (String) value;
            treeTypeSelected(treeTypeId);
        } else if (key.equals("ModifyTreeTypeData")) {
            processModification((Properties) value);
        } else if (key.equals("CancelModifyTreeType")) {
            // Go back to tree type collection view
            createAndShowTreeTypeCollectionView();
        } else if (key.equals("CancelTreeTypeList")) {
            // Go back to search view
            createAndShowTreeTypeSearchView();
        } else if (key.equals("CancelSearch")) {
            // Cancel the entire transaction
            myRegistry.updateSubscribers("CancelTransaction", this);
        }

        myRegistry.updateSubscribers(key, this);
    }

    /**
     * Search for tree types with the given description
     */
    private void searchTreeTypes(String description) {
        try {
            treeTypeCollection = new TreeTypeCollection();
            treeTypeCollection.findByDescription(description);
            createAndShowTreeTypeCollectionView();
        } catch (Exception e) {
            transactionErrorMessage = "ERROR: Could not complete tree type search: " + e.getMessage();
            new Event(Event.getLeafLevelClassName(this), "searchTreeTypes",
                    "Error searching for tree types: " + e.getMessage(), Event.ERROR);
            myRegistry.updateSubscribers("TransactionStatusMessage", this);
        }
    }

    /**
     * Handle when a tree type is selected from the collection
     */
    private void treeTypeSelected(String treeTypeId) {
        try {
            System.out.println("ModifyTreeTypeTransaction: Tree Type ID selected = " + treeTypeId);
            treeTypeToModify = new TreeType(treeTypeId);
            createAndShowModifyTreeTypeView();
        } catch (InvalidPrimaryKeyException e) {
            transactionErrorMessage = "ERROR: Tree Type with ID " + treeTypeId + " not found.";
            myRegistry.updateSubscribers("TransactionStatusMessage", this);
        }
    }

    /**
     * Process the modification of the selected tree type
     */
    private void processModification(Properties props) {
        try {
            System.out.println("DEBUG: Processing tree type modification with properties: " + props);

            // Get the tree type ID for messages
            String id = (String)treeTypeToModify.getState("ID");
            System.out.println("DEBUG: Modifying tree type with ID: " + id);

            // Validate properties
            String typeDescription = props.getProperty("TypeDescription");
            String cost = props.getProperty("Cost");
            String barcodePrefix = props.getProperty("BarcodePrefix");

            if (typeDescription == null || typeDescription.trim().isEmpty()) {
                transactionErrorMessage = "ERROR: Type Description is required";
                myRegistry.updateSubscribers("TransactionStatusMessage", this);
                return;
            }

            if (cost == null || cost.trim().isEmpty()) {
                transactionErrorMessage = "ERROR: Cost is required";
                myRegistry.updateSubscribers("TransactionStatusMessage", this);
                return;
            }

            try {
                double costValue = Double.parseDouble(cost);
                if (costValue < 0) {
                    transactionErrorMessage = "ERROR: Cost must be a positive number";
                    myRegistry.updateSubscribers("TransactionStatusMessage", this);
                    return;
                }
            } catch (NumberFormatException e) {
                transactionErrorMessage = "ERROR: Cost must be a valid number";
                myRegistry.updateSubscribers("TransactionStatusMessage", this);
                return;
            }

            if (barcodePrefix == null || barcodePrefix.trim().isEmpty()) {
                transactionErrorMessage = "ERROR: Barcode Prefix is required";
                myRegistry.updateSubscribers("TransactionStatusMessage", this);
                return;
            }

            // Make sure barcode prefix is unique (unless it's the same as the current tree type)
            String currentPrefix = (String)treeTypeToModify.getState("BarcodePrefix");
            if (!barcodePrefix.equals(currentPrefix)) {
                TreeTypeCollection typeCollection = new TreeTypeCollection();
                typeCollection.findByBarcodePrefix(barcodePrefix);
                if (typeCollection.hasTreeTypes()) {
                    transactionErrorMessage = "ERROR: Barcode Prefix is already in use";
                    myRegistry.updateSubscribers("TransactionStatusMessage", this);
                    return;
                }
            }

            // Update tree type properties
            treeTypeToModify.updateState("TypeDescription", typeDescription);
            treeTypeToModify.updateState("Cost", cost);
            treeTypeToModify.updateState("BarcodePrefix", barcodePrefix);

            // Save to database
            treeTypeToModify.updateStateInDatabase();

            // Create success message
            transactionSuccessMessage = "Tree Type '" + typeDescription + "' (ID: " + id + ") has been successfully updated!";
            System.out.println("DEBUG: " + transactionSuccessMessage);

            // Show success notification with Done button
            showSuccessNotification(typeDescription, id);

        } catch (Exception e) {
            e.printStackTrace();
            transactionErrorMessage = "ERROR: Error updating tree type: " + e.getMessage();
            System.out.println("DEBUG: " + transactionErrorMessage);
            myRegistry.updateSubscribers("TransactionStatusMessage", this);
        }
    }

    /**
     * Display a success notification with a Done button
     */
    private void showSuccessNotification(String typeDescription, String id) {
        // Use Platform.runLater to ensure this runs on the JavaFX thread
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Update Successful");
            alert.setHeaderText("Tree Type Updated Successfully");
            alert.setContentText("Tree Type '" + typeDescription + "' (ID: " + id + ") has been successfully updated!");

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
     * Create and show the tree type search view
     */
    private void createAndShowTreeTypeSearchView() {

        Scene currentScene = myViews.get("TreeTypeSearchView");

        if (currentScene == null) {
            View newView = ViewFactory.createView("TreeTypeSearchView", this);
            currentScene = new Scene(newView);
            myViews.put("TreeTypeSearchView", currentScene);
        }

        swapToView(currentScene);
    }

    /**
     * Create and show the tree type collection view
     */
    private void createAndShowTreeTypeCollectionView() {
        myViews.remove("TreeTypeCollectionView");

        Scene currentScene = myViews.get("TreeTypeCollectionView");

        if (currentScene == null) {
            View newView = ViewFactory.createView("TreeTypeCollectionView", this);
            currentScene = new Scene(newView);
            myViews.put("TreeTypeCollectionView", currentScene);
        }

        swapToView(currentScene);
    }

    /**
     * Create and show the modify tree type view
     */
    private void createAndShowModifyTreeTypeView() {
        Scene currentScene = myViews.get("ModifyTreeTypeView");

        if (currentScene == null) {
            // Convert TreeType data to Properties to pass to the ModifyTreeTypeView
            Properties treeTypeProps = new Properties();
            treeTypeProps.setProperty("ID", (String)treeTypeToModify.getState("ID"));
            treeTypeProps.setProperty("TypeDescription", (String)treeTypeToModify.getState("TypeDescription"));
            treeTypeProps.setProperty("Cost", (String)treeTypeToModify.getState("Cost"));
            treeTypeProps.setProperty("BarcodePrefix", (String)treeTypeToModify.getState("BarcodePrefix"));

            View newView = new userinterface.TreeTypeViews.ModifyTreeTypeView(this, treeTypeProps);
            currentScene = new Scene(newView);
            myViews.put("ModifyTreeTypeView", currentScene);
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
        } else if (key.equals("TreeTypeList")) {
            return treeTypeCollection;
        } else if (key.equals("TreeType") && treeTypeToModify != null) {
            return treeTypeToModify;
        } else if (key.equals("TreeTypes") && treeTypeCollection != null) {
            return treeTypeCollection.getState("TreeTypes");
        }
        return null;
    }
}
