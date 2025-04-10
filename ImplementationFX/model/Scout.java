package model;

import database.*;
import exception.*;
import impresario.*;
import common.*;
import event.*;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class Scout extends EntityBase implements IView, impresario.IModel {
    private static final String myTableName = "Scout";
    protected Properties dependencies;
    protected Properties persistentState;
    private String updateStatusMessage = "";

    public Scout(String id) throws InvalidPrimaryKeyException {
        super(myTableName);
        setDependencies();
        String query = "SELECT * FROM " + myTableName + " WHERE ID = '" + id + "'";
        Vector<Properties> result = getSelectQueryResult(query);

        if (result == null || result.size() != 1) {
            throw new InvalidPrimaryKeyException("No scout found with ID: " + id);
        } else {
            persistentState = result.firstElement();
        }
    }

    public Scout() {
        super(myTableName);
        setDependencies();
        persistentState = new Properties();
        setDefaultStatus();
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

        setDefaultStatus();
    }

    private void setDefaultStatus() {
        if (!persistentState.containsKey("Status")) {
            persistentState.setProperty("Status", "Active");
        }
        if (!persistentState.containsKey("DateStatusUpdated")) {
            persistentState.setProperty("DateStatusUpdated", new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
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
        String id = persistentState.getProperty("ID");
        if (id == null || id.isEmpty()) {
            insertNewScout();
        } else {
            updateExistingScout(id);
        }
    }

    private void insertNewScout() throws SQLException {
        Properties scoutData = getStateAsProperties();
        scoutData.remove("ID");
        insertPersistentState(mySchema, scoutData);
    }

    private void updateExistingScout(String id) throws SQLException {
        Properties whereClause = new Properties();
        whereClause.setProperty("ID", id);
        updatePersistentState(mySchema, persistentState, whereClause);
    }

    public void deleteScout() throws SQLException {
        String id = persistentState.getProperty("ID");
        if (id == null || id.isEmpty()) {
            throw new SQLException("Cannot delete scout: ID is missing.");
        }

        Properties whereClause = new Properties();
        whereClause.setProperty("ID", id);
        deletePersistentState(mySchema, whereClause);
    }

    private Properties getStateAsProperties() {
        Properties data = new Properties();
        data.setProperty("ID", persistentState.getProperty("ID", ""));
        data.setProperty("LastName", persistentState.getProperty("LastName", ""));
        data.setProperty("FirstName", persistentState.getProperty("FirstName", ""));
        data.setProperty("MiddleName", persistentState.getProperty("MiddleName", ""));
        data.setProperty("DateOfBirth", persistentState.getProperty("DateOfBirth", ""));
        data.setProperty("PhoneNumber", persistentState.getProperty("PhoneNumber", ""));
        data.setProperty("Email", persistentState.getProperty("Email", ""));
        data.setProperty("TroopID", persistentState.getProperty("TroopID", ""));
        data.setProperty("Status", persistentState.getProperty("Status", "Active"));
        data.setProperty("DateStatusUpdated", persistentState.getProperty("DateStatusUpdated", ""));
        return data;
    }

    public void setState(String key, String value) {
        persistentState.setProperty(key, value);
    }

    public Vector<String> getEntryListView() {
        Vector<String> view = new Vector<>();
        view.add(persistentState.getProperty("ID"));
        view.add(persistentState.getProperty("LastName"));
        view.add(persistentState.getProperty("FirstName"));
        view.add(persistentState.getProperty("MiddleName"));
        view.add(persistentState.getProperty("DateOfBirth"));
        view.add(persistentState.getProperty("PhoneNumber"));
        view.add(persistentState.getProperty("Email"));
        view.add(persistentState.getProperty("TroopID"));
        view.add(persistentState.getProperty("Status"));
        view.add(persistentState.getProperty("DateStatusUpdated"));
        return view;
    }

    @Override
    public String toString() {
        return "Scout ID: " + getState("ID") +
                ", Name: " + getState("FirstName") + " " + getState("MiddleName") + " " + getState("LastName") +
                ", DOB: " + getState("DateOfBirth") +
                ", Phone: " + getState("PhoneNumber") +
                ", Email: " + getState("Email") +
                ", TroopID: " + getState("TroopID") +
                ", Status: " + getState("Status") +
                ", Status Updated: " + getState("DateStatusUpdated");
    }
}
