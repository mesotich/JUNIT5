package com.dmdev.junit.service;

import com.dmdev.junit.dao.UserDAO;
import com.dmdev.junit.dto.User;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserService {

    private final List<User> users = new ArrayList<>();
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    public boolean delete(Integer userId){
        Integer userIdLocal = 25;
        return userDAO.delete(userId);
    }

    public List<User> getAll() {
        return users;
    }

    public void add(User... users) {
        this.users.addAll(Arrays.asList(users));
    }

    public Optional<User> login(String userName, String password) {
        if (userName == null || password == null)
            throw new IllegalArgumentException("username or password is null");
        return users.stream()
                .filter(user -> user.getUserName().equals(userName))
                .filter(user -> user.getPassword().equals(password))
                .findFirst();
    }

    public Map<Integer, User> getAllConvertedById() {
        return users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
