package com.dmdev.junit;

import com.dmdev.junit.dto.User;
import com.dmdev.junit.service.UserService;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class UserServiceTest {

    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "111");
    private UserService userService;

    @BeforeAll
    void init() {
        System.out.println("Before all: " + this);
    }

    @BeforeEach
    void prepare() {
        userService = new UserService();
        System.out.println("Before each: " + this);
    }

    @Test
    void usersEmptyIfNoUserAdded() {
        System.out.println("Test 1: " + this);
        var users = userService.getAll();
        assertTrue(users.isEmpty(), () -> "User list should be empty");
    }

    @Test
    void usersSizeIfUserAdded() {
        System.out.println("Test 2: " + this);
        userService.add(IVAN);
        userService.add(PETR);
        var users = userService.getAll();
        assertEquals(2, users.size());
    }

    @Test
    void loginSuccessIfUserExists() {
        userService.add(IVAN);
        Optional<User> mayBeUser = userService.login(IVAN.getUserName(), IVAN.getPassword());
        assertTrue(mayBeUser.isPresent());
        mayBeUser.ifPresent(user -> assertEquals(IVAN, user));
    }

    @Test
    void loginFailIfPasswordIsNotCorrect() {
        userService.add(IVAN);
        var mayBeUser = userService.login(IVAN.getUserName(), "12343");
        assertTrue(mayBeUser.isEmpty());
    }
    @Test
    void loginFailIfUserDoesNotExist() {
        userService.add(IVAN);
        var mayBeUser = userService.login("dummy", IVAN.getPassword());
        assertTrue(mayBeUser.isEmpty());
    }

    @AfterEach
    void deleteDataFromDataBase() {
        System.out.println("AfterEach: " + this);
    }

    @AfterAll
    void closeConnectionPool() {
        System.out.println("After all: " + this);
    }
}
