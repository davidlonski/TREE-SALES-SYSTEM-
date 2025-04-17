package model;

import impresario.IModel;
import impresario.IView;
import exception.InvalidPrimaryKeyException;
import userinterface.ViewFactory;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ModifyScoutTransaction extends EntityBase implements IModel, IView {

    private Scout scoutToModify;
    private ScoutCollection scoutCollection;
    private String transactionStatusMessage = "";
    private Hashtable<String, Vector<IView>> subscribers = new Hashtable<>();

    public ModifyScoutTransaction() {
        super("Scout");
    }

    @Override
    public void stateChangeRequest(String key, Object value) {
        switch (key) {
            case "DoYourJob":
                createAndShowScoutSearchView();
                break;

            case "SearchScouts":
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
                scoutToModify = new Scout((Properties) value);
                createAndShowModifyScoutView();
                break;

            case "ModifyScout":
                processTransaction((Properties) value);
                break;
        }

        updateSubscribers(key, this);
    }

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

        updateSubscribers("TransactionStatusMessage", this);
    }

    private void createAndShowScoutSearchView() {
        ViewFactory.createView("ScoutSearchView", this);
    }

    private void createAndShowScoutCollectionView() {
        ViewFactory.createView("ScoutCollectionView", scoutCollection);
    }

    private void createAndShowModifyScoutView() {
        ViewFactory.createView("ModifyScoutView", scoutToModify);
    }

    public Object getState(String key) {
        if ("TransactionStatusMessage".equals(key)) {
            return transactionStatusMessage;
        } else if (scoutToModify != null) {
            return scoutToModify.getState(key);
        }
        return null;
    }

    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }

    public void subscribe(String key, IView subscriber) {
        Vector<IView> subs = subscribers.computeIfAbsent(key, k -> new Vector<>());
        if (!subs.contains(subscriber)) {
            subs.add(subscriber);
        }
    }

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

    @Override
    protected void initializeSchema(String tableName) {
        if (mySchema == null) {
            mySchema = getSchemaInfo(tableName);
        }
    }
}
