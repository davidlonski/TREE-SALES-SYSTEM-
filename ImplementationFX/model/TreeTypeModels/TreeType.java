package model.TreeTypeModels;

import exception.InvalidPrimaryKeyException;
import impresario.IModel;
import impresario.IView;
import model.EntityBase;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class TreeType extends EntityBase implements IModel, IView {

    private static final String myTableName = "TreeType";
    protected Properties persistentState;
    protected Properties dependencies;
    private String updateStatusMessage = "";

    public TreeType(String id) throws InvalidPrimaryKeyException {
        super(myTableName);
        setDependencies();

        String query = "SELECT * FROM " + myTableName + " WHERE ID = '" + id + "'";
        Vector<Properties> retrievedData = getSelectQueryResult(query);

        if (retrievedData == null || retrievedData.size() != 1) {
            throw new InvalidPrimaryKeyException("No TreeType found with ID: " + id);
        } else {
            persistentState = new Properties();
            Properties retrievedProps = retrievedData.elementAt(0);

            Enumeration<?> allKeys = retrievedProps.propertyNames();
            while (allKeys.hasMoreElements()) {
                String nextKey = (String) allKeys.nextElement();
                persistentState.setProperty(nextKey, retrievedProps.getProperty(nextKey));
            }
        }
    }

    public TreeType(Properties props) {
        super(myTableName);
        setDependencies();
        persistentState = new Properties();
        Enumeration<?> keys = props.propertyNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            persistentState.setProperty(key, props.getProperty(key));
        }
    }

    private void setDependencies() {
        dependencies = new Properties();
        myRegistry.setDependencies(dependencies);
    }

    @Override
    public void updateState(String key, Object value) {
        persistentState.setProperty(key, (String) value);
        myRegistry.updateSubscribers(key, this);
    }

    @Override
    public Object getState(String key) {
        if ("UpdateStatusMessage".equals(key)) {
            return updateStatusMessage;
        }
        return persistentState.getProperty(key);
    }

    public Vector<String> getEntryListView() {
        Vector<String> view = new Vector<>();
        view.addElement(persistentState.getProperty("ID"));
        view.addElement(persistentState.getProperty("TypeDescription"));
        view.addElement(persistentState.getProperty("Cost"));
        view.addElement(persistentState.getProperty("BarcodePrefix"));
        return view;
    }

    public void updateStateInDatabase() {
        try {
            if (persistentState.getProperty("ID") != null) {
                Properties whereClause = new Properties();
                whereClause.setProperty("ID", persistentState.getProperty("ID"));
                updatePersistentState(mySchema, persistentState, whereClause);
                updateStatusMessage = "TreeType with ID " + persistentState.getProperty("ID") + " updated successfully.";
            } else {
                Integer newId = insertAutoIncrementalPersistentState(mySchema, persistentState);
                persistentState.setProperty("ID", "" + newId.intValue());
                updateStatusMessage = "New TreeType inserted with ID: " + persistentState.getProperty("ID");
            }
        } catch (SQLException e) {
            updateStatusMessage = "Error saving TreeType: " + e.getMessage();
        }
    }

    public void processNewTreeType(Properties p) {
        persistentState = new Properties();
        persistentState.setProperty("TypeDescription", p.getProperty("TypeDescription"));
        persistentState.setProperty("Cost", p.getProperty("Cost"));
        persistentState.setProperty("BarcodePrefix", p.getProperty("BarcodePrefix"));

        try {
            updateStateInDatabase();
        } catch (Exception ex) {
            System.err.println("Failed to add TreeType to database.");
            ex.printStackTrace();
        }
    }

    public void save() {
        updateStateInDatabase();
    }

    @Override
    protected void initializeSchema(String tableName) {
        if (mySchema == null) {
            mySchema = getSchemaInfo(tableName);
        }
    }
    public void stateChangeRequest(String key, Object value) {
        myRegistry.updateSubscribers(key, this);
    }
}
