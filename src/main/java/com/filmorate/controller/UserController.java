package com.filmorate.controller;
import com.filmorate.exception.ValidationException;
import com.filmorate.model.User;
import com.filmorate.storage.film.FilmStorage;
import com.filmorate.storage.user.UserStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage  userStorage;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);;
@Autowired
    public UserController(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @GetMapping
    public Collection<User> findAll() {
        return new ArrayList<>(userStorage.findAll());
    }

    @PostMapping
    public User create(@RequestBody User user) {
        validate(user);
        userStorage.create(user);
        log.info("Created user with id={}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        validate(user);
        userStorage.update(user);
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
