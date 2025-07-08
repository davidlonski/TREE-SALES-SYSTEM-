package com.treesales.service;

import com.treesales.entity.Scout;
import com.treesales.entity.Scout.ScoutStatus;
import com.treesales.repository.ScoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ScoutService {
    
    @Autowired
    private ScoutRepository scoutRepository;
    
    // Create a new scout
    public Scout createScout(Scout scout) {
        validateScout(scout);
        if (scout.getDateStatusUpdated() == null) {
            scout.setDateStatusUpdated(LocalDate.now());
        }
        return scoutRepository.save(scout);
    }
    
    // Get scout by ID
    public Optional<Scout> getScoutById(Long id) {
        return scoutRepository.findById(id);
    }
    
    // Get all scouts
    public List<Scout> getAllScouts() {
        return scoutRepository.findAll();
    }
    
    // Get scouts by status
    public List<Scout> getScoutsByStatus(ScoutStatus status) {
        return scoutRepository.findByStatus(status);
    }
    
    // Get active scouts ordered by name
    public List<Scout> getActiveScouts() {
        return scoutRepository.findByStatusOrderByLastNameAscFirstNameAsc(ScoutStatus.Active);
    }
    
    // Get scouts by troop ID
    public List<Scout> getScoutsByTroopId(String troopId) {
        return scoutRepository.findByTroopId(troopId);
    }
    
    // Get active scouts by troop ID
    public List<Scout> getActiveScoutsByTroopId(String troopId) {
        return scoutRepository.findByTroopIdAndStatus(troopId, ScoutStatus.Active);
    }
    
    // Get scout by email
    public Optional<Scout> getScoutByEmail(String email) {
        return scoutRepository.findByEmail(email);
    }
    
    // Get scout by phone number
    public Optional<Scout> getScoutByPhoneNumber(String phoneNumber) {
        return scoutRepository.findByPhoneNumber(phoneNumber);
    }
    
    // Search scouts by name
    public List<Scout> searchScoutsByName(String name) {
        return scoutRepository.findByNameContaining(name);
    }
    
    // Search scouts by full name
    public List<Scout> searchScoutsByFullName(String fullName) {
        return scoutRepository.findByFullNameContaining(fullName);
    }
    
    // Get scouts by last name
    public List<Scout> getScoutsByLastName(String lastName) {
        return scoutRepository.findByLastNameOrderByFirstName(lastName);
    }
    
    // Get scouts by date of birth range
    public List<Scout> getScoutsByDateOfBirthRange(LocalDate startDate, LocalDate endDate) {
        return scoutRepository.findByDateOfBirthBetween(startDate, endDate);
    }
    
    // Update scout
    public Scout updateScout(Long id, Scout updatedScout) {
        Optional<Scout> existingScout = scoutRepository.findById(id);
        if (existingScout.isPresent()) {
            Scout scout = existingScout.get();
            scout.setFirstName(updatedScout.getFirstName());
            scout.setMiddleName(updatedScout.getMiddleName());
            scout.setLastName(updatedScout.getLastName());
            scout.setDateOfBirth(updatedScout.getDateOfBirth());
            scout.setPhoneNumber(updatedScout.getPhoneNumber());
            scout.setEmail(updatedScout.getEmail());
            scout.setTroopId(updatedScout.getTroopId());
            scout.setStatus(updatedScout.getStatus());
            scout.setDateStatusUpdated(LocalDate.now());
            
            validateScout(scout);
            return scoutRepository.save(scout);
        }
        throw new RuntimeException("Scout not found with ID: " + id);
    }
    
    // Delete scout
    public void deleteScout(Long id) {
        if (scoutRepository.existsById(id)) {
            scoutRepository.deleteById(id);
        } else {
            throw new RuntimeException("Scout not found with ID: " + id);
        }
    }
    
    // Set scout as inactive
    public Scout setScoutInactive(Long id) {
        Optional<Scout> scoutOptional = scoutRepository.findById(id);
        if (scoutOptional.isPresent()) {
            Scout scout = scoutOptional.get();
            scout.setInactive();
            return scoutRepository.save(scout);
        }
        throw new RuntimeException("Scout not found with ID: " + id);
    }
    
    // Reactivate scout
    public Scout reactivateScout(Long id) {
        Optional<Scout> scoutOptional = scoutRepository.findById(id);
        if (scoutOptional.isPresent()) {
            Scout scout = scoutOptional.get();
            scout.setStatus(ScoutStatus.Active);
            scout.setDateStatusUpdated(LocalDate.now());
            return scoutRepository.save(scout);
        }
        throw new RuntimeException("Scout not found with ID: " + id);
    }
    
    // Get scouts updated after a specific date
    public List<Scout> getScoutsUpdatedAfter(LocalDate date) {
        return scoutRepository.findByDateStatusUpdatedAfter(date);
    }
    
    // Get count by status
    public long getCountByStatus(ScoutStatus status) {
        return scoutRepository.countByStatus(status);
    }
    
    // Get count by troop ID
    public long getCountByTroopId(String troopId) {
        return scoutRepository.countByTroopId(troopId);
    }
    
    // Validate scout data
    public void validateScout(Scout scout) {
        if (scout.getFirstName() == null || scout.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (scout.getLastName() == null || scout.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (scout.getFirstName().length() > 20) {
            throw new IllegalArgumentException("First name must be 20 characters or fewer");
        }
        if (scout.getMiddleName() != null && scout.getMiddleName().length() > 20) {
            throw new IllegalArgumentException("Middle name must be 20 characters or fewer");
        }
        if (scout.getLastName().length() > 20) {
            throw new IllegalArgumentException("Last name must be 20 characters or fewer");
        }
        if (scout.getEmail() != null && !scout.getEmail().trim().isEmpty()) {
            if (!isValidEmail(scout.getEmail())) {
                throw new IllegalArgumentException("Please provide a valid email address");
            }
        }
    }
    
    // Check if scout exists
    public boolean scoutExists(Long id) {
        return scoutRepository.existsById(id);
    }
    
    // Check if email is already in use
    public boolean isEmailInUse(String email) {
        return scoutRepository.findByEmail(email).isPresent();
    }
    
    // Check if phone number is already in use
    public boolean isPhoneNumberInUse(String phoneNumber) {
        return scoutRepository.findByPhoneNumber(phoneNumber).isPresent();
    }
    
    // Simple email validation
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
}