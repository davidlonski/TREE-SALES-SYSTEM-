package model.ScoutModels;

import exception.*;
import impresario.*;
import model.EntityBase;

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

    public void findScoutsWithLastNameLike(String name) {
        scouts = new Vector<>();
        String query = "SELECT * FROM " + myTableName + " WHERE LastName LIKE '%" + name + "%'";
        populateScouts(query);
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

    private void populateScouts(String query) {
        Vector<Properties> results = getSelectQueryResult(query);
        if (results != null) {
            for (Properties data : results) {
                scouts.add(new Scout(data));
            }
        }
    }

    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }
}
