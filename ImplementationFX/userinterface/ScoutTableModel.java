package userinterface;

import javafx.beans.property.SimpleStringProperty;
import java.util.Vector;

/**
 * Table model representation for scouts in the system.
 */
public class ScoutTableModel {

    private final SimpleStringProperty scoutId;
    private final SimpleStringProperty name;
    private final SimpleStringProperty phoneNumber;
    private final SimpleStringProperty status;

    /**
     * Constructor: Takes a vector containing scout details.
     * Expected order:
     * [0] - Scout ID
     * [1] - Full Name
     * [2] - Phone Number
     * [3] - Status
     */
    public ScoutTableModel(Vector<String> scoutData) {
        scoutId = new SimpleStringProperty(scoutData.get(0));
        name = new SimpleStringProperty(scoutData.get(1));
        phoneNumber = new SimpleStringProperty(scoutData.get(2));
        status = new SimpleStringProperty(scoutData.get(3));
    }

    // Getters and setters

    public String getScoutId() {
        return scoutId.get();
    }

    public void setScoutId(String value) {
        scoutId.set(value);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public void setPhoneNumber(String value) {
        phoneNumber.set(value);
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String value) {
        status.set(value);
    }
}
