package userinterface.TreeViews;

import impresario.IModel;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import model.TreeModels.Tree;
import model.TreeModels.TreeCollection;
import userinterface.MessageView;
import userinterface.View;

import java.util.Vector;

public class TreeCollectionView extends View {

    protected TableView<TreeTableModel> tableOfTrees;
    protected Button submitButton, cancelButton;
    private MessageView statusLog;
    private String mode; // "Modify" or "Remove"

    public TreeCollectionView(IModel model, String mode) {
        super(model, "TreeCollectionView");
        this.mode = mode;

        VBox container = new VBox(10);
        container.setPadding(new Insets(15, 5, 5, 5));
        container.getChildren().add(createTitle());
        container.getChildren().add(createFormContent());
        container.getChildren().add(createStatusLog(""));
        getChildren().add(container);

        populateFields();
        myModel.subscribe("TreeListUpdated", this);
    }

    protected void populateFields() {
        getEntryTableModelValues();
    }

    protected void getEntryTableModelValues() {
        ObservableList<TreeTableModel> tableData = FXCollections.observableArrayList();
        try {
            TreeCollection treeCollection = (TreeCollection) myModel.getState("TreeList");
            Vector<Tree> entryList = (Vector<Tree>) treeCollection.getState("Trees");

            tableOfTrees.getItems().clear();

            for (Tree tree : entryList) {
                Vector<String> treeData = tree.getEntryListView();
                TreeTableModel row = new TreeTableModel(treeData);
                tableData.add(row);
            }

            tableOfTrees.setItems(tableData);
        } catch (Exception e) {
            displayErrorMessage("Error fetching tree list: " + e.getMessage());
        }
    }

    private Node createTitle() {
        Text titleText = new Text("Tree Collection");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        return titleText;
    }

    private VBox createFormContent() {
        VBox vbox = new VBox(10);

        Text prompt = new Text("List of Trees");
        prompt.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        tableOfTrees = new TableView<>();
        tableOfTrees.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        TableColumn<TreeTableModel, String> barcodeCol = new TableColumn<>("Barcode");
        barcodeCol.setCellValueFactory(new PropertyValueFactory<>("barcode"));

        TableColumn<TreeTableModel, String> treeTypeCol = new TableColumn<>("Tree Type");
        treeTypeCol.setCellValueFactory(new PropertyValueFactory<>("treeType"));

        TableColumn<TreeTableModel, String> notesCol = new TableColumn<>("Notes");
        notesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));

        TableColumn<TreeTableModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<TreeTableModel, String> statusDateCol = new TableColumn<>("Status Date");
        statusDateCol.setCellValueFactory(new PropertyValueFactory<>("dateStatusUpdated"));

        tableOfTrees.getColumns().addAll(
                barcodeCol, treeTypeCol, notesCol, statusCol, statusDateCol
        );

        tableOfTrees.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() >= 2) {
                    processTreeSelected();
                }
            }
        });

        submitButton = new Button("Select");
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                processTreeSelected();
            }
        });

        cancelButton = new Button("Back");
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                myModel.stateChangeRequest("CancelTreeList", null);
            }
        });

        HBox btnBox = new HBox(20, submitButton, cancelButton);
        btnBox.setAlignment(Pos.CENTER);

        vbox.getChildren().addAll(prompt, tableOfTrees, btnBox);
        return vbox;
    }

    protected void processTreeSelected() {
        TreeTableModel selected = tableOfTrees.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if ("Remove".equals(mode)) {
                myModel.stateChangeRequest("RemoveTree", selected.getBarcode());
            } else {
                myModel.stateChangeRequest("TreeSelected", selected.getBarcode());
            }
        } else {
            displayErrorMessage("No tree selected.");
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
        if ("TreeListUpdated".equals(key)) {
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
