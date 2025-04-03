package model;

import database.*;
import exception.*;
import impresario.*;
import common.*;
import event.*;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class Scout extends EntityBase implements IView, impresario.IModel {
    private static final String myTableName = "Scout";
    protected Properties dependencies;
    protected Properties persistentState;
    private String updateStatusMessage = "";

    public Scout(String scoutId) throws InvalidPrimaryKeyException {
        super(myTableName);
        setDependencies();
        String query = "SELECT * FROM " + myTableName + " WHERE scoutId = '" + scoutId + "'";
        Vector<Properties> result = getSelectQueryResult(query);

        if (result == null || result.size() != 1) {
            throw new InvalidPrimaryKeyException("No scout found with ID: " + scoutId);
        } else {
            this.persistentState = result.elementAt(0);
        }
    }

    public Scout() {
        super(myTableName);
        persistentState = new Properties();
    }

    public Scout(Properties props) {
        super(myTableName);
        setDependencies();
        persistentState = new Properties();

        Enumeration<?> keys = props.propertyNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = props.getProperty(key);
            persistentState.setProperty(key, value);
        }

        if (!persistentState.containsKey("status")) {
            persistentState.setProperty("status", "Active");
        }
    }

    private void setDependencies() {
        dependencies = new Properties();
        if (myRegistry != null) {
            myRegistry.setDependencies(dependencies);
        }
    }

    public Object getState(String key) {
        return persistentState.getProperty(key);
    }

    public void stateChangeRequest(String key, Object value) {
        myRegistry.updateSubscribers(key, this);
    }

    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }

    protected void initializeSchema(String tableName) {
        if (mySchema == null) {
            mySchema = getSchemaInfo(tableName);
            if (mySchema.getProperty("insertType") == null) {
                mySchema.setProperty("insertType", "AUTOINCREMENT");
            }
        }
    }

    public void update() throws SQLException {
        String scoutId = persistentState.getProperty("scoutId");
        if (scoutId == null || scoutId.isEmpty()) {
            insertNewScout();
        } else {
            updateExistingScout(scoutId);
        }
    }

    private void insertNewScout() throws SQLException {
        Properties scoutData = getStateAsProperties();
        scoutData.remove("scoutId");
        insertPersistentState(mySchema, scoutData);
    }

    private void updateExistingScout(String scoutId) throws SQLException {
        Properties whereClause = new Properties();
        whereClause.setProperty("scoutId", scoutId);
        updatePersistentState(mySchema, persistentState, whereClause);
    }

    private Properties getStateAsProperties() {
        Properties data = new Properties();
        data.setProperty("scoutId", persistentState.getProperty("scoutId", ""));
        data.setProperty("name", persistentState.getProperty("name", ""));
        data.setProperty("contactInfo", persistentState.getProperty("contactInfo", ""));
        data.setProperty("status", persistentState.getProperty("status", "Active"));
        return data;
    }

    public Vector<String> getEntryListView() {
        Vector<String> view = new Vector<>();
        view.addElement(persistentState.getProperty("scoutId"));
        view.addElement(persistentState.getProperty("name"));
        view.addElement(persistentState.getProperty("contactInfo"));
        view.addElement(persistentState.getProperty("status"));
        return view;
    }

    @Override
    public String toString() {
        return "Scout ID: " + getState("scoutId") +
                ", Name: " + getState("name") +
                ", Contact Info: " + getState("contactInfo") +
                ", Status: " + getState("status");
    }

    public void setState(String key, String value) {
        persistentState.setProperty(key, value);
    }
}

