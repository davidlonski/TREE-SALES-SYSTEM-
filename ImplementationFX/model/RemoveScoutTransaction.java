package model;

import impresario.IModel;
import impresario.IView;
import exception.InvalidPrimaryKeyException;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

public class RemoveScoutTransaction implements IModel, IView {

    private Scout scoutToRemove;
    private String transactionStatusMessage;
    private Hashtable<String, Vector<IView>> subscribers = new Hashtable<>();

    //----------------------------------------------------------
    public RemoveScoutTransaction() {
        // No dependencies needed currently
    }

    //----------------------------------------------------------
    public void processTransaction(Properties props) {
        String scoutId = props.getProperty("ID");

        if (scoutId == null || scoutId.trim().isEmpty()) {
            transactionStatusMessage = "ERROR: No Scout ID provided.";
            return;
        }

        try {
            scoutToRemove = new Scout(scoutId);

            // Perform physical deletion from the database
            scoutToRemove.deleteScout();
            transactionStatusMessage = "Scout successfully removed from the system.";

        } catch (InvalidPrimaryKeyException e) {
            transactionStatusMessage = "ERROR: Scout not found with ID: " + scoutId;
        } catch (SQLException e) {
            transactionStatusMessage = "ERROR: Database error while removing Scout: " + e.getMessage();
        }

        updateSubscribers("TransactionStatusMessage", this);
    }

    //----------------------------------------------------------
    @Override
    public Object getState(String key) {
        if ("TransactionStatusMessage".equals(key)) {
            return transactionStatusMessage;
        } else if (scoutToRemove != null) {
            return scoutToRemove.getState(key);
        }
        return null;
    }

    //----------------------------------------------------------
    @Override
    public void stateChangeRequest(String key, Object value) {
        if ("DoYourJob".equals(key)) {
            doYourJob();
        }

        updateSubscribers(key, this);
    }

    //----------------------------------------------------------
    public void doYourJob() {
        // For testing or UI event hooks
    }

    //----------------------------------------------------------
    @Override
    public void subscribe(String key, IView subscriber) {
        Vector<IView> subs = subscribers.computeIfAbsent(key, k -> new Vector<>());
        if (!subs.contains(subscriber)) {
            subs.add(subscriber);
        }
    }

    //----------------------------------------------------------
    @Override
    public void unSubscribe(String key, IView subscriber) {
        Vector<IView> subs = subscribers.get(key);
        if (subs != null) {
            subs.remove(subscriber);
        }
    }

    //----------------------------------------------------------
    public void updateSubscribers(String key, Object value) {
        Vector<IView> subs = subscribers.get(key);
        if (subs != null) {
            for (IView view : subs) {
                view.updateState(key, value);
            }
        }
    }

    //----------------------------------------------------------
    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }
}
