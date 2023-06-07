package com.skyscanner.service;

import com.skyscanner.entity.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    List<User> getAllUsers();
}
