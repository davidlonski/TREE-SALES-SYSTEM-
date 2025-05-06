package model.TreeModels;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Optional;
import java.util.Properties;

import exception.InvalidPrimaryKeyException;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import event.Event;
import model.Transaction;
import model.TreeTypeModels.TreeType;
import userinterface.TreeViews.ModifyTreeView;
import userinterface.View;
import userinterface.ViewFactory;

public class ModifyTreeTransaction extends Transaction {

    private Properties dependencies;
    private Hashtable<String, Scene> myViews;
    private Stage myStage;
    private String transactionErrorMessage = "";
    private String transactionSuccessMessage = "";
    private TreeCollection treeCollection;
    private Tree treeToModify;

    public ModifyTreeTransaction() {
        super();
        myViews = new Hashtable<>();
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
        } else if (key.equals("ModifyTreeData")) {
            processModification((Properties) value);
        } else if (key.equals("CancelModifyTree")) {
            createAndShowTreeCollectionView();
        } else if (key.equals("CancelTreeList")) {
            createAndShowTreeSearchView();
        } else if (key.equals("CancelSearch")) {
            myRegistry.updateSubscribers("CancelTransaction", this);
        }
    }

    @Override
    public void doYourJob() {
        try {
            Scene scene = createView();
            swapToView(scene);
        } catch (Exception e) {
            transactionErrorMessage = "ERROR: Could not open search view: " + e.getMessage();
            myRegistry.updateSubscribers("TransactionStatusMessage", this);
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
            treeToModify = new Tree(barcode);
            createAndShowModifyTreeView();
        } catch (InvalidPrimaryKeyException e) {
            transactionErrorMessage = "ERROR: Tree with barcode " + barcode + " not found.";
            myRegistry.updateSubscribers("TransactionStatusMessage", this);
        }
    }

    private void processModification(Properties props) {
        try {
            treeToModify.setState("TreeType", props.getProperty("TreeType"));
            treeToModify.setState("Notes", props.getProperty("Notes"));
            treeToModify.setState("Status", props.getProperty("Status"));

            String oldStatus = (String) treeToModify.getState("Status");
            String newStatus = props.getProperty("Status");
            if (newStatus != null && !newStatus.equals(oldStatus)) {
                String today = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
                treeToModify.setState("DateStatusUpdated", today);
            } else if (props.getProperty("DateStatusUpdated") != null) {
                treeToModify.setState("DateStatusUpdated", props.getProperty("DateStatusUpdated"));
            }

            treeToModify.save();

            transactionSuccessMessage = "Tree " + treeToModify.getState("Barcode") + " has been successfully updated!";
            showSuccessNotification((String) treeToModify.getState("Barcode"));

        } catch (Exception e) {
            transactionErrorMessage = "ERROR: Error updating tree: " + e.getMessage();
            myRegistry.updateSubscribers("TransactionStatusMessage", this);
        }
    }

    private void showSuccessNotification(String barcode) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Update Successful");
            alert.setHeaderText("Tree Updated Successfully");
            alert.setContentText("Tree " + barcode + " has been successfully updated!");

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
        Scene currentScene = myViews.get("TreeCollectionView");

        if (currentScene == null) {
            View newView = ViewFactory.createView("TreeCollectionView", this);
            currentScene = new Scene(newView);
            myViews.put("TreeCollectionView", currentScene);
        }

        swapToView(currentScene);
    }

    private void createAndShowModifyTreeView() {
        Scene currentScene = myViews.get("ModifyTreeView");

        if (currentScene == null) {
            Properties treeProps = new Properties();
            treeProps.setProperty("Barcode", (String) treeToModify.getState("Barcode"));

            // ✅ NEW: Resolve TreeType ID ➜ TypeDescription
            String treeTypeId = (String) treeToModify.getState("TreeType");
            try {
                TreeType treeType = new TreeType(treeTypeId);
                String typeDescription = (String) treeType.getState("TypeDescription");
                treeProps.setProperty("TreeType", typeDescription);
            } catch (Exception e) {
                System.err.println("Could not load TreeType description for ID " + treeTypeId + ": " + e.getMessage());
                treeProps.setProperty("TreeType", "Unknown Type");
            }

            treeProps.setProperty("Notes", (String) treeToModify.getState("Notes"));
            treeProps.setProperty("Status", (String) treeToModify.getState("Status"));
            treeProps.setProperty("DateStatusUpdated", (String) treeToModify.getState("DateStatusUpdated"));

            View newView = new ModifyTreeView(this, treeProps);
            currentScene = new Scene(newView);
            myViews.put("ModifyTreeView", currentScene);
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
        } else if (key.equals("Tree") && treeToModify != null) {
            return treeToModify;
        } else if (key.equals("Trees") && treeCollection != null) {
            return treeCollection.getState("Trees");
        }
        return null;
    }
}
