package model;

import impresario.IModel;
import impresario.IView;

import java.util.Vector;
import java.util.Properties;

public class TreeCollection extends EntityBase implements IModel, IView {

    private static final String myTableName = "TreeType";
    private Vector<Tree> treeC;

    public TreeCollection() {
        super(myTableName);
        treeC = new Vector<>();
    }

    public void findAllTrees() {
        String query = "SELECT * FROM " + myTableName;
        Vector<Properties> allDataRetrieved = getSelectQueryResult(query);

        if (allDataRetrieved != null) {
            for (Properties props : allDataRetrieved) {
                Tree treeCo = new Tree(props);
                treeC.add(treeCo);
            }
        }
    }

    @Override
    public Object getState(String key) {
        if ("Tree".equals(key)) {
            return treeC;
        }
        return null;
    }

    @Override
    public void updateState(String key, Object value) {

    }

    @Override
    public void initializeSchema(String tableName) {
        if (mySchema == null) {
            mySchema = getSchemaInfo(tableName);
        }
    }

    public void stateChangeRequest(String key, Object value) {
        myRegistry.updateSubscribers(key, this);
    }

}
