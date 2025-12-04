package com.bankanalytics.service;

import com.bankanalytics.entity.User;
import com.bankanalytics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public List<User> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(Long id) {
        log.debug("Fetching user by id: {}", id);
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);
        return userRepository.findByEmail(email);
    }
    
    public List<User> getUsersByRole(User.UserRole role) {
        log.debug("Fetching users by role: {}", role);
        return userRepository.findByRole(role);
    }
    
    public List<User> getUsersByDivision(Long divisionId) {
        log.debug("Fetching users by division: {}", divisionId);
        return userRepository.findByDivisionId(divisionId);
    }
    
    public User createUser(User user) {
        log.info("Creating new user: {}", user.getEmail());
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }
        

        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        
        return userRepository.save(user);
    }
    
    public User updateUser(Long id, User userDetails) {
        log.info("Updating user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        user.setDivision(userDetails.getDivision());
        

        if (userDetails.getPasswordHash() != null && !userDetails.getPasswordHash().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(userDetails.getPasswordHash()));
        }
        
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        
        userRepository.deleteById(id);
    }
    
    public List<User> searchUsersByName(String name) {
        log.debug("Searching users by name: {}", name);
        return userRepository.findByNameContaining(name);
    }
    
    public long getUserCountByDivision(Long divisionId) {
        log.debug("Getting user count for division: {}", divisionId);
        return userRepository.countByDivisionId(divisionId);
    }
    
    public List<User> getUsersByDivisionAndRole(Long divisionId, User.UserRole role) {
        log.debug("Fetching users by division {} and role {}", divisionId, role);
        return userRepository.findByDivisionIdAndRole(divisionId, role);
    }
}
