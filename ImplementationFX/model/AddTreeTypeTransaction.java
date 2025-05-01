package model;

import java.util.Hashtable;
import java.util.Properties;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import impresario.IModel;
import impresario.IView;
import event.Event;
import jdk.jfr.BooleanFlag;
import userinterface.MainStageContainer;
import userinterface.View;
import userinterface.ViewFactory;
import userinterface.WindowPosition;

public class AddTreeTypeTransaction extends Transaction{
    private Properties dependencies;
    private Hashtable<String, Scene> myViews;
    private Stage myStage;
    private String transactionErrorMessage = "";
    private String transactionSuccessMessage = "";

    public AddTreeTypeTransaction(){
        super();
        myViews = new Hashtable<>();
        myStage = MainStageContainer.getInstance();
        setDependencies();
    }

    @Override
    protected void setDependencies() {
        dependencies = new Properties();
        dependencies.setProperty("TransactionStatusMessage", "");
        myRegistry.setDependencies(dependencies);
    }

    @Override
    protected Scene createView() {
        View newView = ViewFactory.createView("AddTreeTypeView", this);
        Scene currentScene = new Scene(newView);
        myViews.put("AddTreeTypeView", currentScene);
        return currentScene;
    }

    @Override
    public void stateChangeRequest(String key, Object value) {
        if ("DoYourJob".equals(key)) {
            doYourJob();
        } else if ("CancelTransaction".equals(key)) {
            myRegistry.updateSubscribers("CancelTransaction", this);
        } else if ("AddTreeTypeTransaction".equals(key)) {
            processTransaction((Properties) value);
        }
    }

    @Override
    public void doYourJob() {
        Scene newScene = createView();
        swapToView(newScene);
    }

    protected void processTransaction(Properties treeTypeData) {
        try {
            Properties dbProps = new Properties();

            dbProps.setProperty("ID", treeTypeData.getProperty("ID"));
            dbProps.setProperty("TypeDescription", treeTypeData.getProperty("TypeDescription"));
            dbProps.setProperty("Cost", treeTypeData.getProperty("Cost"));
            dbProps.setProperty("BarcodePrefix", treeTypeData.getProperty("BarcodePrefix"));
            TreeType newTreeType = new TreeType(dbProps);
            newTreeType.save();

            transactionSuccessMessage = "Tree Type" + dbProps.getProperty("ID") + " has been successfully added!";
            showSuccessNotification(dbProps.getProperty("ID"));

        } catch (Exception e) {
            transactionErrorMessage = "ERROR: Failed to add tree type - " + e.getMessage();
            myRegistry.updateSubscribers("TransactionStatusMessage", this);
        }
    }

    private void showSuccessNotification(String id) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Tree Type Added");
            alert.setHeaderText("Tree Type Added Successfully");
            alert.setContentText("Tree Type " + id + " has been successfully added to the database.");

            ButtonType doneButton = new ButtonType("Done");
            alert.getButtonTypes().setAll(doneButton);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == doneButton) {
                myRegistry.updateSubscribers("TransactionStatusMessage", this);
                myRegistry.updateSubscribers("CancelTransaction", this);
            }
        });
    }

    public void swapToView(Scene newScene) {
        if (newScene == null) {
            System.out.println("AddTreeTypeTransaction.swapToView(): Missing view for display");
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
        } else if ("TransactionStatusMessage".equals(key)) {
            if (!transactionSuccessMessage.isEmpty()) {
                return transactionSuccessMessage;
            } else if (!transactionErrorMessage.isEmpty()) {
                return transactionErrorMessage;
            }
            return "";
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
