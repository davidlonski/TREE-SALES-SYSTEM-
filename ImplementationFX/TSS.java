
// system imports
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;

// project imports
import event.Event;

import userinterface.MainStageContainer;
import userinterface.WindowPosition;

import model.System;


public class TSS extends Application {

    private System mySystem;

    private Stage mainStage;

    public void start(Stage primaryStage) {
        java.lang.System.out.println("Tree Scout Version 1.0");
        java.lang.System.out.println("Copyright 2004/2015 CSC429-Group");

        // Create the top-level container (main frame) and add contents to it.
        MainStageContainer.setStage(primaryStage, "Tree Sales System Version 1.0");
        mainStage = MainStageContainer.getInstance();

        // Finish setting up the stage (ENABLE THE GUI TO BE CLOSED USING THE TOP RIGHT
        // 'X' IN THE WINDOW), and show it.
        mainStage.setOnCloseRequest(new EventHandler <javafx.stage.WindowEvent>() {
            @Override
            public void handle(javafx.stage.WindowEvent event) {
                java.lang.System.exit(0);
            }
        });

        try
        {
            mySystem = new System();
        }
        catch(Exception exc)
        {
            java.lang.System.err.println("TSS.TSS - could not create System!");
            new Event(Event.getLeafLevelClassName(this), "TSS.<init>", "Unable to create System object", Event.ERROR);
            exc.printStackTrace();
        }


        WindowPosition.placeCenter(mainStage);

        mainStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
