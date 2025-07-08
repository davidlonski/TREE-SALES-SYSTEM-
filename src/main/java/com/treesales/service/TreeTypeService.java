package com.treesales.service;

import com.treesales.entity.TreeType;
import com.treesales.repository.TreeTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TreeTypeService {
    
    @Autowired
    private TreeTypeRepository treeTypeRepository;
    
    // Create a new tree type
    public TreeType createTreeType(TreeType treeType) {
        validateTreeType(treeType);
        return treeTypeRepository.save(treeType);
    }
    
    // Get tree type by ID
    public Optional<TreeType> getTreeTypeById(Long id) {
        return treeTypeRepository.findById(id);
    }
    
    // Get all tree types
    public List<TreeType> getAllTreeTypes() {
        return treeTypeRepository.findAll();
    }
    
    // Get tree types ordered by description
    public List<TreeType> getAllTreeTypesOrderedByDescription() {
        return treeTypeRepository.findAllByOrderByTypeDescriptionAsc();
    }
    
    // Get tree types ordered by cost
    public List<TreeType> getAllTreeTypesOrderedByCost() {
        return treeTypeRepository.findAllByOrderByCostAsc();
    }
    
    // Get tree type by description
    public Optional<TreeType> getTreeTypeByDescription(String description) {
        return treeTypeRepository.findByTypeDescription(description);
    }
    
    // Search tree types by description
    public List<TreeType> searchTreeTypesByDescription(String keyword) {
        return treeTypeRepository.findByTypeDescriptionContainingIgnoreCase(keyword);
    }
    
    // Get tree types by barcode prefix
    public List<TreeType> getTreeTypesByBarcodePrefix(String prefix) {
        return treeTypeRepository.findByBarcodePrefix(prefix);
    }
    
    // Get tree types by cost range
    public List<TreeType> getTreeTypesByCostRange(BigDecimal minCost, BigDecimal maxCost) {
        return treeTypeRepository.findTreeTypesInCostRange(minCost, maxCost);
    }
    
    // Get tree types with cost less than or equal to
    public List<TreeType> getTreeTypesByCostLessThanEqual(BigDecimal maxCost) {
        return treeTypeRepository.findByCostLessThanEqual(maxCost);
    }
    
    // Get tree types with cost greater than or equal to
    public List<TreeType> getTreeTypesByCostGreaterThanEqual(BigDecimal minCost) {
        return treeTypeRepository.findByCostGreaterThanEqual(minCost);
    }
    
    // Update tree type
    public TreeType updateTreeType(Long id, TreeType updatedTreeType) {
        Optional<TreeType> existingTreeType = treeTypeRepository.findById(id);
        if (existingTreeType.isPresent()) {
            TreeType treeType = existingTreeType.get();
            treeType.setTypeDescription(updatedTreeType.getTypeDescription());
            treeType.setCost(updatedTreeType.getCost());
            treeType.setBarcodePrefix(updatedTreeType.getBarcodePrefix());
            
            validateTreeType(treeType);
            return treeTypeRepository.save(treeType);
        }
        throw new RuntimeException("TreeType not found with ID: " + id);
    }
    
    // Delete tree type
    public void deleteTreeType(Long id) {
        if (treeTypeRepository.existsById(id)) {
            treeTypeRepository.deleteById(id);
        } else {
            throw new RuntimeException("TreeType not found with ID: " + id);
        }
    }
    
    // Get most expensive tree types
    public List<TreeType> getMostExpensiveTreeTypes() {
        return treeTypeRepository.findMostExpensiveTreeTypes();
    }
    
    // Get least expensive tree types
    public List<TreeType> getLeastExpensiveTreeTypes() {
        return treeTypeRepository.findLeastExpensiveTreeTypes();
    }
    
    // Search tree types by barcode prefix pattern
    public List<TreeType> searchTreeTypesByBarcodePrefix(String prefix) {
        return treeTypeRepository.findByBarcodePrefixContaining(prefix);
    }
    
    // Validate tree type data
    public void validateTreeType(TreeType treeType) {
        if (treeType.getTypeDescription() == null || treeType.getTypeDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Type description is required");
        }
        if (treeType.getCost() == null) {
            throw new IllegalArgumentException("Cost is required");
        }
        if (treeType.getCost().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cost must be greater than 0");
        }
        
        // Check for duplicate descriptions (case-insensitive)
        if (treeTypeRepository.existsByTypeDescriptionIgnoreCase(treeType.getTypeDescription())) {
            // If updating, check if it's not the same record
            if (treeType.getId() == null || 
                !treeTypeRepository.findById(treeType.getId())
                    .map(existing -> existing.getTypeDescription().equalsIgnoreCase(treeType.getTypeDescription()))
                    .orElse(false)) {
                throw new IllegalArgumentException("A tree type with this description already exists");
            }
        }
        
        // Check for duplicate barcode prefix
        if (treeType.getBarcodePrefix() != null && 
            treeTypeRepository.existsByBarcodePrefix(treeType.getBarcodePrefix())) {
            // If updating, check if it's not the same record
            if (treeType.getId() == null ||
                !treeTypeRepository.findById(treeType.getId())
                    .map(existing -> treeType.getBarcodePrefix().equals(existing.getBarcodePrefix()))
                    .orElse(false)) {
                throw new IllegalArgumentException("A tree type with this barcode prefix already exists");
            }
        }
    }
    
    // Check if tree type exists
    public boolean treeTypeExists(Long id) {
        return treeTypeRepository.existsById(id);
    }
    
    // Check if description is already in use
    public boolean isDescriptionInUse(String description) {
        return treeTypeRepository.existsByTypeDescriptionIgnoreCase(description);
    }
    
    // Check if barcode prefix is already in use
    public boolean isBarcodePrefixInUse(String prefix) {
        return treeTypeRepository.existsByBarcodePrefix(prefix);
    }
}