package model.TreeModels;

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
import model.Transaction;
import userinterface.TreeViews.RemoveTreeView;
import userinterface.View;
import userinterface.ViewFactory;

public class RemoveTreeTransaction extends Transaction {

    private TreeCollection treeCollection;
    private Tree treeToRemove;
    private String transactionErrorMessage = "";
    private String transactionSuccessMessage = "";

    public RemoveTreeTransaction() throws Exception {
        super();
    }

    @Override
    protected void setDependencies() {
        dependencies = new Properties();
        dependencies.setProperty("TransactionStatusMessage", "");
        myRegistry.setDependencies(dependencies);
    }

    @Override
    protected Scene createView() {
        Scene currentScene = myViews.get("TreeSearchView");

        if (currentScene == null) {
            View newView = ViewFactory.createView("TreeSearchView", this);
            currentScene = new Scene(newView);
            myViews.put("TreeSearchView", currentScene);
        }

        return currentScene;
    }

    @Override
    public void stateChangeRequest(String key, Object value) {
        if (key.equals("DoYourJob")) {
            doYourJob();
        } else if (key.equals("SearchTrees")) {
            searchTrees((String) value);
        } else if (key.equals("TreeSelected")) {
            String barcode = (String) value;
            treeSelected(barcode);
        } else if (key.equals("RemoveTree")) {
            processRemoval();
        } else if (key.equals("CancelRemoval")) {
            createAndShowTreeCollectionView();
        } else if (key.equals("CancelTreeList")) {
            createAndShowTreeSearchView();
        } else if (key.equals("CancelSearch")) {
            myRegistry.updateSubscribers("CancelTransaction", this);
        }
    }

    private void searchTrees(String barcode) {
        try {
            treeCollection = new TreeCollection();
            treeCollection.findTreesWithBarcodeLike(barcode);
            createAndShowTreeCollectionView();
        } catch (Exception e) {
            transactionErrorMessage = "ERROR: Could not complete tree search: " + e.getMessage();
            new Event(Event.getLeafLevelClassName(this), "searchTrees",
                    "Error searching for trees: " + e.getMessage(), Event.ERROR);
            myRegistry.updateSubscribers("TransactionStatusMessage", this);
        }
    }

    private void treeSelected(String barcode) {
        try {
            treeToRemove = new Tree(barcode);
            createAndShowRemoveTreeView();
        } catch (InvalidPrimaryKeyException e) {
            transactionErrorMessage = "ERROR: Tree with barcode " + barcode + " not found.";
            myRegistry.updateSubscribers("TransactionStatusMessage", this);
        }
    }

    private void processRemoval() {
        try {
            String barcode = (String)treeToRemove.getState("Barcode");
            String treeType = (String)treeToRemove.getState("TreeType");

            treeToRemove.setInactive();

            transactionSuccessMessage = "Tree " + barcode + " (Type: " + treeType + ") has been successfully removed!";
            showSuccessNotification(barcode, treeType);

        } catch (SQLException e) {
            transactionErrorMessage = "ERROR: Database error while removing tree: " + e.getMessage();
            myRegistry.updateSubscribers("TransactionStatusMessage", this);
        }
    }

    private void showSuccessNotification(String barcode, String treeType) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Removal Successful");
            alert.setHeaderText("Tree Removed Successfully");
            alert.setContentText("Tree " + barcode + " (Type: " + treeType + ") has been successfully removed from the database.");

            ButtonType doneButton = new ButtonType("Done");
            alert.getButtonTypes().setAll(doneButton);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == doneButton) {
                myRegistry.updateSubscribers("TransactionStatusMessage", this);
                myRegistry.updateSubscribers("CancelTransaction", this);
            }
        });
    }

    private void createAndShowTreeSearchView() {
        Scene currentScene = myViews.get("TreeSearchView");

        if (currentScene == null) {
            View newView = ViewFactory.createView("TreeSearchView", this);
            currentScene = new Scene(newView);
            myViews.put("TreeSearchView", currentScene);
        }

        swapToView(currentScene);
    }

    private void createAndShowTreeCollectionView() {
        myViews.remove("TreeCollectionView");

        Scene currentScene = myViews.get("TreeCollectionView");

        if (currentScene == null) {
            View newView = ViewFactory.createView("TreeCollectionView", this);
            currentScene = new Scene(newView);
            myViews.put("TreeCollectionView", currentScene);
        }

        swapToView(currentScene);
    }

    private void createAndShowRemoveTreeView() {
        Scene currentScene = myViews.get("RemoveTreeView");

        if (currentScene == null) {
            Properties treeProps = new Properties();
            treeProps.setProperty("barcode", (String)treeToRemove.getState("Barcode"));
            treeProps.setProperty("treeType", (String)treeToRemove.getState("TreeType"));
            treeProps.setProperty("notes", (String)treeToRemove.getState("Notes"));
            treeProps.setProperty("status", (String)treeToRemove.getState("Status"));
            treeProps.setProperty("dateStatusUpdated", (String)treeToRemove.getState("DateStatusUpdated"));

            View newView = new RemoveTreeView(this, treeProps);
            currentScene = new Scene(newView);
            myViews.put("RemoveTreeView", currentScene);
        }

        swapToView(currentScene);
    }

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
        } else if (key.equals("TreeList")) {
            return treeCollection;
        } else if (key.equals("Tree") && treeToRemove != null) {
            return treeToRemove;
        } else if (key.equals("Trees") && treeCollection != null) {
            return treeCollection.getState("Trees");
        }
        return null;
    }
}
