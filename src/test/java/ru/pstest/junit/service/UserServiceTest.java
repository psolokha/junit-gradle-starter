package ru.pstest.junit.service;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.*;
import ru.pstest.junit.dto.User;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.*;

/**@TestInstance(TestInstance.Lifecycle.PER_METHOD) - для каждого метода будет осздаваться свой инстанс данного класса
 * В этом случае @BeforeAll и @AfterAll дожны быть статическими
 * @TestInstance(TestInstance.Lifecycle.PER_CLASS) - инстанс создается единожды для класса и запускаются методы
 *
 * Используем Jacoco для тестирования с процентом покрытия: Run 'TestClass' with Coverage
 *
 * Порядок запуска тестов:
 * @TestMethodOrder(MethodOrderer.Random.class) - запуск тестов в случайном порядке
 * @TestMethodOrder(MethodOrderer.OrderAnnotation.class) - помечаем тесты аннотацией @Order(num) чтобы задать порядок
 * @TestMethodOrder(MethodOrderer.MethodName.class) - запускаем тесты в алфавитном порядке по имени метода
 * @TestMethodOrder(MethodOrderer.DisplayName.class) - запускаем тесты в алфавитном порядке из аннотации @DisplayName("text")
 *
 */
@TestMethodOrder(MethodOrderer.DisplayName.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("fast")
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



    @DisplayName("2. Если пользователь не добавлен, то коллекция пустая")
    @Test
    void userListEmptyIfNoUserAdded() {
        System.out.println("Test1: " + this);
        var userList = userService.getAll();
//        assertTrue(userList.isEmpty());
        assertThat(userList).isEmpty();

//        assertAll(); - объединить сразу несколько ассертов, которые будут проверены, чтобы тест не падал при первом же фейле.
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

    @Nested
    @Tag("login")
    @DisplayName("Проверка функциональности логина")
    class LoginTest {
        //Тестируем с использованием эксепшенов
        @Test
        void throwExceptionIfUsernameOrPasswordIsNull() {
            // method implementation
            try {
                userService.login(null, "dummy");
                fail("Login should throw exception");
            } catch (IllegalArgumentException ex) {
                assertTrue(true);
            }

            //junit5 version:
            assertAll(
                    () -> {
                        var exception = assertThrows(IllegalArgumentException.class, () -> userService.login(null, "dummy"));
                        //Проверяем сообщение эксепшена
                        assertThat(exception.getMessage()).isEqualTo("username or password is null");
                    },
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login("dummy", null))
            );

        }

        @Test
        @DisplayName("1. Проверяем успешный логин, если юзер существует")
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
    }
}
