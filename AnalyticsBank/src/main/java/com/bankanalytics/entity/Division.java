package com.bankanalytics.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "divisions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Division {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @ElementCollection
    @CollectionTable(name = "division_blocks", joinColumns = @JoinColumn(name = "division_id"))
    @Column(name = "block_name")
    private List<String> blocks;
    
    @OneToMany(mappedBy = "division", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<User> users;
    
    @OneToMany(mappedBy = "division", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Goal> goals;
    
    @OneToMany(mappedBy = "division", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Report> reports;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
