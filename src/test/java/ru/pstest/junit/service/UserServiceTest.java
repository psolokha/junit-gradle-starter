package ru.pstest.junit.service;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pstest.junit.TestBase;
import ru.pstest.junit.dao.UserDao;
import ru.pstest.junit.dto.User;
import ru.pstest.junit.extension.*;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @TestInstance(TestInstance.Lifecycle.PER_METHOD) - для каждого метода будет создаваться свой инстанс данного класса
 * В этом случае @BeforeAll и @AfterAll дожны быть статическими
 * @TestInstance(TestInstance.Lifecycle.PER_CLASS) - инстанс создается единожды для класса и запускаются методы
 * <p>
 * Используем Jacoco для тестирования с процентом покрытия: Run 'TestClass' with Coverage
 * <p>
 * Порядок запуска тестов:
 * @TestMethodOrder(MethodOrderer.Random.class) - запуск тестов в случайном порядке
 * @TestMethodOrder(MethodOrderer.OrderAnnotation.class) - помечаем тесты аннотацией @Order(num) чтобы задать порядок
 * @TestMethodOrder(MethodOrderer.MethodName.class) - запускаем тесты в алфавитном порядке по имени метода
 * @TestMethodOrder(MethodOrderer.DisplayName.class) - запускаем тесты в алфавитном порядке из аннотации @DisplayName("text")
 */
@TestMethodOrder(MethodOrderer.DisplayName.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//Аналог @RunWith
@ExtendWith({
        UserServiceParameterResolver.class,
        PostProcessingExtension.class,
        ConditionalExtension.class,
        //Чтобы работали аннотации Mockito
        MockitoExtension.class
//        ThrowableExtension.class
        //Этот класс экстендится от родительского TestBase
//        GlobalExtension.class
})
@Tag("fast")
public class UserServiceTest extends TestBase {

    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "111");
    @InjectMocks
    private UserService userService;
    @Mock
    private UserDao userDao;
    @Captor
    private ArgumentCaptor<Integer> captor;

    UserServiceTest(TestInfo testInfo) {
        System.out.println("testInfo here");
    }

    @BeforeAll
    void init() {
        System.out.println("Before all: " + this);
    }

    @BeforeEach
    void prepare(UserService userService) {
        System.out.println("Before each: " + this);
        //После того как развесили аннотации, нам больше это не нужно
//        this.userDao = Mockito.mock(UserDao.class);
//        this.userDao = Mockito.spy(new UserDao());
//        this.userService = new UserService(userDao);
    }


    @DisplayName("2. Если пользователь не добавлен, то коллекция пустая")
    @Test
    void userListEmptyIfNoUserAdded(UserService userService) throws IOException {
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
        assertThat(users).hasSize(users.size());
    }


    // Использование AssertJ
    @Test
    void usersConvertedToMapByID() {
        userService.add(IVAN, PETR);

        Map<Integer, User> users = userService.getAllConvertedByID();

        assertAll(
                () -> assertThat(users).containsKeys(IVAN.getId(), PETR.getId()),
                () -> assertThat(users).containsValues(IVAN, PETR)
        );
    }

    //Использование Hamcrest
    @Test
    void usersConvertedToMapByIDByHamcrest() {
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

    /**
     * Использование Mockito
     */
    @Test
    void shouldDeleteExistedUser() {
        userService.add(IVAN);
        //Для spy()
        Mockito.doReturn(true).when(userDao).delete(IVAN.getId());
        //Для mock()
//        Mockito.when(userDao.delete(IVAN.getId()))
//                .thenReturn(true)
//                .thenReturn(false);
        boolean deleteResult = userService.delete(IVAN.getId());
        System.out.println(userService.delete(IVAN.getId()));
        System.out.println(userService.delete(IVAN.getId()));

        //Проверить с какими аргументами вызывался метод
//        ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);

        //Убедимся, что метод вызывался три раза
        Mockito.verify(userDao, Mockito.times(3)).delete(captor.capture());
        assertThat(captor.getValue()).isEqualTo(IVAN.getId());
        assertThat(deleteResult).isTrue();
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
            maybeUser.ifPresent(user -> assertThat(user).isEqualTo(IVAN));
        }

        @Test
        void loginFailedIfPasswordIsNotCorrect() {
            userService.add(IVAN);

            var maybeUser = userService.login(IVAN.getUserName(), "dummy");

//        assertTrue(maybeUser.isEmpty());
            assertThat(maybeUser).isEmpty();
        }

        @Test
        @Timeout(200)
        /**
         * Используется что-то одно:
         * или аннотация, или assertTimeout
         */
        void checkLoginFunctionalityPerformance() {
            var result = assertTimeout(Duration.ofMillis(200L),
                    () -> {
                        Thread.sleep(100);
                        return userService.login("dummy", IVAN.getPassword());
                    });
        }

        @RepeatedTest(value = 7, name = RepeatedTest.LONG_DISPLAY_NAME)
        /**
         * Удобный способ запускать тест несколько раз и видеть в дебаггере
         * на какой итерации происходит падение
         */
        void loginFailedIfUserNotExists(RepetitionInfo repetitionInfo) {
            userService.add(IVAN);

            var maybeUser = userService.login("dummy", IVAN.getPassword());

//        assertTrue(maybeUser.isEmpty());
            assertThat(maybeUser).isEmpty();
        }

        @ParameterizedTest
        /**
         * Аннотации используют один аргумент
         *
         * @NullSource
         * @EmptySource
         *
         * @NullAndEmptySource - для коллекций
         * @ValueSource - для примитивов - @ValueSource(strings = {"Ivan, Petr"})
         * @EnumSource - для енамов
         */

        @MethodSource("ru.pstest.junit.service.UserServiceTest#getArgumentsForLoginTest")
        void loginParametrizedTest(String userName, String password, Optional<User> user) {
            userService.add(IVAN, PETR);
            var maybeUser = userService.login(userName, password);

            assertThat(maybeUser).isEqualTo(user);
        }

        @ParameterizedTest(name = "{arguments} test done!!!")
        @CsvFileSource(resources = "/login-test-data.csv", delimiter = ',', numLinesToSkip = 1)
        void loginParametrizedTestWithCSVFile(String userName, String password) {
            userService.add(IVAN, PETR);
            System.out.println(userName + " - " + password);
            User maybeUser = userService.login(userName, password).get();

            assertThat(maybeUser).isIn(userService.getAll());
        }

        @ParameterizedTest
        @DisplayName("Login test with CSV Source ")
        @CsvSource(value = {
                "Ivan,123",
                "Petr,111"
        }, delimiter = ',')
        void loginParametrizedTestWithCSVSource(String userName, String password) {
            userService.add(IVAN, PETR);
            System.out.println(userName + " - " + password);
            User maybeUser = userService.login(userName, password).get();

            assertThat(maybeUser).isIn(userService.getAll());
        }

    }

    static Stream<Arguments> getArgumentsForLoginTest() {
        return Stream.of(
                Arguments.of("Ivan", "123", Optional.of(IVAN)),
                Arguments.of("Petr", "111", Optional.of(PETR)),
                Arguments.of("Ivan", "dummy", Optional.empty()),
                Arguments.of("dummy", "123", Optional.empty())
        );
    }
}
