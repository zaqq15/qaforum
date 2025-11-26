package com.blueseals.qaforum.controller;

import com.blueseals.qaforum.model.User;
import com.blueseals.qaforum.model.Role;
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
        try {
            User fileUser = new User();
            fileUser.setId(999L);
            fileUser.setFullName("File User");
            fileUser.setEmail("test@demo.com");
            fileUser.setRole(Role.STUDENT);
            fileUser.setPassword("password123");

            storageService.saveUser(fileUser);

            return "Success: User saved to users_data.txt!";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/demo/load")
    public List<String> demoLoadFromFile() {
        try {
            List<User> users = storageService.loadAllUsers();


            return users.stream()
                    .map(u -> "User ID: " + u.getId() + ", Email: " + u.getEmail())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return List.of("Error: " + e.getMessage());
        }
    }
}