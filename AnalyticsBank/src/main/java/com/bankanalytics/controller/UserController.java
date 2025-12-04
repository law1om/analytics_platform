
package com.bankanalytics.controller;

import com.bankanalytics.entity.User;
import com.bankanalytics.entity.Division;
import com.bankanalytics.dto.UserDto;
import com.bankanalytics.dto.CreateUserDto;
import com.bankanalytics.service.UserService;
import com.bankanalytics.repository.DivisionRepository;
import com.bankanalytics.mapper.DtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final DivisionRepository divisionRepository;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("GET /users - Fetching all users");
        List<User> users = userService.getAllUsers();
        List<UserDto> result = users.stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        log.info("GET /users/{} - Fetching user by id", id);
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(dtoMapper.toDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        log.info("GET /users/email/{} - Fetching user by email", email);
        return userService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(dtoMapper.toDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDto>> getUsersByRole(@PathVariable User.UserRole role) {
        log.info("GET /users/role/{} - Fetching users by role", role);
        List<User> users = userService.getUsersByRole(role);
        List<UserDto> result = users.stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/division/{divisionId}")
    public ResponseEntity<List<UserDto>> getUsersByDivision(@PathVariable Long divisionId) {
        log.info("GET /users/division/{} - Fetching users by division", divisionId);
        List<User> users = userService.getUsersByDivision(divisionId);
        List<UserDto> result = users.stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/division/{divisionId}/role/{role}")
    public ResponseEntity<List<UserDto>> getUsersByDivisionAndRole(@PathVariable Long divisionId,
                                                                @PathVariable User.UserRole role) {
        log.info("GET /users/division/{}/role/{} - Fetching users by division and role", divisionId, role);
        List<User> users = userService.getUsersByDivisionAndRole(divisionId, role);
        List<UserDto> result = users.stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsersByName(@RequestParam String name) {
        log.info("GET /users/search?name={} - Searching users by name", name);
        List<User> users = userService.searchUsersByName(name);
        List<UserDto> result = users.stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/division/{divisionId}/count")
    public ResponseEntity<Long> getUserCountByDivision(@PathVariable Long divisionId) {
        log.info("GET /users/division/{}/count - Getting user count for division", divisionId);
        long count = userService.getUserCountByDivision(divisionId);
        return ResponseEntity.ok(count);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody User user) {
        log.info("POST /users - Creating new user: {}", user.getEmail());
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toDto(createdUser));
        } catch (IllegalArgumentException e) {
            log.error("Error creating user: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id,
                                           @Valid @RequestBody User userDetails) {
        log.info("PUT /users/{} - Updating user", id);
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(dtoMapper.toDto(updatedUser));
        } catch (RuntimeException e) {
            log.error("Error updating user: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /users/{} - Deleting user", id);
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting user: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

}
