package userinterface;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.Properties;

public class RemoveScoutView extends View {

    private Label scoutInfoLabel;
    private Button confirmButton;
    private Button cancelButton;
    private MessageView statusLog;

    private String scoutID;

    public RemoveScoutView(Properties scoutProps) {
        this.scoutID = scoutProps.getProperty("scoutID");

        setPadding(new Insets(20));
        setSpacing(10);

        Label titleLabel = new Label("Remove Scout");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Display summary of scout info for confirmation
        scoutInfoLabel = new Label(formatScoutInfo(scoutProps));
        scoutInfoLabel.setWrapText(true);
        scoutInfoLabel.setStyle("-fx-border-color: gray; -fx-padding: 10; -fx-background-color: #f9f9f9;");

        confirmButton = new Button("Confirm Removal");
        confirmButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        cancelButton = new Button("Cancel");

        HBox buttonBox = new HBox(10, confirmButton, cancelButton);

        statusLog = new MessageView("");

        getChildren().addAll(
                titleLabel,
                new Label("Are you sure you want to remove the following scout?"),
                scoutInfoLabel,
                buttonBox,
                statusLog
        );

        confirmButton.setOnAction(e -> processRemoval());
        cancelButton.setOnAction(e -> cancelRemoval());
    }

    private String formatScoutInfo(Properties scoutProps) {
        return String.format("Scout ID: %s\nName: %s %s\nDate of Birth: %s\nPhone: %s\nEmail: %s\nTroop ID: %s",
                scoutProps.getProperty("scoutID"),
                scoutProps.getProperty("firstName"),
                scoutProps.getProperty("lastName"),
                scoutProps.getProperty("dateOfBirth"),
                scoutProps.getProperty("phoneNumber"),
                scoutProps.getProperty("email"),
                scoutProps.getProperty("troopID"),
                scoutProps.getProperty("status")
        );
    }

    private void processRemoval() {
        if (scoutID == null || scoutID.isEmpty()) {
            statusLog.displayErrorMessage("Invalid Scout ID. Cannot remove.");
            return;
        }

        // Call controller logic here
        // Example: controller.processScoutRemoval(scoutID);
        statusLog.displayMessage("Scout removed successfully.");
    }

    private void cancelRemoval() {
        // You can navigate back to the previous view or just clear the log
        statusLog.clearErrorMessage();
    }
}
