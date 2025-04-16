package userinterface;

import impresario.IModel;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.Properties;

public class RemoveScoutView extends View {

    private Label scoutInfoLabel;
    private Button confirmButton, cancelButton;
    private MessageView statusLog;
    private String scoutID;

    public RemoveScoutView(IModel model, Properties scoutProps) {
        super(model, "RemoveScoutView");
        this.scoutID = scoutProps.getProperty("scoutID");

        VBox container = new VBox(10);
        container.setPadding(new Insets(20));
        container.getChildren().addAll(createTitle(), createFormContent(scoutProps), createStatusLog(""));

        getChildren().add(container);
    }

    private Node createTitle() {
        Text titleText = new Text("Remove Scout");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        return titleText;
    }

    private Node createFormContent(Properties scoutProps) {
        VBox box = new VBox(10);

        scoutInfoLabel = new Label(formatScoutInfo(scoutProps));
        scoutInfoLabel.setWrapText(true);
        scoutInfoLabel.setStyle("-fx-border-color: gray; -fx-padding: 10; -fx-background-color: #f9f9f9;");

        confirmButton = new Button("Confirm Removal");
        confirmButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        cancelButton = new Button("Cancel");

        confirmButton.setOnAction(e -> processRemoval());
        cancelButton.setOnAction(e -> cancelRemoval());

        box.getChildren().addAll(
                new Label("Are you sure you want to remove the following scout?"),
                scoutInfoLabel,
                new HBox(10, confirmButton, cancelButton)
        );

        return box;
    }

    private String formatScoutInfo(Properties scoutProps) {
        return String.format("Scout ID: %s\nName: %s %s\nDate of Birth: %s\nPhone: %s\nEmail: %s\nTroop ID: %s",
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

    private void processRemoval() {
        if (scoutID == null || scoutID.isEmpty()) {
            statusLog.displayErrorMessage("Invalid Scout ID. Cannot remove.");
            return;
        }

        Properties p = new Properties();
        p.setProperty("scoutID", scoutID);

        myModel.stateChangeRequest("RemoveScout", p);
        statusLog.displayMessage("Scout removed successfully.");
    }

    private void cancelRemoval() {
        statusLog.clearErrorMessage();
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
