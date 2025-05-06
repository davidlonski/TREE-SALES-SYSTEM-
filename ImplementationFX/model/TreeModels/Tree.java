package model.TreeModels;

import exception.*;
import impresario.*;
import model.EntityBase;

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
        if (status != null && !status.equals("Available") && !status.equals("Sold") 
            && !status.equals("Damaged") && !status.equals("Inactive")) {
            throw new IllegalArgumentException("Status must be one of: Available, Sold, Damaged, or Inactive");
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
        updateStateInDatabase();
    }

    private void updateStateInDatabase() {
        try {
            if (persistentState.getProperty("Barcode") != null) {
                // Check if tree exists
                String query = "SELECT * FROM " + myTableName + " WHERE Barcode = '" + persistentState.getProperty("Barcode") + "'";
                Vector<Properties> result = getSelectQueryResult(query);
                
                if (result != null && result.size() > 0) {
                    // Update existing tree
                    Properties whereClause = new Properties();
                    whereClause.setProperty("Barcode", persistentState.getProperty("Barcode"));
                    updatePersistentState(mySchema, persistentState, whereClause);
                    updateStatusMessage = "Tree data for barcode: " + persistentState.getProperty("Barcode") + " updated successfully in database!";
                } else {
                    // Insert new tree
                    if (persistentState.getProperty("TreeType") == null || persistentState.getProperty("Status") == null) {
                        throw new RuntimeException("Required fields (TreeType, Status) are missing");
                    }
                    insertPersistentState(mySchema, persistentState);
                    updateStatusMessage = "New tree data installed successfully in database!";
                }
            } else {
                // Insert new tree without barcode check
                if (persistentState.getProperty("TreeType") == null || persistentState.getProperty("Status") == null) {
                    throw new RuntimeException("Required fields (TreeType, Status) are missing");
                }
                insertPersistentState(mySchema, persistentState);
                updateStatusMessage = "New tree data installed successfully in database!";
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

    public void setInactive() throws SQLException {
        setState("Status", "Inactive");
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(new java.util.Date());
        setState("DateStatusUpdated", currentDate);
        
        save();
    }
}
    
    
    
