package com.dmdev.junit;

import com.dmdev.junit.dto.User;
import com.dmdev.junit.service.UserService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle;

@Tag("fast")
@Tag("user")
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
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
    @DisplayName("users will be empty if no users added")
    void usersEmptyIfNoUserAdded() {
        System.out.println("Test 1: " + this);
        var users = userService.getAll();
        MatcherAssert.assertThat(users, IsEmptyCollection.empty());
        assertTrue(users.isEmpty(), () -> "User list should be empty");
    }

    @Test
    void usersSizeIfUserAdded() {
        System.out.println("Test 2: " + this);
        userService.add(IVAN);
        userService.add(PETR);
        var users = userService.getAll();
        assertThat(users).hasSize(2);
        // assertEquals(2, users.size());
    }

    @Test
    void usersConvertedToMapById() {
        userService.add(IVAN, PETR);
        var users = userService.getAllConvertedById();
        assertAll(() -> assertThat(users).containsKeys(IVAN.getId(), PETR.getId()),
                () -> assertThat(users).containsValues(IVAN, PETR)
        );
        MatcherAssert.assertThat(users, IsMapContaining.hasKey(IVAN.getId()));
//        assertThat(users).containsKeys(IVAN.getId(), PETR.getId());
//        assertThat(users).containsValues(IVAN, PETR);
    }

    @AfterEach
    void deleteDataFromDataBase() {
        System.out.println("AfterEach: " + this);
    }

    @AfterAll
    void closeConnectionPool() {
        System.out.println("After all: " + this);
    }

    @Tag("login")
    @Nested
    @DisplayName("test user login functionality")
    class LoginTest {

        @Test
        void loginSuccessIfUserExists() {
            userService.add(IVAN);
            Optional<User> mayBeUser = userService.login(IVAN.getUserName(), IVAN.getPassword());
            assertThat(mayBeUser).isPresent();
            mayBeUser.ifPresent(user -> assertThat(user).isEqualTo(IVAN));
            //mayBeUser.ifPresent(user -> assertEquals(IVAN, user));
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

        @Test
//    @org.junit.Test(expected = IllegalArgumentException.class)
        void throwExceptionIfUsernameOrPasswordIsNull() {
            Assertions.assertAll(
                    () -> {
                        var exception = assertThrows(IllegalArgumentException.class,
                                () -> userService.login(null, "dummy"));
                        assertThat(exception.getMessage()).isEqualTo("username or password is null");
                    },
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> userService.login("dummy", null))
            );
//        try {
//            userService.login(null,"dummy");
//            fail("login should throw exception on null username");
//        }
//        catch (IllegalArgumentException e){
//            assertTrue(true);
//        }
        }
    }
}
