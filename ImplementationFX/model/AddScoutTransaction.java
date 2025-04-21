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

public class AddScoutTransaction extends Transaction {
    private Scout newScout;

    private String insertSuccessful;

    public AddScoutTransaction() throws Exception{
        super();
    }

    protected void setDependencies(){
        dependencies = new Properties();

        insertSuccessful = "Successfully added scout to the database";

        dependencies.setProperty("InsertSuccessful", insertSuccessful);

        myRegistry.setDependencies(dependencies);
    }

    private void processTransaction(Properties props) {
        // Business logic
        newScout = new Scout(props);
        newScout.save();

        insertSuccessful = "Successfully added scout to the database";
        myRegistry.updateSubscribers("InsertSuccessful", this);
    }

    public Object getState(String key){
        return switch (key) {
            case "InsertSuccessful" -> insertSuccessful;
            default -> "";
        };
    }

    public void stateChangeRequest(String key, Object value){
        if (key.equals("DoYourJob")){
            doYourJob();
        }else if(key.equals("ProcessScoutTransaction")){
            processTransaction((Properties) value);
        }else if(key.equals("CancelAddScoutTransaction")){
            stateChangeRequest("CancelTransaction", null);
        }

        myRegistry.updateSubscribers(key, this);
    }


    protected Scene createView(){
        Scene currentScene = myViews.get("AddScoutView");

        if(currentScene == null){
            View newView = ViewFactory.createView("AddScoutView", this);
            currentScene = new Scene(newView);
            myViews.put("AddScoutView", currentScene);

            return currentScene;

        }else{
            return currentScene;
        }
    }



}