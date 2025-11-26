package com.blueseals.qaforum.service;

import com.blueseals.qaforum.exception.DataStorageException;
import com.blueseals.qaforum.exception.ValidationException;
import com.blueseals.qaforum.model.Role;
import com.blueseals.qaforum.model.User;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
public class FileStorageService implements StorageService {

    private static final String FILE_PATH = "users_data.txt";

    @Override
    public void saveUser(User user) throws DataStorageException {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Invalid email format");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new ValidationException("Password too short");
        }

        try (FileWriter fw = new FileWriter(FILE_PATH, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            String line = user.getId() + "," + user.getEmail() + "," + user.getFullName() + "," + user.getRole();
            out.println(line);

        } catch (IOException e) {
            throw new DataStorageException("Could not write to storage file", e);
        }
    }

    @Override
    public List<User> loadAllUsers() throws DataStorageException {
        List<User> users = new ArrayList<>();
        File file = new File(FILE_PATH);

        if(!file.exists()) {
            return users;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");


                if (parts.length == 4) {
                    User u = new User();
                    u.setId(Long.parseLong(parts[0].trim()));
                    u.setEmail(parts[1].trim());
                    u.setFullName(parts[2].trim());
                    u.setRole(Role.valueOf(parts[3].trim()));
                    users.add(u);
                }
            }
        } catch (FileNotFoundException e) {
            throw new DataStorageException("Could not read from storage file", e);
        } catch (NumberFormatException e) {
            throw new DataStorageException("Corrupt data in storage file", e);
        }
        return users;
    }
}