package model;

import java.util.Hashtable;
import java.util.Properties;

import javafx.scene.Scene;
import javafx.stage.Stage;

import impresario.IModel;
import impresario.IView;
import event.Event;
import userinterface.MainStageContainer;
import userinterface.View;
import userinterface.ViewFactory;
import userinterface.WindowPosition;

public class AddTreeTransaction extends Transaction {

    private Properties dependencies;
    private Hashtable<String, Scene> myViews;
    private Stage myStage;
    private String transactionErrorMessage = "";
    private String successMessage = "";

    public AddTreeTransaction() {
        super();

        myStage = MainStageContainer.getInstance();
        myViews = new Hashtable<>();

        setDependencies();
    }

    @Override
    protected void setDependencies() {
        dependencies = new Properties();
        myRegistry.setDependencies(dependencies);
    }

    @Override
    protected Scene createView() {
        View newView = ViewFactory.createView("AddTreeView", this); // Must be "AddTreeView"!
        Scene currentScene = new Scene(newView);
        myViews.put("AddTreeView", currentScene);
        return currentScene;
    }

    @Override
    public void stateChangeRequest(String key, Object value) {
        if ("DoYourJob".equals(key)) {
            doYourJob();
        } else if ("CancelTransaction".equals(key)) {
            // Optional - can return to main menu if you want
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
            System.out.println("AddTreeTransaction.swapToView(): Missing view for display");
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
