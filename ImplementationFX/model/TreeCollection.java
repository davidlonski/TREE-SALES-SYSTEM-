package model;

import database.*;
import exception.*;
import impresario.*;
import java.util.Properties;
import java.util.Vector;

public class TreeCollection extends EntityBase implements IView, impresario.IModel {

    private Vector<Tree> trees;
    private static final String myTableName = "Tree";

    public TreeCollection() throws Exception {
        super(myTableName);
        trees = new Vector<>();
    }

    public void findTreesWithBarcodeLike(String barcode) {
        trees = new Vector<>();
        String query = "SELECT * FROM " + myTableName + " WHERE Barcode LIKE '%" + barcode + "%'";
        populateTrees(query);
    }

    public void findAllTrees() {
        String query = "SELECT * FROM " + myTableName;
        populateTrees(query);
    }

    private void populateTrees(String query) {
        Vector<Properties> results = getSelectQueryResult(query);
        if (results != null) {
            for (Properties data : results) {
                trees.add(new Tree(data));
            }
        }
    }

    @Override
    public Object getState(String key) {
        if (key.equals("Trees")) return trees;
        else if (key.equals("TreeList")) return this;
        return null;
    }

    @Override
    public void stateChangeRequest(String key, Object value) {
        myRegistry.updateSubscribers(key, this);
    }

    @Override
    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }

    @Override
    protected void initializeSchema(String tableName) {
        if (mySchema == null) {
            mySchema = getSchemaInfo(tableName);
        }
    }
}
