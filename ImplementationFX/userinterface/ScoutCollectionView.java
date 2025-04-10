package userinterface;

import impresario.IModel;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import model.Scout;
import model.ScoutCollection;

import java.util.Enumeration;
import java.util.Vector;

public class ScoutCollectionView extends View {

    protected TableView<ScoutTableModel> tableOfScouts;
    protected Button submitButton, cancelButton;
    private MessageView statusLog;

    public ScoutCollectionView(IModel model) {
        super(model, "ScoutCollectionView");

        VBox container = new VBox(10);
        container.setPadding(new Insets(15, 5, 5, 5));
        container.getChildren().add(createTitle());
        container.getChildren().add(createFormContent());
        container.getChildren().add(createStatusLog(""));
        getChildren().add(container);

        populateFields();
        myModel.subscribe("ScoutListUpdated", this);
    }

    protected void populateFields() {
        getEntryTableModelValues();
    }

    protected void getEntryTableModelValues() {
        ObservableList<ScoutTableModel> tableData = FXCollections.observableArrayList();
        try {
            ScoutCollection scoutCollection = (ScoutCollection) myModel.getState("ScoutList");
            Vector<Scout> entryList = (Vector<Scout>) scoutCollection.getState("Scouts");

            tableOfScouts.getItems().clear();
            for (Scout scout : entryList) {
                Vector<String> scoutData = scout.getEntryListView();  // Get data from Scout
                ScoutTableModel row = new ScoutTableModel(scoutData);  // Pass the vector to the model
                tableData.add(row);
            }

            tableOfScouts.setItems(tableData);
        } catch (Exception e) {
            displayErrorMessage("Error fetching scout list: " + e.getMessage());
        }
    }


    private Node createTitle() {
        Text titleText = new Text(" Scout Collection ");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        return titleText;
    }

    private VBox createFormContent() {
        VBox vbox = new VBox(10);

        Text prompt = new Text("List of Scouts");
        prompt.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        tableOfScouts = new TableView<>();
        tableOfScouts.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        TableColumn<ScoutTableModel, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("scoutId"));

        TableColumn<ScoutTableModel, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<ScoutTableModel, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        TableColumn<ScoutTableModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        tableOfScouts.getColumns().addAll(idCol, nameCol, phoneCol, statusCol);

        tableOfScouts.setOnMousePressed((MouseEvent event) -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() >= 2) {
                processScoutSelected();
            }
        });

        submitButton = new Button("Select");
        submitButton.setOnAction(e -> processScoutSelected());

        cancelButton = new Button("Back");
        cancelButton.setOnAction((ActionEvent e) -> myModel.stateChangeRequest("CancelScoutList", null));

        HBox btnBox = new HBox(20, submitButton, cancelButton);
        btnBox.setAlignment(Pos.CENTER);

        vbox.getChildren().addAll(prompt, tableOfScouts, btnBox);
        return vbox;
    }

    protected void processScoutSelected() {
        ScoutTableModel selected = tableOfScouts.getSelectionModel().getSelectedItem();
        if (selected != null) {
            myModel.stateChangeRequest("ScoutSelected", selected.getScoutId());
        } else {
            displayErrorMessage("No scout selected.");
        }
    }

    protected MessageView createStatusLog(String initialMessage) {
        statusLog = new MessageView(initialMessage);
        return statusLog;
    }

    public void displayErrorMessage(String msg) {
        statusLog.displayErrorMessage(msg);
    }

    public void clearErrorMessage() {
        statusLog.clearErrorMessage();
    }

    @Override
    public void updateState(String key, Object value) {
        if (key.equals("ScoutListUpdated")) {
            getEntryTableModelValues();
        }
    }

    @Override
    public Scene createScene() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(15, 5, 5, 5));

        container.getChildren().addAll(createTitle(), createFormContent(), createStatusLog(""));

        return new Scene(container, 600, 400);
    }

}
