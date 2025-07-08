package com.treesales.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

@Entity
@Table(name = "Tree")
public class Tree {
    
    @Id
    @Column(name = "Barcode", length = 20)
    @NotBlank(message = "Barcode is required")
    @Size(max = 20, message = "Barcode must be 20 characters or fewer")
    private String barcode;
    
    @Column(name = "TreeType", nullable = false)
    @NotBlank(message = "TreeType is required")
    private String treeType;
    
    @Column(name = "Notes", length = 200)
    @Size(max = 200, message = "Notes must be 200 characters or fewer")
    private String notes;
    
    @Column(name = "Status", nullable = false)
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private TreeStatus status = TreeStatus.Available;
    
    @Column(name = "DateStatusUpdated")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateStatusUpdated;
    
    // Constructors
    public Tree() {
        this.dateStatusUpdated = LocalDate.now();
    }
    
    public Tree(String barcode, String treeType, String notes, TreeStatus status) {
        this.barcode = barcode;
        this.treeType = treeType;
        this.notes = notes;
        this.status = status != null ? status : TreeStatus.Available;
        this.dateStatusUpdated = LocalDate.now();
    }
    
    // Getters and Setters
    public String getBarcode() {
        return barcode;
    }
    
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    
    public String getTreeType() {
        return treeType;
    }
    
    public void setTreeType(String treeType) {
        this.treeType = treeType;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public TreeStatus getStatus() {
        return status;
    }
    
    public void setStatus(TreeStatus status) {
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
    public void sellTree() {
        this.status = TreeStatus.Sold;
        this.dateStatusUpdated = LocalDate.now();
    }
    
    public void markAsDamaged() {
        this.status = TreeStatus.Damaged;
        this.dateStatusUpdated = LocalDate.now();
    }
    
    public void setInactive() {
        this.status = TreeStatus.Inactive;
        this.dateStatusUpdated = LocalDate.now();
    }
    
    @Override
    public String toString() {
        return "Tree{" +
                "barcode='" + barcode + '\'' +
                ", treeType='" + treeType + '\'' +
                ", notes='" + notes + '\'' +
                ", status=" + status +
                ", dateStatusUpdated=" + dateStatusUpdated +
                '}';
    }
    
    // Enum for Tree Status
    public enum TreeStatus {
        Available, Sold, Damaged, Inactive
    }
}