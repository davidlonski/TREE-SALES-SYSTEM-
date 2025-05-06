package userinterface.TreeViews;

import javafx.beans.property.SimpleStringProperty;
import java.util.Vector;

public class TreeTableModel {

    private final SimpleStringProperty barcode;
    private final SimpleStringProperty treeType;
    private final SimpleStringProperty notes;
    private final SimpleStringProperty status;
    private final SimpleStringProperty dateStatusUpdated;

    public TreeTableModel(Vector<String> treeData) {
        barcode = new SimpleStringProperty(treeData.get(0));
        treeType = new SimpleStringProperty(treeData.get(1));
        notes = new SimpleStringProperty(treeData.get(2));
        status = new SimpleStringProperty(treeData.get(3));
        dateStatusUpdated = new SimpleStringProperty(treeData.get(4));
    }

    // Getters and setters
    public String getBarcode() {
        return barcode.get();
    }

    public void setBarcode(String value) {
        barcode.set(value);
    }

    public String getTreeType() {
        return treeType.get();
    }

    public void setTreeType(String value) {
        treeType.set(value);
    }

    public String getNotes() {
        return notes.get();
    }

    public void setNotes(String value) {
        notes.set(value);
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String value) {
        status.set(value);
    }

    public String getDateStatusUpdated() {
        return dateStatusUpdated.get();
    }

    public void setDateStatusUpdated(String value) {
        dateStatusUpdated.set(value);
    }
}
