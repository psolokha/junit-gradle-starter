package ru.pstest.junit.dto;

import lombok.Value;

@Value(staticConstructor = "of")
public class User {

    Integer id;
    String userName;
    String password;
}
