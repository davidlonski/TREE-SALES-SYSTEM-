package userinterface.TreeViews;

import impresario.IModel;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import userinterface.MessageView;
import userinterface.View;

import java.util.Properties;

public class RemoveTreeView extends View {

    private Label treeInfoLabel;
    private Button confirmButton, cancelButton;
    private MessageView statusLog;

    private final Properties treeProps;

    public RemoveTreeView(IModel model, Properties props) {
        super(model, "RemoveTreeView");
        this.treeProps = props;

        VBox container = new VBox(10);
        container.setPadding(new Insets(20));
        container.getChildren().addAll(createTitle(), createFormContent(), createStatusLog(""));

        getChildren().add(container);
    }

    private Node createTitle() {
        Text titleText = new Text("Remove Tree");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        return titleText;
    }

    private Node createFormContent() {
        VBox box = new VBox(10);

        treeInfoLabel = new Label(formatTreeInfo());
        treeInfoLabel.setWrapText(true);
        treeInfoLabel.setStyle("-fx-border-color: gray; -fx-padding: 10; -fx-background-color: #f9f9f9;");

        confirmButton = new Button("Confirm Removal");
        confirmButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        confirmButton.setOnAction(e -> myModel.stateChangeRequest("RemoveTree", null));

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> myModel.stateChangeRequest("CancelRemoval", null));

        box.getChildren().addAll(
                new Label("Are you sure you want to remove this tree?"),
                treeInfoLabel,
                new HBox(10, confirmButton, cancelButton)
        );

        return box;
    }

    private String formatTreeInfo() {
        return String.format("Barcode: %s\nTree Type: %s\nNotes: %s\nStatus: %s\nStatus Updated: %s",
                treeProps.getProperty("barcode"),
                treeProps.getProperty("treeType"),
                treeProps.getProperty("notes"),
                treeProps.getProperty("status"),
                treeProps.getProperty("dateStatusUpdated")
        );
    }

    protected MessageView createStatusLog(String initialMessage) {
        statusLog = new MessageView(initialMessage);
        return statusLog;
    }

    @Override
    public void updateState(String key, Object value) {
        if ("TransactionStatusMessage".equals(key)) {
            String msg = (String) value;
            if (msg.toLowerCase().startsWith("error")) {
                statusLog.displayErrorMessage(msg);
            } else {
                statusLog.displayMessage(msg);
            }
        }
    }

    @Override
    public Scene createScene() {
        return new Scene(this, 600, 400);
    }
}
