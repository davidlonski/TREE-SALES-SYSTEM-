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

    public Tree() {
        super(myTableName);
        persistentState = new Properties();
        setDependencies();
    }

    public Tree(String id) throws InvalidPrimaryKeyException {
        super(myTableName);
        setDependencies();
        String query = "SELECT * FROM " + myTableName + " WHERE Barcode = '" + id + "'";
        Vector<Properties> result = getSelectQueryResult(query);
     
        if (result == null || result.size() != 1) {
            throw new InvalidPrimaryKeyException("No tree found with Barcode: " + id);
        } else {
            persistentState = result.firstElement();
        }
    }

    public Tree(Properties props) {
        super(myTableName);
        setDependencies();
        persistentState = new Properties();

        String barcode = props.getProperty("Barcode");
        String notes = props.getProperty("Notes");
        String dateStatus = props.getProperty("DateStatusUpdated");
        String treeType = props.getProperty("TreeType");
        String status = props.getProperty("Status");

        if (barcode != null && barcode.length() > 20) {
            throw new IllegalArgumentException("Barcode must be 20 characters or fewer.");
        }
        if (notes != null && notes.length() > 200) {
            throw new IllegalArgumentException("Notes must be 200 characters or fewer.");
        }
        if (status != null && !status.equals("Available") && !status.equals("Sold") && !status.equals("Damaged")) {
            throw new IllegalArgumentException("Status must be one of: Available, Sold, or Damaged");
        }

        Enumeration allKeys = props.propertyNames();
        while (allKeys.hasMoreElements()) {
            String nextKey = (String) allKeys.nextElement();
            persistentState.setProperty(nextKey, props.getProperty(nextKey));
        }
    }

    private void setDependencies() {
        dependencies = new Properties();
        myRegistry.setDependencies(dependencies);
    }

    public Object getState(String key) {
        if (key.equals("UpdateStatusMessage")) {
            return updateStatusMessage;
        }
        return persistentState.getProperty(key);
    }

    public void stateChangeRequest(String key, Object value) {
        myRegistry.updateSubscribers(key, this);
    }

    public void updateState(String key, Object value) {
        myRegistry.updateSubscribers(key, this);
    }

    private Properties getStateAsProperties() {
        Properties data = new Properties();
        data.setProperty("Barcode", persistentState.getProperty("Barcode", ""));
        data.setProperty("TreeType", persistentState.getProperty("TreeType", ""));
        data.setProperty("Notes", persistentState.getProperty("Notes", ""));
        data.setProperty("Status", persistentState.getProperty("Status", "Available"));
        data.setProperty("DateStatusUpdated", persistentState.getProperty("DateStatusUpdated", 
            new SimpleDateFormat("MM-dd-yyyy").format(new Date())));
        return data;
    }

    public void setState(String key, String value) {
        persistentState.setProperty(key, value);
    }

    public void save() {
        insertTree();
    }

    private void insertTree() {
        try {
            Properties props = getStateAsProperties();
            if (props.getProperty("Barcode") == null || props.getProperty("Barcode").trim().isEmpty()) {
                throw new SQLException("Barcode cannot be empty");
            }
            if (props.getProperty("TreeType") == null || props.getProperty("TreeType").trim().isEmpty()) {
                throw new SQLException("TreeType cannot be empty");
            }
            insertPersistentState(mySchema, props);
            updateStatusMessage = "Tree with barcode: " + props.getProperty("Barcode") + " inserted successfully!";
        } catch (SQLException ex) {
            updateStatusMessage = "Error saving tree: " + ex.getMessage();
            throw new RuntimeException(updateStatusMessage);
        }
    }

    private void updateStateInDatabase() {
        try {
            if (persistentState.getProperty("Barcode") != null) {
                Properties whereClause = new Properties();
                whereClause.setProperty("Barcode", persistentState.getProperty("Barcode"));
                updatePersistentState(mySchema, persistentState, whereClause);
                updateStatusMessage = "Tree data for tree with barcode: " + persistentState.getProperty("Barcode") + " updated successfully in database!";
            } else {
                throw new SQLException("Barcode cannot be null");
            }
        } catch (SQLException ex) {
            updateStatusMessage = "Error in installing tree data in database: " + ex.getMessage();
            throw new RuntimeException("Error saving tree: " + ex.getMessage());
        }
    }

    public Vector<String> getEntryListView() {
        Vector<String> view = new Vector<>();
        view.add(persistentState.getProperty("Barcode"));
        view.add(persistentState.getProperty("TreeType"));
        view.add(persistentState.getProperty("Notes"));
        view.add(persistentState.getProperty("Status"));
        view.add(persistentState.getProperty("DateStatusUpdated"));
        return view;
    }

    protected void initializeSchema(String tableName) {
        if (mySchema == null) {
            mySchema = getSchemaInfo(tableName);
        }
    }

    @Override
    public String toString() {
        return "Barcode: " + getState("Barcode") +
                ", TreeType: " + getState("TreeType") +
                ", Notes: " + getState("Notes") +
                ", Status: " + getState("Status") +
                ", DateStatusUpdated: " + getState("DateStatusUpdated");
    }

    public void display() {
        System.out.println(toString());
    }

    public void sellTree() {
        persistentState.setProperty("Status", "Sold");
        persistentState.setProperty("DateStatusUpdated", 
            new SimpleDateFormat("MM-dd-yyyy").format(new Date()));
        save();
    }

    public void markAsDamaged() {
        persistentState.setProperty("Status", "Damaged");
        persistentState.setProperty("DateStatusUpdated", 
            new SimpleDateFormat("MM-dd-yyyy").format(new Date()));
        save();
    }
}
    
    
    
