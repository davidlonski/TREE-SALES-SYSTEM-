package model;

// system imports
import java.util.Hashtable;
import java.util.Properties;

import javafx.stage.Stage;
import javafx.scene.Scene;

// project imports
import impresario.IModel;
import impresario.IView;
import impresario.ModelRegistry;

import event.Event;
import userinterface.MainStageContainer;
import userinterface.View;
import userinterface.ViewFactory;
import userinterface.WindowPosition;

public class TreeLotCoordinator implements IView, IModel {
    // For Impresario
    private Properties dependencies;
    private ModelRegistry myRegistry;

    // GUI Components
    private Hashtable<String, Scene> myViews;
    private Stage myStage;

    // Initialize Message Strings for observer pattern
    private String closeStage;


    private String transactionErrorMessage = "";

    public TreeLotCoordinator() {
        myStage = MainStageContainer.getInstance();
        myViews = new Hashtable<>();

        myRegistry = new ModelRegistry("TreeLotCoordinator");

        if (myRegistry == null) {
            new Event(Event.getLeafLevelClassName(this), "TreeLotCoordinator",
                    "Could not instantiate Registry", Event.ERROR);
        }

        setDependencies();

        createAndShowView("TransactionChoiceView");
    }

    // Dependencies between model and view subscribers
    //------------------------------------------------------------------    
    private void setDependencies() {
        dependencies = new Properties();

        dependencies.setProperty("AddScout", "TransactionError");
        dependencies.setProperty("RemoveScout", "TransactionError");
        dependencies.setProperty("ModifyScout", "TransactionError");
        dependencies.setProperty("AddTree", "TransactionError");

        myRegistry.setDependencies(dependencies);
    }

    //------------------------------------------------------------------    
    private void createAndShowView(String viewName) {
        Scene currentScene = myViews.get(viewName);

        if (currentScene == null) {
            View newView = ViewFactory.createView(viewName, this);
            currentScene = new Scene(newView);
            myViews.put(viewName, currentScene);
        }

        swapToView(currentScene);
    }

    //------------------------------------------------------------------ 
    @Override
    public void stateChangeRequest(String key, Object value) {
        if(key.equals("done")) {
            myStage.close();
        }else if (key.equals("AddScoutTransaction")
                || key.equals("ModifyScoutTransaction")
                || key.equals("RemoveScoutTransaction")
                || key.equals("AddTreeTransaction")){
            doTransaction(key);
        }else if (key.equals("CancelTransaction")){
            createAndShowView("TransactionChoiceView");
        }
        myRegistry.updateSubscribers(key, this);
    }

    //------------------------------------------------------------------ 
    public void doTransaction(String transactionType) {
        try{
            Transaction trans = TransactionFactory.createTransaction(transactionType);
            trans.subscribe("CancelTransaction", this);
            trans.stateChangeRequest("DoYourJob", "");
        }catch (Exception ex){
            transactionErrorMessage = "FATAL ERROR: TRANSACTION FAILURE: Unrecognized transaction!!";
            new Event(Event.getLeafLevelClassName(this), "createTransaction",
                    "Transaction Creation Failure: Unrecognized transaction " + ex.toString(),
                    Event.ERROR);
        }
    }


    //------------------------------------------------------------------ 
    @Override
    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }

    // Swaps the current scene in the stage
    //------------------------------------------------------------------ 
    public void swapToView(Scene newScene) {
        if (newScene == null) {
            System.err.println("TreeLotCoordinator.swapToView(): Missing view for display");
            new Event(Event.getLeafLevelClassName(this), "swapToView",
                    "Missing view for display ", Event.ERROR);
            return;
        }

        myStage.setScene(newScene);
        myStage.sizeToScene();
        WindowPosition.placeCenter(myStage);
    }

    // IModel Implementation
    //------------------------------------------------------------------ 
    @Override
    public Object getState(String key) {
        if(key.equals("TransactionError")) {
            return transactionErrorMessage;
        }else{
            return "";
        }
    }

    //------------------------------------------------------------------ 
    @Override
    public void subscribe(String key, IView subscriber) {
        myRegistry.subscribe(key, subscriber);
    }

    //------------------------------------------------------------------ 
    @Override
    public void unSubscribe(String key, IView subscriber) {
        myRegistry.unSubscribe(key, subscriber);
    }

}
