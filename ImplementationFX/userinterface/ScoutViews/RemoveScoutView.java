package userinterface.ScoutViews;

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

public class RemoveScoutView extends View {

    private Label scoutInfoLabel;
    private Button confirmButton, cancelButton;
    private MessageView statusLog;

    private final Properties scoutProps;

    public RemoveScoutView(IModel model, Properties props) {
        super(model, "RemoveScoutView");
        this.scoutProps = props;

        VBox container = new VBox(10);
        container.setPadding(new Insets(20));
        container.getChildren().addAll(createTitle(), createFormContent(), createStatusLog(""));

        getChildren().add(container);
    }

    private Node createTitle() {
        Text titleText = new Text("Remove Scout");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        return titleText;
    }

    private Node createFormContent() {
        VBox box = new VBox(10);

        scoutInfoLabel = new Label(formatScoutInfo());
        scoutInfoLabel.setWrapText(true);
        scoutInfoLabel.setStyle("-fx-border-color: gray; -fx-padding: 10; -fx-background-color: #f9f9f9;");

        confirmButton = new Button("Confirm Removal");
        confirmButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        confirmButton.setOnAction(e -> myModel.stateChangeRequest("RemoveScout", null));

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> myModel.stateChangeRequest("CancelRemoval", null));

        box.getChildren().addAll(
                new Label("Are you sure you want to remove this scout?"),
                scoutInfoLabel,
                new HBox(10, confirmButton, cancelButton)
        );

        return box;
    }

    private String formatScoutInfo() {
        return String.format("Scout ID: %s\nName: %s %s\nDOB: %s\nPhone: %s\nEmail: %s\nTroop ID: %s",
                scoutProps.getProperty("scoutID"),
                scoutProps.getProperty("firstName"),
                scoutProps.getProperty("lastName"),
                scoutProps.getProperty("dateOfBirth"),
                scoutProps.getProperty("phoneNumber"),
                scoutProps.getProperty("email"),
                scoutProps.getProperty("troopID")
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
