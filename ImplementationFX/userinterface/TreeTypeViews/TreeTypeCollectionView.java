package userinterface.TreeTypeViews;

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
import model.TreeTypeModels.*;
import userinterface.MessageView;
import userinterface.View;

import java.util.Vector;

public class TreeTypeCollectionView extends View {

    protected TableView<TreeTypeTableModel> tableOfTreeTypes;
    protected Button submitButton, cancelButton;
    private MessageView statusLog;

    public TreeTypeCollectionView(IModel model) {
        super(model, "TreeTypeCollectionView");

        VBox container = new VBox(10);
        container.setPadding(new Insets(15, 5, 5, 5));
        container.getChildren().add(createTitle());
        container.getChildren().add(createFormContent());
        container.getChildren().add(createStatusLog(""));
        getChildren().add(container);

        populateFields();
        myModel.subscribe("TreeTypeListUpdated", this);
    }

    protected void populateFields() {
        getEntryTableModelValues();
    }

    protected void getEntryTableModelValues() {
        ObservableList<TreeTypeTableModel> tableData = FXCollections.observableArrayList();
        try {
            Vector<TreeType> treeTypes = (Vector<TreeType>) myModel.getState("TreeTypes");

            System.out.println("DEBUG: Found " + (treeTypes != null ? treeTypes.size() : 0) + " tree types");

            tableOfTreeTypes.getItems().clear();

            if (treeTypes != null) {
                for (TreeType type : treeTypes) {
                    Vector<String> typeData = type.getEntryListView();
                    TreeTypeTableModel row = new TreeTypeTableModel(typeData);
                    tableData.add(row);
                    System.out.println("DEBUG: Added tree type ID " + typeData.get(0) + " to table");
                }
            }

            tableOfTreeTypes.setItems(tableData);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            displayErrorMessage("Error fetching tree type list: " + e.getMessage());
        }
    }

    private Node createTitle() {
        Text titleText = new Text("Tree Type Collection");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        return titleText;
    }

    private VBox createFormContent() {
        VBox vbox = new VBox(10);

        Text prompt = new Text("List of Tree Types");
        prompt.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        tableOfTreeTypes = new TableView<>();
        tableOfTreeTypes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        TableColumn<TreeTypeTableModel, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<TreeTypeTableModel, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("typeDescription"));
        descriptionCol.setPrefWidth(200);

        TableColumn<TreeTypeTableModel, String> costCol = new TableColumn<>("Cost");
        costCol.setCellValueFactory(new PropertyValueFactory<>("cost"));

        TableColumn<TreeTypeTableModel, String> barcodePrefixCol = new TableColumn<>("Barcode Prefix");
        barcodePrefixCol.setCellValueFactory(new PropertyValueFactory<>("barcodePrefix"));

        tableOfTreeTypes.getColumns().addAll(
                idCol, descriptionCol, costCol, barcodePrefixCol
        );

        tableOfTreeTypes.setOnMousePressed((MouseEvent event) -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() >= 2) {
                processTreeTypeSelected();
            }
        });

        submitButton = new Button("Select");
        submitButton.setOnAction(e -> {
            System.out.println("DEBUG: Select button clicked");
            processTreeTypeSelected();
        });

        cancelButton = new Button("Back");
        cancelButton.setOnAction((ActionEvent e) -> myModel.stateChangeRequest("CancelTreeTypeList", null));

        HBox btnBox = new HBox(20, submitButton, cancelButton);
        btnBox.setAlignment(Pos.CENTER);

        vbox.getChildren().addAll(prompt, tableOfTreeTypes, btnBox);
        return vbox;
    }

    protected void processTreeTypeSelected() {
        TreeTypeTableModel selected = tableOfTreeTypes.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String treeTypeId = selected.getId();
            System.out.println("DEBUG: Selected tree type with ID: " + treeTypeId);
            System.out.println("DEBUG: Sending TreeTypeSelected event with ID: " + treeTypeId);
            myModel.stateChangeRequest("TreeTypeSelected", treeTypeId);
        } else {
            System.out.println("DEBUG: No tree type selected");
            displayErrorMessage("No tree type selected.");
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
        System.out.println("DEBUG: TreeTypeCollectionView.updateState: " + key);
        if (key.equals("TreeTypeListUpdated")) {
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
