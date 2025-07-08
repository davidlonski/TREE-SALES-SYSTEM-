package com.treesales.repository;

import com.treesales.entity.Tree;
import com.treesales.entity.Tree.TreeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TreeRepository extends JpaRepository<Tree, String> {
    
    // Find trees by status
    List<Tree> findByStatus(TreeStatus status);
    
    // Find trees by tree type
    List<Tree> findByTreeType(String treeType);
    
    // Find trees by tree type and status
    List<Tree> findByTreeTypeAndStatus(String treeType, TreeStatus status);
    
    // Find available trees
    List<Tree> findByStatusOrderByTreeType(TreeStatus status);
    
    // Find trees updated after a certain date
    List<Tree> findByDateStatusUpdatedAfter(LocalDate date);
    
    // Find trees by barcode pattern (partial match)
    @Query("SELECT t FROM Tree t WHERE t.barcode LIKE %:pattern%")
    List<Tree> findByBarcodeContaining(@Param("pattern") String pattern);
    
    // Find trees by notes containing keywords
    @Query("SELECT t FROM Tree t WHERE t.notes LIKE %:keyword%")
    List<Tree> findByNotesContaining(@Param("keyword") String keyword);
    
    // Count trees by status
    long countByStatus(TreeStatus status);
    
    // Count trees by tree type
    long countByTreeType(String treeType);
    
    // Custom query to find trees that need attention (damaged or inactive)
    @Query("SELECT t FROM Tree t WHERE t.status IN ('Damaged', 'Inactive') ORDER BY t.dateStatusUpdated DESC")
    List<Tree> findTreesNeedingAttention();
    
    // Find trees by multiple statuses
    List<Tree> findByStatusIn(List<TreeStatus> statuses);
}