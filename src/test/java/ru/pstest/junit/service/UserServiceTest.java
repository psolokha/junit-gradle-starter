package ru.pstest.junit.service;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.*;
import ru.pstest.junit.dto.User;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertAll;

/**@TestInstance(TestInstance.Lifecycle.PER_METHOD) - для каждого мктода будет осздаваться свой инстанс данного класса
 * В этом случае @BeforeAll и @AfterAll дожны быть статическими
 * @TestInstance(TestInstance.Lifecycle.PER_CLASS) - инстанс создается единожды для класса и запускаются методы
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "111");
    private UserService userService;

    @BeforeAll
    void init() {
        System.out.println("Before all: " + this);
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before each: " + this);
        userService = new UserService();
    }

    @Test
    void userListEmptyIfNoUserAdded() {
        System.out.println("Test1: " + this);
        var userList = userService.getAll();
//        assertTrue(userList.isEmpty());
        assertThat(userList).isEmpty();

//        assertAll(); - объединить сразу несколько ассертов, которые будут проверены, чтобы тест не падал при первом же фейле.
    }

    @Test
    void loginSuccessIfUserExists() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login(IVAN.getUserName(), IVAN.getPassword());

//        assertTrue(maybeUser.isPresent());
//        maybeUser.ifPresent( user -> assertEquals(IVAN, user));
        assertThat(maybeUser).isPresent();
        maybeUser.ifPresent( user -> assertThat(user).isEqualTo(IVAN));
    }

    @Test
    void loginFailedIfPasswordIsNotCorrect() {
        userService.add(IVAN);

        var maybeUser = userService.login(IVAN.getUserName(), "dummy");

//        assertTrue(maybeUser.isEmpty());
        assertThat(maybeUser).isEmpty();
    }

    @Test
    void loginFailedIfUserNotExists() {
        userService.add(IVAN);

        var maybeUser = userService.login("dummy", IVAN.getPassword());

//        assertTrue(maybeUser.isEmpty());
        assertThat(maybeUser).isEmpty();
    }

    @Test
    void usersSizeIfUserAdded() {
        System.out.println("Test2: " + this);
        userService.add(IVAN);
        userService.add(PETR);
        var users = userService.getAll();

//        assertEquals(2, users.size());
        assertThat(users).hasSize(2);
    }

    // Использование AssertJ
    @Test
    void usersConvertedToMapByID(){
        userService.add(IVAN, PETR);

        Map<Integer, User> users = userService.getAllConvertedByID();

        assertAll(
                () -> assertThat(users).containsKeys(IVAN.getId(), PETR.getId()),
                () -> assertThat(users).containsValues(IVAN, PETR)
        );
    }

    //Использование Hamcrest
    @Test
    void usersConvertedToMapByIDByHamcrest(){
        userService.add(IVAN, PETR);

        Map<Integer, User> users = userService.getAllConvertedByID();

        //Использование HamcrestMatcher
        MatcherAssert.assertThat(users, hasKey(IVAN.getId()));
//        MatcherAssert.assertThat(users, empty());
    }

    @AfterEach
    void deleteDataFromDB() {
        System.out.println("After each: " + this);
    }

    @AfterAll
    void closeAll() {
        System.out.println("After all: " + this);
    }
}
