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
    private Properties selectedScoutProps;

    public RemoveScoutTransaction() {}

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
                selectedScoutProps = (Properties) value;
                createAndShowRemoveScoutView();
                break;

            case "RemoveScout":
                processRemoval();
                break;

            case "CancelRemoval":
                transactionStatusMessage = "Scout removal cancelled.";
                updateSubscribers("TransactionStatusMessage", this);
                break;
        }

        updateSubscribers(key, this);
    }

    private void processRemoval() {
        /*
        if (selectedScoutProps == null || selectedScoutProps.getProperty("scoutID") == null) {
            transactionStatusMessage = "ERROR: No Scout selected for removal.";
            return;
        }

        try {
            String scoutId = selectedScoutProps.getProperty("scoutID");
            Scout s = new Scout(scoutId);
            s.deleteScout();
            transactionStatusMessage = "Scout successfully removed.";
        } catch (InvalidPrimaryKeyException e) {
            transactionStatusMessage = "ERROR: Scout not found.";
        } catch (SQLException e) {
            transactionStatusMessage = "ERROR: Database error: " + e.getMessage();
        }
        */
    }

    private void createAndShowScoutSearchView() {
        ViewFactory.createView("ScoutSearchView", this);
    }

    private void createAndShowScoutCollectionView() {
        ViewFactory.createView("ScoutCollectionView", scoutCollection);
    }

    private void createAndShowRemoveScoutView() {
        ViewFactory.createView("RemoveScoutView", this);
    }

    @Override
    public Object getState(String key) {
        if ("TransactionStatusMessage".equals(key)) {
            return transactionStatusMessage;
        }
        return null;
    }

    @Override
    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }

    @Override
    public void subscribe(String key, IView subscriber) {
        subscribers.computeIfAbsent(key, k -> new Vector<>()).add(subscriber);
    }

    @Override
    public void unSubscribe(String key, IView subscriber) {
        Vector<IView> subs = subscribers.get(key);
        if (subs != null) subs.remove(subscriber);
    }

    public void updateSubscribers(String key, Object value) {
        Vector<IView> subs = subscribers.get(key);
        if (subs != null) {
            for (IView view : subs) view.updateState(key, value);
        }
    }
}
