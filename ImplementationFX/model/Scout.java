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



    public Scout() {
        super(myTableName);

        persistentState = new Properties();
    }

    public Scout(String id) throws InvalidPrimaryKeyException {
        super(myTableName);
        setDependencies();
        String query = "SELECT * FROM " + myTableName + " WHERE (ID = " + id + ")";
        Vector<Properties> result = getSelectQueryResult(query);

        if (result == null || result.size() != 1) {
            throw new InvalidPrimaryKeyException("No scout found with ID: " + id);
        } else {
            persistentState = result.firstElement();
        }
    }

    public Scout(Properties props) {
        super(myTableName);
        setDependencies();
        persistentState = new Properties();

        String firstName = props.getProperty("FirstName");
        String middleName = props.getProperty("MiddleName");
        String lastName = props.getProperty("LastName");

        if (firstName != null && firstName.length() > 20) {
            throw new IllegalArgumentException("First name must be 20 characters or fewer.");
        }
        if (middleName != null && middleName.length() > 20) {
            throw new IllegalArgumentException("Middle name must be 20 characters or fewer.");
        }
        if (lastName != null && lastName.length() > 20) {
            throw new IllegalArgumentException("Last name must be 20 characters or fewer.");
        }

        Enumeration allKeys = props.propertyNames();
        while (allKeys.hasMoreElements()) {
            String nextKey = (String) allKeys.nextElement();
            String value = props.getProperty(nextKey);
            if(nextKey != null) {
                persistentState.setProperty(nextKey, value);
            }
        }
    }

    private void setDependencies() {
        dependencies = new Properties();

        myRegistry.setDependencies(dependencies);

    }

    public Object getState(String key) {
        if(key.equals("UpdateStatusMessage")) {
            return updateStatusMessage;
        }
        return persistentState.getProperty(key);
    }

    public void stateChangeRequest(String key, Object value) {
        myRegistry.updateSubscribers(key, this);
    }

    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
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

    public void save() {
        updateStateInDatabase();
    }

    private void updateStateInDatabase(){
        try
        {
            if (persistentState.getProperty("ID") != null)
            {
                // update
                Properties whereClause = new Properties();
                whereClause.setProperty("ID",
                        persistentState.getProperty("ID"));
                updatePersistentState(mySchema, persistentState, whereClause);
                updateStatusMessage = "Scout data for scout ID : " + persistentState.getProperty("ID") + " updated successfully in database!";
            }
            else
            {
                // insert
                Integer ID =
                        insertAutoIncrementalPersistentState(mySchema, persistentState);
                persistentState.setProperty("ID", "" + ID);
                updateStatusMessage = "Scout data for new scout ID : " +  persistentState.getProperty("ID")
                        + " installed successfully in database!";
            }
        }
        catch (SQLException ex)
        {
            updateStatusMessage = "Error in installing scout data in database!";
        }
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


    protected void initializeSchema(String tableName) {
        if (mySchema == null) {
            mySchema = getSchemaInfo(tableName);
            if (mySchema.getProperty("insertType") == null) {
                mySchema.setProperty("insertType", "AUTOINCREMENT");
            }
        }
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

    public void display(){
        System.out.println(this.toString());
    }
}
