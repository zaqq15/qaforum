package com.blueseals.qaforum.service;

import com.blueseals.qaforum.model.User;
import java.util.List;

public interface StorageService {
    void saveUser(User user) throws Exception;
    List<User> loadAllUsers() throws Exception;
}
