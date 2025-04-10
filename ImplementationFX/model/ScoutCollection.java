package model;

import database.*;
import exception.*;
import impresario.*;
import java.util.Properties;
import java.util.Vector;

public class ScoutCollection extends EntityBase implements IView, impresario.IModel {

    private Vector<Scout> scouts;
    private static final String myTableName = "Scout";

    public ScoutCollection() throws Exception {
        super(myTableName);
        scouts = new Vector<>();
        String query = "SELECT * FROM " + myTableName;

        Vector<Properties> allDataRetrieved = getSelectQueryResult(query);
        if (allDataRetrieved != null) {
            for (Properties scoutData : allDataRetrieved) {
                scouts.add(new Scout(scoutData));
            }
        } else {
            throw new InvalidPrimaryKeyException("No scouts found in the database.");
        }
    }

    public void findScoutsWithLastNameLike(String lastName) {
        scouts = new Vector<>();
        String query = "SELECT * FROM " + myTableName + " WHERE LastName LIKE '%" + lastName + "%' ORDER BY FirstName ASC";
        Vector<Properties> results = getSelectQueryResult(query);
        if (results != null) {
            for (Properties props : results) {
                scouts.add(new Scout(props));
            }
        }
    }

    public Object getState(String key) {
        if (key.equals("Scouts")) return scouts;
        else if (key.equals("ScoutList")) return this;
        return null;
    }

    public void stateChangeRequest(String key, Object value) {
        myRegistry.updateSubscribers(key, this);
    }

    protected void initializeSchema(String tableName) {
        if (mySchema == null) {
            mySchema = getSchemaInfo(tableName);
        }
    }

    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }
}
