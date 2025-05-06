package userinterface.TreeTypeViews;

import javafx.beans.property.SimpleStringProperty;
import java.util.Vector;

public class TreeTypeTableModel {

    private final SimpleStringProperty id;
    private final SimpleStringProperty typeDescription;
    private final SimpleStringProperty cost;
    private final SimpleStringProperty barcodePrefix;

    public TreeTypeTableModel(Vector<String> treeTypeData) {
        id = new SimpleStringProperty(treeTypeData.get(0));
        typeDescription = new SimpleStringProperty(treeTypeData.get(1));
        cost = new SimpleStringProperty(treeTypeData.get(2));
        barcodePrefix = new SimpleStringProperty(treeTypeData.get(3));
    }

    // Getters and setters
    public String getId() {
        return id.get();
    }

    public void setId(String value) {
        id.set(value);
    }

    public String getTypeDescription() {
        return typeDescription.get();
    }

    public void setTypeDescription(String value) {
        typeDescription.set(value);
    }

    public String getCost() {
        return cost.get();
    }

    public void setCost(String value) {
        cost.set(value);
    }

    public String getBarcodePrefix() {
        return barcodePrefix.get();
    }

    public void setBarcodePrefix(String value) {
        barcodePrefix.set(value);
    }
}
