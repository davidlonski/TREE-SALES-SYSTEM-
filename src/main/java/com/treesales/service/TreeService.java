package com.treesales.service;

import com.treesales.entity.Tree;
import com.treesales.entity.Tree.TreeStatus;
import com.treesales.repository.TreeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TreeService {
    
    @Autowired
    private TreeRepository treeRepository;
    
    // Create a new tree
    public Tree createTree(Tree tree) {
        if (tree.getDateStatusUpdated() == null) {
            tree.setDateStatusUpdated(LocalDate.now());
        }
        return treeRepository.save(tree);
    }
    
    // Get tree by barcode
    public Optional<Tree> getTreeByBarcode(String barcode) {
        return treeRepository.findById(barcode);
    }
    
    // Get all trees
    public List<Tree> getAllTrees() {
        return treeRepository.findAll();
    }
    
    // Get trees by status
    public List<Tree> getTreesByStatus(TreeStatus status) {
        return treeRepository.findByStatus(status);
    }
    
    // Get available trees ordered by tree type
    public List<Tree> getAvailableTrees() {
        return treeRepository.findByStatusOrderByTreeType(TreeStatus.Available);
    }
    
    // Get trees by tree type
    public List<Tree> getTreesByType(String treeType) {
        return treeRepository.findByTreeType(treeType);
    }
    
    // Get trees by type and status
    public List<Tree> getTreesByTypeAndStatus(String treeType, TreeStatus status) {
        return treeRepository.findByTreeTypeAndStatus(treeType, status);
    }
    
    // Update tree
    public Tree updateTree(String barcode, Tree updatedTree) {
        Optional<Tree> existingTree = treeRepository.findById(barcode);
        if (existingTree.isPresent()) {
            Tree tree = existingTree.get();
            tree.setTreeType(updatedTree.getTreeType());
            tree.setNotes(updatedTree.getNotes());
            tree.setStatus(updatedTree.getStatus());
            tree.setDateStatusUpdated(LocalDate.now());
            return treeRepository.save(tree);
        }
        throw new RuntimeException("Tree not found with barcode: " + barcode);
    }
    
    // Delete tree
    public void deleteTree(String barcode) {
        if (treeRepository.existsById(barcode)) {
            treeRepository.deleteById(barcode);
        } else {
            throw new RuntimeException("Tree not found with barcode: " + barcode);
        }
    }
    
    // Sell tree
    public Tree sellTree(String barcode) {
        Optional<Tree> treeOptional = treeRepository.findById(barcode);
        if (treeOptional.isPresent()) {
            Tree tree = treeOptional.get();
            if (tree.getStatus() == TreeStatus.Available) {
                tree.sellTree();
                return treeRepository.save(tree);
            } else {
                throw new RuntimeException("Tree is not available for sale. Current status: " + tree.getStatus());
            }
        }
        throw new RuntimeException("Tree not found with barcode: " + barcode);
    }
    
    // Mark tree as damaged
    public Tree markTreeAsDamaged(String barcode) {
        Optional<Tree> treeOptional = treeRepository.findById(barcode);
        if (treeOptional.isPresent()) {
            Tree tree = treeOptional.get();
            tree.markAsDamaged();
            return treeRepository.save(tree);
        }
        throw new RuntimeException("Tree not found with barcode: " + barcode);
    }
    
    // Set tree as inactive
    public Tree setTreeInactive(String barcode) {
        Optional<Tree> treeOptional = treeRepository.findById(barcode);
        if (treeOptional.isPresent()) {
            Tree tree = treeOptional.get();
            tree.setInactive();
            return treeRepository.save(tree);
        }
        throw new RuntimeException("Tree not found with barcode: " + barcode);
    }
    
    // Search trees by barcode pattern
    public List<Tree> searchTreesByBarcode(String pattern) {
        return treeRepository.findByBarcodeContaining(pattern);
    }
    
    // Search trees by notes
    public List<Tree> searchTreesByNotes(String keyword) {
        return treeRepository.findByNotesContaining(keyword);
    }
    
    // Get trees needing attention (damaged or inactive)
    public List<Tree> getTreesNeedingAttention() {
        return treeRepository.findTreesNeedingAttention();
    }
    
    // Get trees updated after a specific date
    public List<Tree> getTreesUpdatedAfter(LocalDate date) {
        return treeRepository.findByDateStatusUpdatedAfter(date);
    }
    
    // Get count by status
    public long getCountByStatus(TreeStatus status) {
        return treeRepository.countByStatus(status);
    }
    
    // Get count by tree type
    public long getCountByTreeType(String treeType) {
        return treeRepository.countByTreeType(treeType);
    }
    
    // Validate tree data
    public void validateTree(Tree tree) {
        if (tree.getBarcode() == null || tree.getBarcode().trim().isEmpty()) {
            throw new IllegalArgumentException("Barcode is required");
        }
        if (tree.getTreeType() == null || tree.getTreeType().trim().isEmpty()) {
            throw new IllegalArgumentException("TreeType is required");
        }
        if (tree.getBarcode().length() > 20) {
            throw new IllegalArgumentException("Barcode must be 20 characters or fewer");
        }
        if (tree.getNotes() != null && tree.getNotes().length() > 200) {
            throw new IllegalArgumentException("Notes must be 200 characters or fewer");
        }
    }
    
    // Check if tree exists
    public boolean treeExists(String barcode) {
        return treeRepository.existsById(barcode);
    }
}