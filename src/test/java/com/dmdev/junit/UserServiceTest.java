package com.dmdev.junit;

import com.dmdev.junit.dao.UserDAO;
import com.dmdev.junit.dto.User;
import com.dmdev.junit.extension.ConditionalExtension;
import com.dmdev.junit.extension.PostProcessingExtension;
import com.dmdev.junit.extension.UserTestParamResolver;
import com.dmdev.junit.service.UserService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.mockito.Mockito.*;

@Tag("fast")
@Tag("user")
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ExtendWith({
        UserTestParamResolver.class,
        PostProcessingExtension.class,
        ConditionalExtension.class,
        MockitoExtension.class
        //ThrowableExtension.class
})
public class UserServiceTest extends TestBase {

    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "111");
    @Captor
    private ArgumentCaptor<Integer> argumentCaptor;
    @Mock(lenient = true)
    private UserDAO userDAO;
    @InjectMocks
    private UserService userService;

    UserServiceTest(TestInfo testInfo) {
        System.out.println();
    }

    @BeforeAll
    void init() {
        System.out.println("Before all: " + this);
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before each: " + this);
        lenient().when(userDAO.delete(IVAN.getId())).thenReturn(true);
        doReturn(true).when(userDAO).delete(IVAN.getId());
    }

    @Test
    void throwExceptionIfDatabaseIsNotAvailable() {
        doThrow(RuntimeException.class).when(userDAO).delete(IVAN.getId());
        assertThrows(RuntimeException.class, () -> userService.delete(IVAN.getId()));
    }

    @Test
    void shouldDeleteExistUser() {
        userService.add(IVAN);
//        Mockito.doReturn(true).when(userDAO).delete(IVAN.getId());
        //Mockito.doReturn(true).when(userDAO.delete(Mockito.any()));
//        Mockito.when(userDAO.delete(IVAN.getId()))
//                .thenReturn(true)
//                .thenReturn(false);
        var deleteResult = userService.delete(IVAN.getId());
        System.out.println(userService.delete(IVAN.getId()));
        System.out.println(userService.delete(IVAN.getId()));
        verify(userDAO, times(3)).delete(argumentCaptor.capture());
//        Mockito.reset(userDAO);
        assertThat(argumentCaptor.getValue()).isEqualTo(1);
        assertTrue(deleteResult);
    }

    @Test
    @DisplayName("users will be empty if no users added")
    void usersEmptyIfNoUserAdded(UserService userService) throws IOException {
        if (true)
            throw new RuntimeException();
        System.out.println("Test 1: " + this);
        var users = userService.getAll();
        MatcherAssert.assertThat(users, IsEmptyCollection.empty());
        assertTrue(users.isEmpty(), () -> "User list should be empty");
        this.userService = userService;
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

    @ParameterizedTest(name = "{arguments} test")
    //@ArgumentsSource()
//    @NullSource
//    @EmptySource
//            @NullAndEmptySource
//    @ValueSource(strings = {"Ivan", "Petr"})
//    @EnumSource
    @MethodSource(value = "com.dmdev.junit.UserServiceTest#getArgumentsForLoginTest")
//    @CsvFileSource(resources = "/login-test-data.csv",delimiter = ',',numLinesToSkip = 1)
//    @CsvSource({
//            "Ivan,123",
//            "Petr,111"
//    })
    @DisplayName("login param test")
    void loginParameterizedTest(String username, String password, Optional<User> user) {
        userService.add(IVAN, PETR);
        var mayBeUser = userService.login(username, password);
        assertThat(mayBeUser).isEqualTo(user);
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
        @Disabled("flaky, need to see")
        void loginFailIfPasswordIsNotCorrect() {
            userService.add(IVAN);
            var mayBeUser = userService.login(IVAN.getUserName(), "12343");
            assertTrue(mayBeUser.isEmpty());
        }

        //@Test
        @RepeatedTest(value = 5, name = RepeatedTest.LONG_DISPLAY_NAME)
        void loginFailIfUserDoesNotExist(RepetitionInfo repetitionInfo) {
            userService.add(IVAN);
            var mayBeUser = userService.login("dummy", IVAN.getPassword());
            assertTrue(mayBeUser.isEmpty());
        }

        @Test
        @Disabled
        @Timeout(value = 200, unit = TimeUnit.MILLISECONDS)
        void checkLoginFunctionalityPerformance() {
            System.out.println(Thread.currentThread().getName());
            var mayBeUser = assertTimeoutPreemptively(Duration.ofMillis(200L), () -> {
                System.out.println(Thread.currentThread().getName());
                Thread.sleep(300L);
                return userService.login("dummy", IVAN.getPassword());
            });
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

    static Stream<Arguments> getArgumentsForLoginTest() {
        return Stream.of(
                Arguments.of("Ivan", "123", Optional.of(IVAN)),
                Arguments.of("Petr", "111", Optional.of(PETR)),
                Arguments.of("dummy", "123", Optional.empty()),
                Arguments.of("Ivan", "dummy", Optional.empty())
        );
    }
}
