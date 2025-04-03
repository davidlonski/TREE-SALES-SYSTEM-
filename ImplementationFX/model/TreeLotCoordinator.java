package model;

// system imports
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


public class TreeLotCoordinator implements IView, IModel {
    //-----------------------------------------------------------------------------
    // For Impresario
    private Properties dependencies;
    private ModelRegistry myRegistry;

    // GUI Components
    private Hashtable<String, Scene> myViews;
    private Stage myStage;

    // Initialize Collections and Models here
    // e.g. Book, Patron, BookCollection

    // Initialize Message Strings for observer pattern
    // e.g. insertSuccess, insertFail
    private String closeStage;

    public TreeLotCoordinator() {
        myStage = MainStageContainer.getInstance();
        myViews = new Hashtable<String, Scene>();

        myRegistry = new ModelRegistry("TreeLotCoordinator");

        // Instantiate Collections

        if(myRegistry == null){
            new Event(Event.getLeafLevelClassName(this), "TreeLotCoordinator",
                    "Could not instantiate Registry", Event.ERROR);
        }

        setDependencies();

        // createAndShowTransactionChoiceView();



    }






    //-----------------------------------------------------------------------------
    private void setDependencies(){
        dependencies = new Properties();

        closeStage = "Closing Stage";
        dependencies.setProperty("closeStage", closeStage);

        myRegistry.setDependencies(dependencies);
    }


    //-----------------------------------------------------------------------------
    @Override
    public Object getState(String key) {
        return switch (key){
            // case "BookList" -> bookCollection;
            default -> "";
        };
    }

    //-----------------------------------------------------------------------------
    @Override
    public void subscribe(String key, IView subscriber) {
        myRegistry.subscribe(key, subscriber);
    }

    //-----------------------------------------------------------------------------
    @Override
    public void unSubscribe(String key, IView subscriber) {
        myRegistry.unSubscribe(key, subscriber);
    }

    //-----------------------------------------------------------------------------
    @Override
    public void stateChangeRequest(String key, Object value) {
        if(key.equals("close")){
            closeStage = "Closing Stage";
            myRegistry.updateSubscribers("closeStage", this);
            myStage.close();
        }
    }

    //-----------------------------------------------------------------------------
    @Override
    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }


    //-----------------------------------------------------------------------------
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


        //Place in center
        WindowPosition.placeCenter(myStage);

    }


}
