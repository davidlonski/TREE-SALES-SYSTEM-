package com.treesales.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

@Entity
@Table(name = "TreeType")
public class TreeType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    
    @Column(name = "TypeDescription", nullable = false)
    @NotBlank(message = "Type description is required")
    private String typeDescription;
    
    @Column(name = "Cost", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Cost is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cost must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Cost must be a valid monetary amount")
    private BigDecimal cost;
    
    @Column(name = "BarcodePrefix")
    private String barcodePrefix;
    
    // Constructors
    public TreeType() {}
    
    public TreeType(String typeDescription, BigDecimal cost, String barcodePrefix) {
        this.typeDescription = typeDescription;
        this.cost = cost;
        this.barcodePrefix = barcodePrefix;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTypeDescription() {
        return typeDescription;
    }
    
    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }
    
    public BigDecimal getCost() {
        return cost;
    }
    
    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
    
    public String getBarcodePrefix() {
        return barcodePrefix;
    }
    
    public void setBarcodePrefix(String barcodePrefix) {
        this.barcodePrefix = barcodePrefix;
    }
    
    @Override
    public String toString() {
        return "TreeType{" +
                "id=" + id +
                ", typeDescription='" + typeDescription + '\'' +
                ", cost=" + cost +
                ", barcodePrefix='" + barcodePrefix + '\'' +
                '}';
    }
}