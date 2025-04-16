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

    public TreeLotCoordinator() {
        myStage = MainStageContainer.getInstance();
        myViews = new Hashtable<>();

        myRegistry = new ModelRegistry("TreeLotCoordinator");

        if (myRegistry == null) {
            new Event(Event.getLeafLevelClassName(this), "TreeLotCoordinator",
                    "Could not instantiate Registry", Event.ERROR);
        }

        setDependencies();
        createAndShowTransactionChoiceView();
    }

    // Dependencies between model and view subscribers
    private void setDependencies() {
        dependencies = new Properties();

        closeStage = "Closing Stage";
        dependencies.setProperty("closeStage", closeStage);

        myRegistry.setDependencies(dependencies);
    }

    // View display logic
    private void createAndShowTransactionChoiceView() {
        Scene currentScene = myViews.get("TransactionChoiceView");

        if (currentScene == null) {
            View newView = ViewFactory.createView("TransactionChoiceView", this);
            currentScene = new Scene(newView);
            myViews.put("TransactionChoiceView", currentScene);
        }

        swapToView(currentScene);
    }

    private void createAndShowAddTreeView() {
        Scene currentScene = myViews.get("AddTreeView");

        if (currentScene == null) {
            View newView = ViewFactory.createView("AddTreeView", this);
            currentScene = new Scene(newView);
            myViews.put("AddTreeView", currentScene);
        }

        swapToView(currentScene);
    }

    private void createAndShowScoutView() {
        Scene currentScene = myViews.get("ScoutView");

        if (currentScene == null) {
            View newView = ViewFactory.createView("ScoutView", this);
            currentScene = new Scene(newView);
            myViews.put("ScoutView", currentScene);
        }

        swapToView(currentScene);
    }

    /*private void createAndShowModifyScoutView(Properties scoutProps) {
        View newView = ViewFactory.createView("ModifyScoutView", this, scoutProps);
        Scene newScene = new Scene(newView);
        myViews.put("ModifyScoutView", newScene);
        swapToView(newScene);
    }*/

    /*private void createAndShowRemoveScoutView(Properties scoutProps) {
        View newView = ViewFactory.createView("RemoveScoutView", this, scoutProps);
        Scene newScene = new Scene(newView);
        myViews.put("RemoveScoutView", newScene);
        swapToView(newScene);
    }*/

    private void createAndShowScoutCollectionView() {
        Scene currentScene = myViews.get("ScoutCollectionView");

        if (currentScene == null) {
            View newView = ViewFactory.createView("ScoutCollectionView", this);
            currentScene = new Scene(newView);
            myViews.put("ScoutCollectionView", currentScene);
        }

        swapToView(currentScene);
    }

    // IModel Implementation
    @Override
    public Object getState(String key) {
        return switch (key) {
            default -> "";
        };
    }

    @Override
    public void subscribe(String key, IView subscriber) {
        myRegistry.subscribe(key, subscriber);
    }

    @Override
    public void unSubscribe(String key, IView subscriber) {
        myRegistry.unSubscribe(key, subscriber);
    }

    @Override
    public void stateChangeRequest(String key, Object value) {
        switch (key) {
            case "AddTreeTransaction" -> createAndShowAddTreeView();
            case "AddScoutTransaction" -> createAndShowScoutView();
            case "ModifyScoutTransaction" -> createAndShowScoutCollectionView(); // You select the scout from this view
            case "RemoveScoutTransaction" -> createAndShowScoutCollectionView(); // You select the scout from this view
            case "ShowModifyScoutView" -> {
                if (value instanceof Properties props) {
                    //createAndShowModifyScoutView(props);
                }
            }
            case "ShowRemoveScoutView" -> {
                if (value instanceof Properties props) {
                    //createAndShowRemoveScoutView(props);
                }
            }
            case "CancelTransaction" -> createAndShowTransactionChoiceView();
            case "Done" -> myStage.close();
        }

        myRegistry.updateSubscribers(key, this);
    }

    @Override
    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }

    // Swaps the current scene in the stage
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
}
