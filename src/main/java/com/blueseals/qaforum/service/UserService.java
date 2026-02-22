package com.blueseals.qaforum.service;

import com.blueseals.qaforum.model.User;
import com.blueseals.qaforum.model.Role;
import com.blueseals.qaforum.repository.UserRepository;
import com.blueseals.qaforum.dto.UserRegistrationRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerStudent(String fullName, String email, String rawPassword) {
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(Role.STUDENT);

        userRepository.save(user);
    }
    
    public void registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already properly registered.");
        }
        
        String email = request.getEmail();
        String username = email.split("@")[0];
        username = username.replaceAll("\\d+$", ""); // remove trailing digits
        
        String[] parts = username.split("\\.");
        String firstName = StringUtils.capitalize(parts[0]);
        String lastName = parts.length > 1 ? StringUtils.capitalize(parts[1]) : "";
        String fullName = firstName + (lastName.isEmpty() ? "" : " " + lastName);

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.STUDENT);

        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public boolean verifyPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void updateUserRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        user.setRole(role);
        userRepository.save(user);
    }
}
