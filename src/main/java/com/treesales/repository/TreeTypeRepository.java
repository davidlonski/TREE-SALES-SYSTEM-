package com.treesales.repository;

import com.treesales.entity.TreeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TreeTypeRepository extends JpaRepository<TreeType, Long> {
    
    // Find tree type by description
    Optional<TreeType> findByTypeDescription(String typeDescription);
    
    // Find tree types by description containing keyword
    List<TreeType> findByTypeDescriptionContainingIgnoreCase(String keyword);
    
    // Find tree types by barcode prefix
    List<TreeType> findByBarcodePrefix(String barcodePrefix);
    
    // Find tree types by cost range
    List<TreeType> findByCostBetween(BigDecimal minCost, BigDecimal maxCost);
    
    // Find tree types by cost less than or equal to
    List<TreeType> findByCostLessThanEqual(BigDecimal maxCost);
    
    // Find tree types by cost greater than or equal to
    List<TreeType> findByCostGreaterThanEqual(BigDecimal minCost);
    
    // Find tree types ordered by cost
    List<TreeType> findAllByOrderByCostAsc();
    
    // Find tree types ordered by description
    List<TreeType> findAllByOrderByTypeDescriptionAsc();
    
    // Custom query to find tree types with costs in a specific range
    @Query("SELECT t FROM TreeType t WHERE t.cost BETWEEN :min AND :max ORDER BY t.cost ASC")
    List<TreeType> findTreeTypesInCostRange(@Param("min") BigDecimal min, @Param("max") BigDecimal max);
    
    // Find tree types by partial barcode prefix match
    @Query("SELECT t FROM TreeType t WHERE t.barcodePrefix LIKE %:prefix%")
    List<TreeType> findByBarcodePrefixContaining(@Param("prefix") String prefix);
    
    // Check if a tree type description already exists (case-insensitive)
    boolean existsByTypeDescriptionIgnoreCase(String typeDescription);
    
    // Check if a barcode prefix already exists
    boolean existsByBarcodePrefix(String barcodePrefix);
    
    // Find the most expensive tree type
    @Query("SELECT t FROM TreeType t WHERE t.cost = (SELECT MAX(t2.cost) FROM TreeType t2)")
    List<TreeType> findMostExpensiveTreeTypes();
    
    // Find the least expensive tree type
    @Query("SELECT t FROM TreeType t WHERE t.cost = (SELECT MIN(t2.cost) FROM TreeType t2)")
    List<TreeType> findLeastExpensiveTreeTypes();
}