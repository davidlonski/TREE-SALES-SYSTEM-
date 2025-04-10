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

public class AddTreeTransaction implements IView, IModel {
    private Properties dependencies;
    private ModelRegistry myRegistry;

    private Hashtable<String, Scene> myViews;
    private Stage myStage;

    private String loginErrorMessage;
    private String transactionErrorMessage;
    private String successMessage;
    //private TreeCollection treeCollection

    public AddTreeTransaction()
    {
        myStage = MainStageContainer.getInstance();
        myViews = new Hashtable<String, Scene>();

        myRegistry = new ModelRegistry("AddTreeTransaction");
        //treeCollection = new TreeCollection();

        if(myRegistry == null){
            new Event(Event.getLeafLevelClassName(this), "AddTreeTransaction",
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
        /** if(key.equals("TreeCollection")){
         return treeCollection;
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
        }else if(key.equals("AddTree")){
            Tree tree = new Tree();

            Scene currentScene = (Scene)myViews.get("TreeView");

            if(currentScene != null){
                View newView = ViewFactory.createView("TreeView", this);
                currentScene = new Scene(newView);
                myViews.put("TreeView", currentScene);
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

