package com.treesales.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

@Entity
@Table(name = "Scout")
public class Scout {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    
    @Column(name = "FirstName", nullable = false, length = 20)
    @NotBlank(message = "First name is required")
    @Size(max = 20, message = "First name must be 20 characters or fewer")
    private String firstName;
    
    @Column(name = "MiddleName", length = 20)
    @Size(max = 20, message = "Middle name must be 20 characters or fewer")
    private String middleName;
    
    @Column(name = "LastName", nullable = false, length = 20)
    @NotBlank(message = "Last name is required")
    @Size(max = 20, message = "Last name must be 20 characters or fewer")
    private String lastName;
    
    @Column(name = "DateOfBirth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    
    @Column(name = "PhoneNumber")
    private String phoneNumber;
    
    @Column(name = "Email")
    @Email(message = "Please provide a valid email address")
    private String email;
    
    @Column(name = "TroopID")
    private String troopId;
    
    @Column(name = "Status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ScoutStatus status = ScoutStatus.Active;
    
    @Column(name = "DateStatusUpdated")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateStatusUpdated;
    
    // Constructors
    public Scout() {
        this.dateStatusUpdated = LocalDate.now();
    }
    
    public Scout(String firstName, String middleName, String lastName, LocalDate dateOfBirth, 
                 String phoneNumber, String email, String troopId) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.troopId = troopId;
        this.status = ScoutStatus.Active;
        this.dateStatusUpdated = LocalDate.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getMiddleName() {
        return middleName;
    }
    
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTroopId() {
        return troopId;
    }
    
    public void setTroopId(String troopId) {
        this.troopId = troopId;
    }
    
    public ScoutStatus getStatus() {
        return status;
    }
    
    public void setStatus(ScoutStatus status) {
        this.status = status;
        this.dateStatusUpdated = LocalDate.now();
    }
    
    public LocalDate getDateStatusUpdated() {
        return dateStatusUpdated;
    }
    
    public void setDateStatusUpdated(LocalDate dateStatusUpdated) {
        this.dateStatusUpdated = dateStatusUpdated;
    }
    
    // Business Methods
    public void setInactive() {
        this.status = ScoutStatus.Inactive;
        this.dateStatusUpdated = LocalDate.now();
    }
    
    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        fullName.append(firstName);
        if (middleName != null && !middleName.trim().isEmpty()) {
            fullName.append(" ").append(middleName);
        }
        fullName.append(" ").append(lastName);
        return fullName.toString();
    }
    
    @Override
    public String toString() {
        return "Scout{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", troopId='" + troopId + '\'' +
                ", status=" + status +
                ", dateStatusUpdated=" + dateStatusUpdated +
                '}';
    }
    
    // Enum for Scout Status
    public enum ScoutStatus {
        Active, Inactive
    }
}