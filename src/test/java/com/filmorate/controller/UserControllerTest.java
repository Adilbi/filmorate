package com.filmorate.controller;

import com.filmorate.exception.NotFoundException;
import com.filmorate.exception.ValidationException;
import com.filmorate.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    private UserController controller = new UserController();

    private User validUser() {
        User user = new User();
        user.setEmail("test@des.kz");
        user.setLogin("Admin");
        user.setName("Adilbi");
        user.setBirthday(LocalDate.of(2002, 11, 17));
        return user;
    }

    @Test
    void shouldCreateUser() {
        User user = validUser();
        User created = controller.create(user);
        assertEquals(1, created.getId());
    }

    @Test
    void shouldThrowWhenEmailIsNull() {
        User user = validUser();
        user.setEmail(null);
        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldThrowWhenEmailWithoutAt() {
        User user = validUser();
        user.setEmail("test.des.kz");
        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldThrowWhenLoginIsBlank() {
        User user = validUser();
        user.setLogin("");
        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldThrowWhenLoginContainsSpaces() {
        User user = validUser();
        user.setLogin("ben 10");
        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldUseLoginWhenNameIsBlank() {
        User user = validUser();
        user.setName("");
        User created = controller.create(user);
        assertEquals(user.getLogin(), created.getName());
    }

    @Test
    void shouldCreateWhenBirthdayIsToday() {
        User user = validUser();
        user.setBirthday(LocalDate.now());
        assertDoesNotThrow(() -> controller.create(user));
    }

    @Test
    void shouldThrowWhenBirthdayInFuture() {
        User user = validUser();
        user.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldThrowWhenUpdateUnknownUser() {
        User user = validUser();
        user.setId(999L);
        assertThrows(NotFoundException.class, () -> controller.update(user));
    }


}
