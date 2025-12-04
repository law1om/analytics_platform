package com.bankanalytics.config;

import com.bankanalytics.entity.Division;
import com.bankanalytics.entity.User;
import com.bankanalytics.repository.DivisionRepository;
import com.bankanalytics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DivisionRepository divisionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing database with sample data...");
        
        if (divisionRepository.count() == 0) {
            log.info("No divisions found, creating sample divisions...");
            createSampleDivisions();
        } else {
            log.info("Divisions already exist, skipping division creation. Count: {}", divisionRepository.count());
        }
        
        if (userRepository.count() == 0) {
            log.info("No users found, creating default users...");
            createAdminUser();
            createEmployee();
        } else {
            log.info("Users already exist, skipping user creation. Count: {}", userRepository.count());
        }
        
        log.info("Database initialization completed.");
    }
    
    private void createSampleDivisions() {
        log.info("Creating sample divisions...");
        
        Division headOffice = new Division();
        headOffice.setName("Головной офис");
        headOffice.setBlocks(Arrays.asList(
            "Департамент стратегического планирования",
            "Департамент финансового контроля",
            "Административный отдел"
        ));
        divisionRepository.save(headOffice);
        
        log.info("Sample divisions created successfully.");
    }
    
    private void createAdminUser() {
        log.info("Creating admin user...");
        
        Division headOffice = divisionRepository.findByNameContaining("Головной").stream()
                .findFirst()
                .orElse(null);
        
        User admin = new User();
        admin.setName("Администратор Системы");
        admin.setEmail("admin@bank.com");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setRole(User.UserRole.ADMIN);
        admin.setDivision(headOffice);
        admin.setBlock("Административный отдел");
        
        userRepository.save(admin);
        log.info("Admin user created: admin@bank.com / admin123");
    }
    private void createEmployee() {
        log.info("Creating employee user...");

        Division headOffice = divisionRepository.findByNameContaining("Головной").stream()
                .findFirst()
                .orElse(null);

        User employee = new User();
        employee.setName("Ramil");
        employee.setEmail("ramil@bank.com");
        employee.setPasswordHash(passwordEncoder.encode("123123"));
        employee.setRole(User.UserRole.EMPLOYEE);
        employee.setDivision(headOffice);
        employee.setBlock("Департамент стратегического планирования");

        userRepository.save(employee);
        log.info("Employee user created: ramil@bank.com / 123123");
    }
}
