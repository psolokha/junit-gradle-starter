package ru.pstest.junit.service;

import lombok.Getter;
import ru.pstest.junit.dto.User;

import java.util.*;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class UserService {

    private final List<User> users = new ArrayList<>();

    public List<User> getAll() {
        return users;
    }

    public boolean add(User... users) {
        return this.users.addAll(Arrays.asList(users));
    }

    public Optional<User> login(String userName, String password) {
        if (userName == null || password == null) {
            throw new IllegalArgumentException("username or password is null");
        }

        return users.stream()
                .filter(user -> user.getUserName().equals(userName))
                .filter(user -> user.getPassword().equals(password))
                .findFirst();
    }

    public Map<Integer, User> getAllConvertedByID() {
        return users.stream()
                .collect(toMap(User::getId, identity()));
    }
}
