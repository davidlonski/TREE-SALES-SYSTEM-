package userinterface;

import java.text.NumberFormat;
import java.util.Properties;

import javafx.event.Event;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import impresario.IModel;

public class TransactionChoiceView extends View {

    private Button addScoutTransactionButton;
    private Button modifyScoutTransactionButton;
    private Button removeScoutTransactionButton;
    private Button addTreeTransactionButton;
    private Button modifyTreeTransactionButton;
    private Button removeTreeTransactionButton;
    private Button addTreeTypeTransactionButton;

    private Button closeTransactionButton;

    private MessageView statusLog;

    public TransactionChoiceView(IModel transaction){
        super(transaction, "TransactionChoiceView");

        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);

        container.getChildren().add(createTitle());
        container.getChildren().add(createFormContents());
        container.getChildren().add(createStatusLog("                          "));

        getChildren().add(container);

        populateFields();

        myModel.subscribe("LoginError", this);
    }

    // Create the label (Text) for the title of the screen
    //-------------------------------------------------------------
    private Node createTitle()
    {
        Text titleText = new Text("       Transaction Choice          ");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setFill(Color.DARKGREEN);
        return titleText;
    }

    private GridPane createFormContents(){
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Create a VBox to hold all buttons
        VBox buttonBox = new VBox(10);  // 10 is the spacing between buttons
        buttonBox.setAlignment(Pos.CENTER);

        addScoutTransactionButton = new Button("Add A Scout");
        addScoutTransactionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                processAction(event);
            }
        });
        addScoutTransactionButton.setPrefWidth(150);

        modifyScoutTransactionButton = new Button("Modify A Scout");
        modifyScoutTransactionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearErrorMessage();
                myModel.stateChangeRequest("ModifyScoutTransaction", null);
            }
        });
        modifyScoutTransactionButton.setPrefWidth(150);

        removeScoutTransactionButton = new Button("Remove A Scout");
        removeScoutTransactionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                processAction(event);
            }
        });
        removeScoutTransactionButton.setPrefWidth(150);

        addTreeTransactionButton = new Button("Add A Tree");
        addTreeTransactionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                processAction(event);
            }
        });
        addTreeTransactionButton.setPrefWidth(150);

        modifyTreeTransactionButton = new Button("Modify A Tree");
        modifyTreeTransactionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                processAction(event);
            }
        });
        modifyTreeTransactionButton.setPrefWidth(150);

        removeTreeTransactionButton = new Button("Remove A Tree");
        removeTreeTransactionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                processAction(event);
            }
        });
        removeTreeTransactionButton.setPrefWidth(150);

        addTreeTypeTransactionButton = new Button("Add A Tree Type");
        addTreeTypeTransactionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                processAction(event);
            }
        });
        addTreeTypeTransactionButton.setPrefWidth(150);

        closeTransactionButton = new Button("Close");
        closeTransactionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                processAction(event);
            }
        });
        closeTransactionButton.setPrefWidth(150);

        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.BOTTOM_RIGHT);
        hbox.getChildren().add(closeTransactionButton);

        // Add all buttons to the VBox
        buttonBox.getChildren().addAll(
                addScoutTransactionButton,
                modifyScoutTransactionButton,
                removeScoutTransactionButton,
                addTreeTransactionButton,
                modifyTreeTransactionButton,
                removeTreeTransactionButton,
                addTreeTypeTransactionButton,
                closeTransactionButton
        );

        // Add the VBox to the grid
        grid.add(buttonBox, 0, 0);
        grid.add(hbox, 1, 0);

        return grid;
    }

    private MessageView createStatusLog(String initialMessage){
        statusLog = new MessageView(initialMessage);
        return statusLog;
    }

    public void populateFields(){
        // No fields to populate for this view
    }

    public void processAction(Event evt){
        if (evt.getSource() == addScoutTransactionButton){
            myModel.stateChangeRequest("AddScoutTransaction", null);
        } else if(evt.getSource() == modifyScoutTransactionButton){
            // Navigate to ScoutSearchView
            myModel.stateChangeRequest("ModifyScoutTransaction", null); // Trigger ScoutSearchView
        } else if(evt.getSource() == removeScoutTransactionButton){
            // Navigate to ScoutSearchView
            myModel.stateChangeRequest("RemoveScoutTransaction", null); // Trigger ScoutSearchView
        } else if(evt.getSource() == addTreeTransactionButton){
            myModel.stateChangeRequest("AddTreeTransaction", null);
        } else if(evt.getSource() == modifyTreeTransactionButton){
            myModel.stateChangeRequest("ModifyTreeTransaction", null);
        } else if(evt.getSource() == removeTreeTransactionButton){
            myModel.stateChangeRequest("RemoveTreeTransaction", null);
        } else if(evt.getSource() == addTreeTypeTransactionButton){
            myModel.stateChangeRequest("AddTreeTypeTransaction", null);
        } else if(evt.getSource() == closeTransactionButton){
            myModel.stateChangeRequest("Done", null);
        }
    }

    @Override
    public void updateState(String key, Object value) {
        if (key.equals("addScoutTransactionError")){
            displayErrorMessage((String)value);
        } else if(key.equals("modifyScoutTransactionError")){
            displayErrorMessage((String)value);
        } else if(key.equals("removeScoutTransactionError")){
            displayErrorMessage((String)value);
        } else if(key.equals("addTreeTransactionError")){
            displayErrorMessage((String)value);
        }
    }

    public void displayErrorMessage(String message){
        statusLog.displayMessage(message);
    }

    public void clearErrorMessage(){
        statusLog.clearErrorMessage();
    }

    @Override
    public Scene createScene() {
        VBox container = new VBox(10);
        container.getChildren().addAll(createTitle(), createFormContents(), createStatusLog(""));
        return new Scene(container, 600, 450);
    }
}
