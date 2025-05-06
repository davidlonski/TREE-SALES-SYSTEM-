package userinterface.ScoutViews;

import javafx.beans.property.SimpleStringProperty;
import java.util.Vector;

public class ScoutTableModel {

    private final SimpleStringProperty scoutId;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty middleName;
    private final SimpleStringProperty dateOfBirth;
    private final SimpleStringProperty phoneNumber;
    private final SimpleStringProperty email;
    private final SimpleStringProperty troopId;
    private final SimpleStringProperty status;
    private final SimpleStringProperty dateStatusUpdated;


    public ScoutTableModel(Vector<String> scoutData) {
        scoutId = new SimpleStringProperty(scoutData.get(0));
        lastName = new SimpleStringProperty(scoutData.get(1));
        firstName = new SimpleStringProperty(scoutData.get(2));
        middleName = new SimpleStringProperty(scoutData.get(3));
        dateOfBirth = new SimpleStringProperty(scoutData.get(4));
        phoneNumber = new SimpleStringProperty(scoutData.get(5));
        email = new SimpleStringProperty(scoutData.get(6));
        troopId = new SimpleStringProperty(scoutData.get(7));
        status = new SimpleStringProperty(scoutData.get(8));
        dateStatusUpdated = new SimpleStringProperty(scoutData.get(9));
    }

    // Getters and setters

    public String getScoutId() {
        return scoutId.get();
    }

    public void setScoutId(String value) {
        scoutId.set(value);
    }

    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String value) {
        lastName.set(value);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public void setFirstName(String value) {
        firstName.set(value);
    }

    public String getMiddleName() {
        return middleName.get();
    }

    public void setMiddleName(String value) {
        middleName.set(value);
    }

    public String getDateOfBirth() {
        return dateOfBirth.get();
    }

    public void setDateOfBirth(String value) {
        dateOfBirth.set(value);
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public void setPhoneNumber(String value) {
        phoneNumber.set(value);
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String value) {
        email.set(value);
    }

    public String getTroopId() {
        return troopId.get();
    }

    public void setTroopId(String value) {
        troopId.set(value);
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
