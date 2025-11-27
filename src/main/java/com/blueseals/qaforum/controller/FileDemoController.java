package com.blueseals.qaforum.controller;

import com.blueseals.qaforum.config.AppConfigLoader;
import com.blueseals.qaforum.model.Role;
import com.blueseals.qaforum.model.User;
import com.blueseals.qaforum.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FileDemoController {

    @Autowired
    private StorageService storageService;

    @GetMapping("/demo/save")
    public String demoSaveToFile() {
        String currentMode = AppConfigLoader.getProperty("app.mode");

        if (currentMode == null) {
            throw new IllegalStateException("Critical Error: 'app.mode' is missing from config.properties!");
        }

        if (!"DEMO".equalsIgnoreCase(currentMode)) {

            throw new UnsupportedOperationException("Action Blocked: File writing is disabled in " + currentMode + " mode.");
        }

        try {
            User fileUser = new User();
            fileUser.setId(999L);
            fileUser.setFullName("File User");
            fileUser.setEmail("test@demo.com");
            fileUser.setRole(Role.STUDENT);
            fileUser.setPassword("password123");

            storageService.saveUser(fileUser);

            return "Success: User saved to users_data.txt! (Mode: " + currentMode + ")";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/demo/load")
    public List<String> demoLoadFromFile() {
        String currentMode = AppConfigLoader.getProperty("app.mode");

        if (currentMode == null || !"DEMO".equalsIgnoreCase(currentMode)) {
            // Standard Language Exception
            throw new UnsupportedOperationException("Action Blocked: File reading is disabled in " + currentMode + " mode.");
        }

        try {
            List<User> users = storageService.loadAllUsers();
            return users.stream()
                    .map(u -> "User ID: " + u.getId() + ", Email: " + u.getEmail())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of("Error: " + e.getMessage());
        }
    }
}