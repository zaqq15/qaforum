package com.blueseals.qaforum.config;

import com.blueseals.qaforum.model.Role;
import com.blueseals.qaforum.model.User;
import com.blueseals.qaforum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {

            // check if data already exists

            System.out.println("--- Seeding Database with Users ---");

            // create admin
            if(userRepository.findByEmail("admin@email.com").isEmpty()) {
                User admin = new User();
                admin.setFullName("System Admin");
                admin.setEmail("admin@email.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
            }
            // create professor
            if(userRepository.findByEmail("prof@email.com").isEmpty()) {
                User prof = new User();
                prof.setFullName("Professor X");
                prof.setEmail("prof@email.com");
                prof.setPassword(passwordEncoder.encode("prof123"));
                prof.setRole(Role.PROFESSOR);
                userRepository.save(prof);
            }

            // create student
            if(userRepository.findByEmail("student@email.com").isEmpty()) {
                User student = new User();
                student.setFullName("Jean Grey");
                student.setEmail("student@email.com");
                student.setPassword(passwordEncoder.encode("student123"));
                student.setRole(Role.STUDENT);
                userRepository.save(student);
            }

            System.out.println("--- Database seeded successfully ---");
            System.out.println("Admin Login: admin@email.com / admin123");

        };
    }


}
