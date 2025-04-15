package model;

import impresario.IView;
import exception.InvalidPrimaryKeyException;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class ModifyScoutTransaction extends EntityBase implements IView, impresario.IModel {

    private Scout scoutToModify;
    private String transactionStatusMessage;

    public ModifyScoutTransaction() {
        super("Scout");
    }

    //----------------------------------------------------------
    public void processTransaction(Properties props) {
        String scoutId = props.getProperty("ID");

        if (scoutId == null || scoutId.trim().isEmpty()) {
            transactionStatusMessage = "ERROR: No Scout ID provided.";
            return;
        }

        try {
            scoutToModify = new Scout(scoutId);

            for (String key : props.stringPropertyNames()) {
                if (!key.equals("ID")) {
                    scoutToModify.setState(key, props.getProperty(key));
                }
            }

            if (props.containsKey("Status")) {
                scoutToModify.setState("DateStatusUpdated",
                        new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            }

            scoutToModify.update();
            transactionStatusMessage = "Scout successfully updated.";
        } catch (InvalidPrimaryKeyException e) {
            transactionStatusMessage = "ERROR: Scout not found with ID: " + scoutId;
        } catch (SQLException e) {
            transactionStatusMessage = "ERROR: Database error while updating Scout: " + e.getMessage();
        }
    }

    //----------------------------------------------------------
    public Object getState(String key) {
        if ("TransactionStatusMessage".equals(key)) {
            return transactionStatusMessage;
        } else if (scoutToModify != null) {
            return scoutToModify.getState(key);
        }
        return null;
    }

    //----------------------------------------------------------
    public void stateChangeRequest(String key, Object value) {
        if ("DoYourJob".equals(key)) {
            doYourJob();
        }

        if (myRegistry != null) {
            myRegistry.updateSubscribers(key, this);
        }
    }

    //----------------------------------------------------------
    public void doYourJob() {
        // Placeholder for UI triggers
    }

    //----------------------------------------------------------
    protected void initializeSchema(String tableName) {
        if (mySchema == null) {
            mySchema = getSchemaInfo(tableName); // Pulls table structure (column names, types, etc.)
        }
    }
    //----------------------------------------------------------
    @Override
    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }

}
