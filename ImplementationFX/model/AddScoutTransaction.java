package model;

import java.util.Hashtable;
import java.util.Properties;

import com.sun.tools.javac.Main;
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

public class AddScoutTransaction implements IView, IModel {
    private Properties dependencies;
    private ModelRegistry myRegistry;

    private Hashtable<String, Scene> myViews;
    private Stage myStage;

    private String loginErrorMessage;
    private String transactionErrorMessage;
    private String successMessage;
    //private ScoutCollection scoutCollection

    public AddScoutTransaction()
    {
        myStage = MainStageContainer.getInstance();
        myViews = new Hashtable<String, Scene>();

        myRegistry = new ModelRegistry("AddScoutTransaction");
        //scoutCollection = new ScoutCollection();

        if(myRegistry == null){
            new Event(Event.getLeafLevelClassName(this), "AddScoutTransaction",
                    "Could not instantiate Registry", Event.ERROR);

        }
        setDependencies();


    }

    private void setDependencies() {
        dependencies = new Properties();
        dependencies.setProperty("loginErrorMessage", loginErrorMessage);
        dependencies.setProperty("successMessage", successMessage);
        dependencies.setProperty("transactionErrorMessage", transactionErrorMessage);

        myRegistry.setDependencies(dependencies);
    }





    public Object getState(String key)
    {
        /** if(key.equals("ScoutCollection")){
            return scoutCollection;
        } */

        return "";
    }

    public void updateState(String key, Object value)
    {
        // DEBUG System.out.println("Teller.updateState: key: " + key);

        stateChangeRequest(key, value);
    }

    /** Register objects to receive state updates. */
    //----------------------------------------------------------
    public void subscribe(String key, IView subscriber) {

    }

    /** Unregister previously registered objects. */
    //----------------------------------------------------------
    public void unSubscribe(String key, IView subscriber) {

    }

    public void stateChangeRequest(String key, Object value) {
        if(key.equals("Done")){
            //CreateTreeLotCoordinator();
        }else if(key.equals("AddScout")){
            Scout scout = new Scout();

            Scene currentScene = (Scene)myViews.get("ScoutView");

            if(currentScene != null){
                View newView = ViewFactory.createView("ScoutView", this);
                currentScene = new Scene(newView);
                myViews.put("ScoutView", currentScene);
            }

            swapToView(currentScene);
        }
    }

    public void swapToView(Scene newScene)
    {


        if (newScene == null)
        {
            System.out.println("Teller.swapToView(): Missing view for display");
            new Event(Event.getLeafLevelClassName(this), "swapToView",
                    "Missing view for display ", Event.ERROR);
            return;
        }

        myStage.setScene(newScene);
        myStage.sizeToScene();

        WindowPosition.placeCenter(myStage);
    }
}
