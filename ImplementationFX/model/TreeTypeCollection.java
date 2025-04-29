package model;

import database.Persistable;
import impresario.IModel;
import impresario.IView;
import event.Event;

import java.util.Vector;
import java.util.Properties;

public class TreeTypeCollection extends EntityBase implements IModel, IView {

    private static final String myTableName = "TreeType";
    private Vector<TreeType> treeTypes;

    public TreeTypeCollection() {
        super(myTableName);
        treeTypes = new Vector<>();
    }

    public void findAllTreeTypes() {
        String query = "SELECT * FROM " + myTableName;
        Vector<Properties> allDataRetrieved = getSelectQueryResult(query);

        if (allDataRetrieved != null) {
            for (Properties props : allDataRetrieved) {
                TreeType treeType = new TreeType(props);
                treeTypes.add(treeType);
            }
        }
    }

    @Override
    public Object getState(String key) {
        if ("TreeTypes".equals(key)) {
            return treeTypes;
        }
        return null;
    }

    @Override
    public void updateState(String key, Object value) {
        // No state updates required here for now
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
