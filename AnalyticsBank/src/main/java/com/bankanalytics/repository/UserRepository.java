package com.bankanalytics.repository;

import com.bankanalytics.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    List<User> findByRole(User.UserRole role);
    
    List<User> findByDivisionId(Long divisionId);
    
    @Query("SELECT u FROM User u WHERE u.name LIKE %:name%")
    List<User> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.division.id = :divisionId")
    long countByDivisionId(@Param("divisionId") Long divisionId);
    
    @Query("SELECT u FROM User u WHERE u.division.id = :divisionId AND u.role = :role")
    List<User> findByDivisionIdAndRole(@Param("divisionId") Long divisionId, 
                                      @Param("role") User.UserRole role);
    
    boolean existsByEmail(String email);
}
