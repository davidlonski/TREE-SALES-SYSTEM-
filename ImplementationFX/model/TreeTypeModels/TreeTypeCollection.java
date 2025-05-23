package model.TreeTypeModels;

import impresario.IModel;
import impresario.IView;
import model.EntityBase;

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


    public void findByBarcodePrefix(String prefix) {
        String query = "SELECT * FROM " + myTableName + " WHERE BarcodePrefix = '" + prefix + "'";
        Vector<Properties> results = getSelectQueryResult(query);

        treeTypes.clear();
        if (results != null) {
            for (Properties props : results) {
                TreeType treeType = new TreeType(props);
                treeTypes.add(treeType);
            }
        }
    }

    public void findByDescription(String description) {
        String query = "SELECT * FROM " + myTableName + " WHERE TypeDescription LIKE '%" + description + "%'";
        Vector<Properties> results = getSelectQueryResult(query);

        treeTypes.clear();
        if (results != null) {
            for (Properties props : results) {
                TreeType treeType = new TreeType(props);
                treeTypes.add(treeType);
            }
        }
    }

    public boolean hasTreeTypes() {
        return !treeTypes.isEmpty();
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
