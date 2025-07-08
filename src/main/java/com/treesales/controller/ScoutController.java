package com.treesales.controller;

import com.treesales.entity.Scout;
import com.treesales.entity.Scout.ScoutStatus;
import com.treesales.service.ScoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/scouts")
@CrossOrigin(origins = "*")
public class ScoutController {
    
    @Autowired
    private ScoutService scoutService;
    
    // Create a new scout
    @PostMapping
    public ResponseEntity<?> createScout(@Valid @RequestBody Scout scout) {
        try {
            Scout createdScout = scoutService.createScout(scout);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdScout);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating scout: " + e.getMessage());
        }
    }
    
    // Get scout by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getScoutById(@PathVariable Long id) {
        try {
            Optional<Scout> scout = scoutService.getScoutById(id);
            if (scout.isPresent()) {
                return ResponseEntity.ok(scout.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving scout: " + e.getMessage());
        }
    }
    
    // Get all scouts
    @GetMapping
    public ResponseEntity<?> getAllScouts() {
        try {
            List<Scout> scouts = scoutService.getAllScouts();
            return ResponseEntity.ok(scouts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving scouts: " + e.getMessage());
        }
    }
    
    // Get scouts by status
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getScoutsByStatus(@PathVariable String status) {
        try {
            ScoutStatus scoutStatus = ScoutStatus.valueOf(status);
            List<Scout> scouts = scoutService.getScoutsByStatus(scoutStatus);
            return ResponseEntity.ok(scouts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status: " + status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving scouts by status: " + e.getMessage());
        }
    }
    
    // Get active scouts
    @GetMapping("/active")
    public ResponseEntity<?> getActiveScouts() {
        try {
            List<Scout> scouts = scoutService.getActiveScouts();
            return ResponseEntity.ok(scouts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving active scouts: " + e.getMessage());
        }
    }
    
    // Get scouts by troop ID
    @GetMapping("/troop/{troopId}")
    public ResponseEntity<?> getScoutsByTroopId(@PathVariable String troopId) {
        try {
            List<Scout> scouts = scoutService.getScoutsByTroopId(troopId);
            return ResponseEntity.ok(scouts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving scouts by troop ID: " + e.getMessage());
        }
    }
    
    // Get active scouts by troop ID
    @GetMapping("/troop/{troopId}/active")
    public ResponseEntity<?> getActiveScoutsByTroopId(@PathVariable String troopId) {
        try {
            List<Scout> scouts = scoutService.getActiveScoutsByTroopId(troopId);
            return ResponseEntity.ok(scouts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving active scouts by troop ID: " + e.getMessage());
        }
    }
    
    // Get scout by email
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getScoutByEmail(@PathVariable String email) {
        try {
            Optional<Scout> scout = scoutService.getScoutByEmail(email);
            if (scout.isPresent()) {
                return ResponseEntity.ok(scout.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving scout by email: " + e.getMessage());
        }
    }
    
    // Get scout by phone number
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<?> getScoutByPhoneNumber(@PathVariable String phoneNumber) {
        try {
            Optional<Scout> scout = scoutService.getScoutByPhoneNumber(phoneNumber);
            if (scout.isPresent()) {
                return ResponseEntity.ok(scout.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving scout by phone number: " + e.getMessage());
        }
    }
    
    // Search scouts by name
    @GetMapping("/search/name")
    public ResponseEntity<?> searchScoutsByName(@RequestParam String name) {
        try {
            List<Scout> scouts = scoutService.searchScoutsByName(name);
            return ResponseEntity.ok(scouts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching scouts by name: " + e.getMessage());
        }
    }
    
    // Search scouts by full name
    @GetMapping("/search/fullname")
    public ResponseEntity<?> searchScoutsByFullName(@RequestParam String fullName) {
        try {
            List<Scout> scouts = scoutService.searchScoutsByFullName(fullName);
            return ResponseEntity.ok(scouts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching scouts by full name: " + e.getMessage());
        }
    }
    
    // Get scouts by last name
    @GetMapping("/lastname/{lastName}")
    public ResponseEntity<?> getScoutsByLastName(@PathVariable String lastName) {
        try {
            List<Scout> scouts = scoutService.getScoutsByLastName(lastName);
            return ResponseEntity.ok(scouts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving scouts by last name: " + e.getMessage());
        }
    }
    
    // Get scouts by date of birth range
    @GetMapping("/dob-range")
    public ResponseEntity<?> getScoutsByDateOfBirthRange(@RequestParam String startDate, @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<Scout> scouts = scoutService.getScoutsByDateOfBirthRange(start, end);
            return ResponseEntity.ok(scouts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving scouts by date of birth range: " + e.getMessage());
        }
    }
    
    // Update scout
    @PutMapping("/{id}")
    public ResponseEntity<?> updateScout(@PathVariable Long id, @Valid @RequestBody Scout scout) {
        try {
            Scout updatedScout = scoutService.updateScout(id, scout);
            return ResponseEntity.ok(updatedScout);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating scout: " + e.getMessage());
        }
    }
    
    // Delete scout
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteScout(@PathVariable Long id) {
        try {
            scoutService.deleteScout(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting scout: " + e.getMessage());
        }
    }
    
    // Set scout as inactive
    @PostMapping("/{id}/inactive")
    public ResponseEntity<?> setScoutInactive(@PathVariable Long id) {
        try {
            Scout inactiveScout = scoutService.setScoutInactive(id);
            return ResponseEntity.ok(inactiveScout);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error setting scout as inactive: " + e.getMessage());
        }
    }
    
    // Reactivate scout
    @PostMapping("/{id}/reactivate")
    public ResponseEntity<?> reactivateScout(@PathVariable Long id) {
        try {
            Scout reactivatedScout = scoutService.reactivateScout(id);
            return ResponseEntity.ok(reactivatedScout);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reactivating scout: " + e.getMessage());
        }
    }
    
    // Get scouts updated after a date
    @GetMapping("/updated-after/{date}")
    public ResponseEntity<?> getScoutsUpdatedAfter(@PathVariable String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            List<Scout> scouts = scoutService.getScoutsUpdatedAfter(localDate);
            return ResponseEntity.ok(scouts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving scouts updated after date: " + e.getMessage());
        }
    }
    
    // Get count by status
    @GetMapping("/count/status/{status}")
    public ResponseEntity<?> getCountByStatus(@PathVariable String status) {
        try {
            ScoutStatus scoutStatus = ScoutStatus.valueOf(status);
            long count = scoutService.getCountByStatus(scoutStatus);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status: " + status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting count by status: " + e.getMessage());
        }
    }
    
    // Get count by troop ID
    @GetMapping("/count/troop/{troopId}")
    public ResponseEntity<?> getCountByTroopId(@PathVariable String troopId) {
        try {
            long count = scoutService.getCountByTroopId(troopId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting count by troop ID: " + e.getMessage());
        }
    }
    
    // Check if scout exists
    @GetMapping("/{id}/exists")
    public ResponseEntity<?> checkScoutExists(@PathVariable Long id) {
        try {
            boolean exists = scoutService.scoutExists(id);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking scout existence: " + e.getMessage());
        }
    }
    
    // Check if email is in use
    @GetMapping("/check-email/{email}")
    public ResponseEntity<?> checkEmailInUse(@PathVariable String email) {
        try {
            boolean inUse = scoutService.isEmailInUse(email);
            return ResponseEntity.ok(inUse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking email usage: " + e.getMessage());
        }
    }
    
    // Check if phone number is in use
    @GetMapping("/check-phone/{phoneNumber}")
    public ResponseEntity<?> checkPhoneNumberInUse(@PathVariable String phoneNumber) {
        try {
            boolean inUse = scoutService.isPhoneNumberInUse(phoneNumber);
            return ResponseEntity.ok(inUse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking phone number usage: " + e.getMessage());
        }
    }
}