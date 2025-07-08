package com.treesales.repository;

import com.treesales.entity.Scout;
import com.treesales.entity.Scout.ScoutStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScoutRepository extends JpaRepository<Scout, Long> {
    
    // Find scouts by status
    List<Scout> findByStatus(ScoutStatus status);
    
    // Find scouts by troop ID
    List<Scout> findByTroopId(String troopId);
    
    // Find scouts by troop ID and status
    List<Scout> findByTroopIdAndStatus(String troopId, ScoutStatus status);
    
    // Find scouts by email
    Optional<Scout> findByEmail(String email);
    
    // Find scouts by last name
    List<Scout> findByLastNameOrderByFirstName(String lastName);
    
    // Find scouts by first name and last name
    List<Scout> findByFirstNameAndLastName(String firstName, String lastName);
    
    // Find scouts by phone number
    Optional<Scout> findByPhoneNumber(String phoneNumber);
    
    // Find scouts by date of birth range
    List<Scout> findByDateOfBirthBetween(LocalDate startDate, LocalDate endDate);
    
    // Find scouts updated after a certain date
    List<Scout> findByDateStatusUpdatedAfter(LocalDate date);
    
    // Search scouts by name (case-insensitive)
    @Query("SELECT s FROM Scout s WHERE " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(s.middleName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Scout> findByNameContaining(@Param("name") String name);
    
    // Get scouts by full name search
    @Query("SELECT s FROM Scout s WHERE " +
           "LOWER(CONCAT(s.firstName, ' ', COALESCE(s.middleName, ''), ' ', s.lastName)) " +
           "LIKE LOWER(CONCAT('%', :fullName, '%'))")
    List<Scout> findByFullNameContaining(@Param("fullName") String fullName);
    
    // Count scouts by status
    long countByStatus(ScoutStatus status);
    
    // Count scouts by troop ID
    long countByTroopId(String troopId);
    
    // Find active scouts ordered by last name
    List<Scout> findByStatusOrderByLastNameAscFirstNameAsc(ScoutStatus status);
    
    // Find scouts by multiple troop IDs
    List<Scout> findByTroopIdIn(List<String> troopIds);
}