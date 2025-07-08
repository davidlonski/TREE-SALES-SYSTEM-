package com.treesales.controller;

import com.treesales.entity.TreeType;
import com.treesales.service.TreeTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tree-types")
@CrossOrigin(origins = "*")
public class TreeTypeController {
    
    @Autowired
    private TreeTypeService treeTypeService;
    
    // Create a new tree type
    @PostMapping
    public ResponseEntity<?> createTreeType(@Valid @RequestBody TreeType treeType) {
        try {
            TreeType createdTreeType = treeTypeService.createTreeType(treeType);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTreeType);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating tree type: " + e.getMessage());
        }
    }
    
    // Get tree type by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTreeTypeById(@PathVariable Long id) {
        try {
            Optional<TreeType> treeType = treeTypeService.getTreeTypeById(id);
            if (treeType.isPresent()) {
                return ResponseEntity.ok(treeType.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving tree type: " + e.getMessage());
        }
    }
    
    // Get all tree types
    @GetMapping
    public ResponseEntity<?> getAllTreeTypes() {
        try {
            List<TreeType> treeTypes = treeTypeService.getAllTreeTypes();
            return ResponseEntity.ok(treeTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving tree types: " + e.getMessage());
        }
    }
    
    // Get tree types ordered by description
    @GetMapping("/ordered/description")
    public ResponseEntity<?> getAllTreeTypesOrderedByDescription() {
        try {
            List<TreeType> treeTypes = treeTypeService.getAllTreeTypesOrderedByDescription();
            return ResponseEntity.ok(treeTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving tree types ordered by description: " + e.getMessage());
        }
    }
    
    // Get tree types ordered by cost
    @GetMapping("/ordered/cost")
    public ResponseEntity<?> getAllTreeTypesOrderedByCost() {
        try {
            List<TreeType> treeTypes = treeTypeService.getAllTreeTypesOrderedByCost();
            return ResponseEntity.ok(treeTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving tree types ordered by cost: " + e.getMessage());
        }
    }
    
    // Get tree type by description
    @GetMapping("/description/{description}")
    public ResponseEntity<?> getTreeTypeByDescription(@PathVariable String description) {
        try {
            Optional<TreeType> treeType = treeTypeService.getTreeTypeByDescription(description);
            if (treeType.isPresent()) {
                return ResponseEntity.ok(treeType.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving tree type by description: " + e.getMessage());
        }
    }
    
    // Search tree types by description
    @GetMapping("/search/description")
    public ResponseEntity<?> searchTreeTypesByDescription(@RequestParam String keyword) {
        try {
            List<TreeType> treeTypes = treeTypeService.searchTreeTypesByDescription(keyword);
            return ResponseEntity.ok(treeTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching tree types by description: " + e.getMessage());
        }
    }
    
    // Get tree types by barcode prefix
    @GetMapping("/barcode-prefix/{prefix}")
    public ResponseEntity<?> getTreeTypesByBarcodePrefix(@PathVariable String prefix) {
        try {
            List<TreeType> treeTypes = treeTypeService.getTreeTypesByBarcodePrefix(prefix);
            return ResponseEntity.ok(treeTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving tree types by barcode prefix: " + e.getMessage());
        }
    }
    
    // Get tree types by cost range
    @GetMapping("/cost-range")
    public ResponseEntity<?> getTreeTypesByCostRange(@RequestParam BigDecimal minCost, @RequestParam BigDecimal maxCost) {
        try {
            List<TreeType> treeTypes = treeTypeService.getTreeTypesByCostRange(minCost, maxCost);
            return ResponseEntity.ok(treeTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving tree types by cost range: " + e.getMessage());
        }
    }
    
    // Get tree types with cost less than or equal to
    @GetMapping("/cost-max/{maxCost}")
    public ResponseEntity<?> getTreeTypesByCostLessThanEqual(@PathVariable BigDecimal maxCost) {
        try {
            List<TreeType> treeTypes = treeTypeService.getTreeTypesByCostLessThanEqual(maxCost);
            return ResponseEntity.ok(treeTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving tree types by max cost: " + e.getMessage());
        }
    }
    
    // Get tree types with cost greater than or equal to
    @GetMapping("/cost-min/{minCost}")
    public ResponseEntity<?> getTreeTypesByCostGreaterThanEqual(@PathVariable BigDecimal minCost) {
        try {
            List<TreeType> treeTypes = treeTypeService.getTreeTypesByCostGreaterThanEqual(minCost);
            return ResponseEntity.ok(treeTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving tree types by min cost: " + e.getMessage());
        }
    }
    
    // Update tree type
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTreeType(@PathVariable Long id, @Valid @RequestBody TreeType treeType) {
        try {
            TreeType updatedTreeType = treeTypeService.updateTreeType(id, treeType);
            return ResponseEntity.ok(updatedTreeType);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating tree type: " + e.getMessage());
        }
    }
    
    // Delete tree type
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTreeType(@PathVariable Long id) {
        try {
            treeTypeService.deleteTreeType(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting tree type: " + e.getMessage());
        }
    }
    
    // Get most expensive tree types
    @GetMapping("/most-expensive")
    public ResponseEntity<?> getMostExpensiveTreeTypes() {
        try {
            List<TreeType> treeTypes = treeTypeService.getMostExpensiveTreeTypes();
            return ResponseEntity.ok(treeTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving most expensive tree types: " + e.getMessage());
        }
    }
    
    // Get least expensive tree types
    @GetMapping("/least-expensive")
    public ResponseEntity<?> getLeastExpensiveTreeTypes() {
        try {
            List<TreeType> treeTypes = treeTypeService.getLeastExpensiveTreeTypes();
            return ResponseEntity.ok(treeTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving least expensive tree types: " + e.getMessage());
        }
    }
    
    // Search tree types by barcode prefix pattern
    @GetMapping("/search/barcode-prefix")
    public ResponseEntity<?> searchTreeTypesByBarcodePrefix(@RequestParam String prefix) {
        try {
            List<TreeType> treeTypes = treeTypeService.searchTreeTypesByBarcodePrefix(prefix);
            return ResponseEntity.ok(treeTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching tree types by barcode prefix: " + e.getMessage());
        }
    }
    
    // Check if tree type exists
    @GetMapping("/{id}/exists")
    public ResponseEntity<?> checkTreeTypeExists(@PathVariable Long id) {
        try {
            boolean exists = treeTypeService.treeTypeExists(id);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking tree type existence: " + e.getMessage());
        }
    }
    
    // Check if description is in use
    @GetMapping("/check-description/{description}")
    public ResponseEntity<?> checkDescriptionInUse(@PathVariable String description) {
        try {
            boolean inUse = treeTypeService.isDescriptionInUse(description);
            return ResponseEntity.ok(inUse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking description usage: " + e.getMessage());
        }
    }
    
    // Check if barcode prefix is in use
    @GetMapping("/check-barcode-prefix/{prefix}")
    public ResponseEntity<?> checkBarcodePrefixInUse(@PathVariable String prefix) {
        try {
            boolean inUse = treeTypeService.isBarcodePrefixInUse(prefix);
            return ResponseEntity.ok(inUse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking barcode prefix usage: " + e.getMessage());
        }
    }
}