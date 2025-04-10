package model;

import database.*;
import exception.*;
import impresario.*;
import common.*;
import event.*;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class Tree extends EntityBase implements IView, impresario.IModel {
    private static final String myTableName = "Tree";
    protected Properties dependencies;
    protected Properties persistentState;
    private String updateStatusMessage = "";

    public Tree(String barcode) throws InvalidPrimaryKeyException {
        super(myTableName);
        setDependencies();
        String query = "SELECT * FROM " + myTableName + " WHERE Barcode = '" + barcode + "'";
        Vector<Properties> result = getSelectQueryResult(query);

        if (result == null || result.size() != 1) {
            throw new InvalidPrimaryKeyException("No tree found with Barcode: " + barcode);
        } else {
            this.persistentState = result.elementAt(0);
        }
    }

    public Tree() {
        super(myTableName);
        persistentState = new Properties();
        setDependencies();
    }

    public Tree(Properties props) {
        super(myTableName);
        setDependencies();
        persistentState = new Properties();

        Enumeration<?> keys = props.propertyNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = props.getProperty(key);
            persistentState.setProperty(key, value);
        }

        if (!persistentState.containsKey("Status")) {
            persistentState.setProperty("Status", "Available");
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

    public void setState(String key, String value) {
        persistentState.setProperty(key, value);
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
        }
    }

    public void update() throws SQLException {
        String barcode = persistentState.getProperty("Barcode");
        if (barcode == null || barcode.isEmpty()) {
            insertNewTree();
        } else {
            updateExistingTree(barcode);
        }
    }

    private void insertNewTree() throws SQLException {
        Properties treeData = getStateAsProperties();
        insertPersistentState(mySchema, treeData);
    }

    private void updateExistingTree(String barcode) throws SQLException {
        Properties whereClause = new Properties();
        whereClause.setProperty("Barcode", barcode);
        updatePersistentState(mySchema, persistentState, whereClause);
    }

    private Properties getStateAsProperties() {
        Properties data = new Properties();
        data.setProperty("Barcode", persistentState.getProperty("Barcode", ""));
        data.setProperty("TreeType", persistentState.getProperty("TreeType", ""));
        data.setProperty("Notes", persistentState.getProperty("Notes", ""));
        data.setProperty("Status", persistentState.getProperty("Status", "Available"));
        data.setProperty("DateStatusUpdated", persistentState.getProperty("DateStatusUpdated", ""));
        return data;
    }

    public void sellTree() {
        persistentState.setProperty("Status", "Sold");
        String date = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
        persistentState.setProperty("DateStatusUpdated", date);
    }

    @Override
    public String toString() {
        return "Barcode: " + getState("Barcode") +
                ", Tree Type: " + getState("TreeType") +
                ", Notes: " + getState("Notes") +
                ", Status: " + getState("Status") +
                ", Date Status Updated: " + getState("DateStatusUpdated");
    }

    public Vector<String> getEntryListView() {
        Vector<String> view = new Vector<>();
        view.addElement(persistentState.getProperty("Barcode"));
        view.addElement(persistentState.getProperty("TreeType"));
        view.addElement(persistentState.getProperty("Notes"));
        view.addElement(persistentState.getProperty("Status"));
        view.addElement(persistentState.getProperty("DateStatusUpdated"));
        return view;
    }
}
