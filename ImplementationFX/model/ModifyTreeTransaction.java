package model;

import java.util.Hashtable;
import java.util.Optional;
import java.util.Properties;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import event.Event;
import impresario.IModel;
import impresario.IView;
import userinterface.MainStageContainer;
import userinterface.View;
import userinterface.ViewFactory;
import userinterface.WindowPosition;

public class ModifyTreeTransaction extends Transaction {

    private Properties dependencies;
    private Hashtable<String, Scene> myViews;
    private Stage myStage;
    private String transactionErrorMessage = "";
    private String successMessage = "";
    private Tree treeToModify;

    public ModifyTreeTransaction() {
        super();

        myStage = MainStageContainer.getInstance();
        myViews = new Hashtable<>();

        setDependencies();
    }

    @Override
    protected void setDependencies() {
        dependencies = new Properties();
        dependencies.setProperty("TransactionErrorMessage", "");
        dependencies.setProperty("SuccessMessage", "");
        myRegistry.setDependencies(dependencies);
    }

    private void processTransaction(Properties props) {
        try {
            String treeId = props.getProperty("TreeID");
            treeToModify = new Tree(treeId);

            treeToModify.setState("Name", props.getProperty("Name"));
            treeToModify.setState("Location", props.getProperty("Location"));

            treeToModify.save();

            successMessage = "Successfully modified Tree (ID: " + treeId + ")";
            showSuccessNotification(treeId);

        } catch (Exception e) {
            transactionErrorMessage = "ERROR: Could not modify tree - " + e.getMessage();
            myRegistry.updateSubscribers("TransactionErrorMessage", this);
        }
    }

    private void showSuccessNotification(String treeId) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Tree Modified");
            alert.setHeaderText("Tree Modified Successfully");
            alert.setContentText("Tree (ID: " + treeId + ") has been modified.");

            ButtonType doneButton = new ButtonType("Done");
            alert.getButtonTypes().setAll(doneButton);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == doneButton) {
                myRegistry.updateSubscribers("SuccessMessage", this);
                myRegistry.updateSubscribers("CancelTransaction", this); // Return to main
            }
        });
    }

    @Override
    protected Scene createView() {
        Scene currentScene = myViews.get("ModifyTreeView");

        if (currentScene == null) {
            View newView = ViewFactory.createView("ModifyTreeView", this);
            currentScene = new Scene(newView);
            myViews.put("ModifyTreeView", currentScene);
        }

        return currentScene;
    }

    @Override
    public void stateChangeRequest(String key, Object value) {
        if ("DoYourJob".equals(key)) {
            doYourJob();
        } else if ("ProcessTreeTransaction".equals(key)) {
            processTransaction((Properties) value);
        } else if ("CancelTransaction".equals(key)) {
            myRegistry.updateSubscribers("CancelTransaction", this);
        }

        myRegistry.updateSubscribers(key, this);
    }

    @Override
    public void doYourJob() {
        Scene newScene = createView();
        swapToView(newScene);
    }

    public void swapToView(Scene newScene) {
        if (newScene == null) {
            System.out.println("ModifyTreeTransaction.swapToView(): Missing view for display");
            new Event(Event.getLeafLevelClassName(this), "swapToView",
                    "Missing view for display ", Event.ERROR);
            return;
        }

        myStage.setScene(newScene);
        myStage.sizeToScene();
        WindowPosition.placeCenter(myStage);
    }

    @Override
    public Object getState(String key) {
        if ("TransactionErrorMessage".equals(key)) {
            return transactionErrorMessage;
        }
        if ("SuccessMessage".equals(key)) {
            return successMessage;
        }
        return null;
    }

    @Override
    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }

    @Override
    public void subscribe(String key, IView subscriber) {
        myRegistry.subscribe(key, subscriber);
    }

    @Override
    public void unSubscribe(String key, IView subscriber) {
        myRegistry.unSubscribe(key, subscriber);
    }
}
