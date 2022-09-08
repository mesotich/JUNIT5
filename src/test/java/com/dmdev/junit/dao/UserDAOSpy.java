package com.dmdev.junit.dao;

import java.util.HashMap;
import java.util.Map;

public class UserDAOSpy extends UserDAO {

    private final UserDAO userDAO;
    private Map<Integer, Boolean> answers = new HashMap<>();

    public UserDAOSpy(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    // public Answer1<Integer,Boolean> answer1;

    @Override
    public boolean delete(Integer userId) {
        return answers.getOrDefault(userId, userDAO.delete(userId));
    }
}
