package com.blueseals.qaforum.service;

import com.blueseals.qaforum.model.User;
import com.blueseals.qaforum.model.Role;
import com.blueseals.qaforum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}
