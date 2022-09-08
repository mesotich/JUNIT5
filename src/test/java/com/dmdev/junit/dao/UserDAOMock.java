package com.dmdev.junit.dao;

import org.mockito.stubbing.Answer1;

import java.util.HashMap;
import java.util.Map;

public class UserDAOMock extends UserDAO {

    private Map<Integer, Boolean> answers = new HashMap<>();
    //public Answer1<Integer,Boolean> answer1;

    @Override
    public boolean delete(Integer userId) {
        return answers.getOrDefault(userId, false);
    }
}
