package com.blueseals.qaforum.config;

import com.blueseals.qaforum.model.Role;
import com.blueseals.qaforum.model.User;
import com.blueseals.qaforum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Value("${app.admin.email:admin@e-uvt.ro}")
    private String adminEmail;
    
    @Value("${app.admin.password:SecureAdmin123!}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        // create an initial admin account if it doesn't exist
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setFullName("System Admin");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            
            userRepository.save(admin);
            System.out.println("Default Admin account preloaded with email: " + adminEmail);
        }
    }
}
