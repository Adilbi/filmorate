package com.filmorate.controller;

import com.filmorate.exception.NotFoundException;
import com.filmorate.exception.ValidationException;
import com.filmorate.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private long currentId = 0;
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@RequestBody User user) {
        validate(user);
        user.setId(++currentId);
        users.put(user.getId(), user);
        log.info("Created user with id={}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        validate(user);
        if (users.get(user.getId()) == null) {
            throw new NotFoundException("Пользователь с ID" + user.getId() + "не найден");
        }
        users.put(user.getId(), user);
        log.info("Updated user with id={}", user.getId());
        return user;
    }

    private void validate(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("некорректный email");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Поле логин не может быть пустым и содержать пробел");
        }
        LocalDate today = LocalDate.now();
        if (user.getBirthday() == null || user.getBirthday().isAfter(today)) {
            throw new ValidationException("Поле день рождения обязательно, не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }


    }

}
