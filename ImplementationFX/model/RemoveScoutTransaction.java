package model;

import impresario.IModel;
import impresario.IView;
import exception.InvalidPrimaryKeyException;
import userinterface.ViewFactory;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

public class RemoveScoutTransaction implements IModel, IView {

    private Hashtable<String, Vector<IView>> subscribers = new Hashtable<>();
    private String transactionStatusMessage = "";

    private ScoutCollection scoutCollection;
    private Scout selectedScout;

    public RemoveScoutTransaction() {
        // No dependencies to initialize
    }

    @Override
    public void stateChangeRequest(String key, Object value) {
        switch (key) {
            case "DoYourJob":
                createAndShowScoutSearchView();
                break;

            case "ScoutCollectionView":
                try {
                    String namePart = (String) value;
                    scoutCollection = new ScoutCollection();
                    scoutCollection.findScoutsWithLastNameLike(namePart);
                    createAndShowScoutCollectionView();
                } catch (Exception e) {
                    transactionStatusMessage = "ERROR: Could not retrieve scout data: " + e.getMessage();
                    updateSubscribers("TransactionStatusMessage", this);
                }
                break;

            case "ScoutSelected":
                selectedScout = new Scout((Properties) value);
                createAndShowRemoveScoutView();
                break;

            case "RemoveScout":
                processRemoval((Properties) value);
                break;
        }

        updateSubscribers(key, this);
    }

    private void processRemoval(Properties props) {
        String scoutId = props.getProperty("scoutID");

        if (scoutId == null || scoutId.isEmpty()) {
            transactionStatusMessage = "ERROR: No Scout ID provided.";
            return;
        }

        try {
            selectedScout = new Scout(scoutId);
            selectedScout.deleteScout();
            transactionStatusMessage = "Scout successfully removed from the system.";
        } catch (InvalidPrimaryKeyException e) {
            transactionStatusMessage = "ERROR: Scout not found with ID: " + scoutId;
        } catch (SQLException e) {
            transactionStatusMessage = "ERROR: Database error while removing Scout: " + e.getMessage();
        }

        updateSubscribers("TransactionStatusMessage", this);
    }

    private void createAndShowScoutSearchView() {
        ViewFactory.createView("ScoutSearchView", this);
    }

    private void createAndShowScoutCollectionView() {
        ViewFactory.createView("ScoutCollectionView", scoutCollection);
    }

    private void createAndShowRemoveScoutView() {
        ViewFactory.createView("RemoveScoutView", selectedScout);
    }

    @Override
    public Object getState(String key) {
        if ("TransactionStatusMessage".equals(key)) {
            return transactionStatusMessage;
        } else if (selectedScout != null) {
            return selectedScout.getState(key);
        }
        return null;
    }

    @Override
    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }

    @Override
    public void subscribe(String key, IView subscriber) {
        Vector<IView> subs = subscribers.computeIfAbsent(key, k -> new Vector<>());
        if (!subs.contains(subscriber)) {
            subs.add(subscriber);
        }
    }

    @Override
    public void unSubscribe(String key, IView subscriber) {
        Vector<IView> subs = subscribers.get(key);
        if (subs != null) {
            subs.remove(subscriber);
        }
    }

    public void updateSubscribers(String key, Object value) {
        Vector<IView> subs = subscribers.get(key);
        if (subs != null) {
            for (IView view : subs) {
                view.updateState(key, value);
            }
        }
    }
}
