package com.treesales.controller;

import com.treesales.entity.Tree;
import com.treesales.entity.Tree.TreeStatus;
import com.treesales.service.TreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/trees")
@CrossOrigin(origins = "*")
public class TreeController {
    
    @Autowired
    private TreeService treeService;
    
    // Create a new tree
    @PostMapping
    public ResponseEntity<?> createTree(@Valid @RequestBody Tree tree) {
        try {
            treeService.validateTree(tree);
            Tree createdTree = treeService.createTree(tree);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTree);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating tree: " + e.getMessage());
        }
    }
    
    // Get tree by barcode
    @GetMapping("/{barcode}")
    public ResponseEntity<?> getTreeByBarcode(@PathVariable String barcode) {
        try {
            Optional<Tree> tree = treeService.getTreeByBarcode(barcode);
            if (tree.isPresent()) {
                return ResponseEntity.ok(tree.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving tree: " + e.getMessage());
        }
    }
    
    // Get all trees
    @GetMapping
    public ResponseEntity<?> getAllTrees() {
        try {
            List<Tree> trees = treeService.getAllTrees();
            return ResponseEntity.ok(trees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving trees: " + e.getMessage());
        }
    }
    
    // Get trees by status
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getTreesByStatus(@PathVariable String status) {
        try {
            TreeStatus treeStatus = TreeStatus.valueOf(status);
            List<Tree> trees = treeService.getTreesByStatus(treeStatus);
            return ResponseEntity.ok(trees);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status: " + status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving trees by status: " + e.getMessage());
        }
    }
    
    // Get available trees
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableTrees() {
        try {
            List<Tree> trees = treeService.getAvailableTrees();
            return ResponseEntity.ok(trees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving available trees: " + e.getMessage());
        }
    }
    
    // Get trees by tree type
    @GetMapping("/type/{treeType}")
    public ResponseEntity<?> getTreesByType(@PathVariable String treeType) {
        try {
            List<Tree> trees = treeService.getTreesByType(treeType);
            return ResponseEntity.ok(trees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving trees by type: " + e.getMessage());
        }
    }
    
    // Get trees by type and status
    @GetMapping("/type/{treeType}/status/{status}")
    public ResponseEntity<?> getTreesByTypeAndStatus(@PathVariable String treeType, @PathVariable String status) {
        try {
            TreeStatus treeStatus = TreeStatus.valueOf(status);
            List<Tree> trees = treeService.getTreesByTypeAndStatus(treeType, treeStatus);
            return ResponseEntity.ok(trees);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status: " + status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving trees by type and status: " + e.getMessage());
        }
    }
    
    // Update tree
    @PutMapping("/{barcode}")
    public ResponseEntity<?> updateTree(@PathVariable String barcode, @Valid @RequestBody Tree tree) {
        try {
            treeService.validateTree(tree);
            Tree updatedTree = treeService.updateTree(barcode, tree);
            return ResponseEntity.ok(updatedTree);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating tree: " + e.getMessage());
        }
    }
    
    // Delete tree
    @DeleteMapping("/{barcode}")
    public ResponseEntity<?> deleteTree(@PathVariable String barcode) {
        try {
            treeService.deleteTree(barcode);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting tree: " + e.getMessage());
        }
    }
    
    // Sell tree
    @PostMapping("/{barcode}/sell")
    public ResponseEntity<?> sellTree(@PathVariable String barcode) {
        try {
            Tree soldTree = treeService.sellTree(barcode);
            return ResponseEntity.ok(soldTree);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error selling tree: " + e.getMessage());
        }
    }
    
    // Mark tree as damaged
    @PostMapping("/{barcode}/damage")
    public ResponseEntity<?> markTreeAsDamaged(@PathVariable String barcode) {
        try {
            Tree damagedTree = treeService.markTreeAsDamaged(barcode);
            return ResponseEntity.ok(damagedTree);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error marking tree as damaged: " + e.getMessage());
        }
    }
    
    // Set tree as inactive
    @PostMapping("/{barcode}/inactive")
    public ResponseEntity<?> setTreeInactive(@PathVariable String barcode) {
        try {
            Tree inactiveTree = treeService.setTreeInactive(barcode);
            return ResponseEntity.ok(inactiveTree);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error setting tree as inactive: " + e.getMessage());
        }
    }
    
    // Search trees by barcode pattern
    @GetMapping("/search/barcode")
    public ResponseEntity<?> searchTreesByBarcode(@RequestParam String pattern) {
        try {
            List<Tree> trees = treeService.searchTreesByBarcode(pattern);
            return ResponseEntity.ok(trees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching trees by barcode: " + e.getMessage());
        }
    }
    
    // Search trees by notes
    @GetMapping("/search/notes")
    public ResponseEntity<?> searchTreesByNotes(@RequestParam String keyword) {
        try {
            List<Tree> trees = treeService.searchTreesByNotes(keyword);
            return ResponseEntity.ok(trees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching trees by notes: " + e.getMessage());
        }
    }
    
    // Get trees needing attention
    @GetMapping("/attention")
    public ResponseEntity<?> getTreesNeedingAttention() {
        try {
            List<Tree> trees = treeService.getTreesNeedingAttention();
            return ResponseEntity.ok(trees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving trees needing attention: " + e.getMessage());
        }
    }
    
    // Get trees updated after a date
    @GetMapping("/updated-after/{date}")
    public ResponseEntity<?> getTreesUpdatedAfter(@PathVariable String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            List<Tree> trees = treeService.getTreesUpdatedAfter(localDate);
            return ResponseEntity.ok(trees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving trees updated after date: " + e.getMessage());
        }
    }
    
    // Get count by status
    @GetMapping("/count/status/{status}")
    public ResponseEntity<?> getCountByStatus(@PathVariable String status) {
        try {
            TreeStatus treeStatus = TreeStatus.valueOf(status);
            long count = treeService.getCountByStatus(treeStatus);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status: " + status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting count by status: " + e.getMessage());
        }
    }
    
    // Get count by tree type
    @GetMapping("/count/type/{treeType}")
    public ResponseEntity<?> getCountByTreeType(@PathVariable String treeType) {
        try {
            long count = treeService.getCountByTreeType(treeType);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting count by tree type: " + e.getMessage());
        }
    }
    
    // Check if tree exists
    @GetMapping("/{barcode}/exists")
    public ResponseEntity<?> checkTreeExists(@PathVariable String barcode) {
        try {
            boolean exists = treeService.treeExists(barcode);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking tree existence: " + e.getMessage());
        }
    }
}