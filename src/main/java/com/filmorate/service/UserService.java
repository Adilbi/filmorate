package com.filmorate.service;

import com.filmorate.exception.NotFoundException;
import com.filmorate.exception.ValidationException;
import com.filmorate.model.User;
import com.filmorate.storage.user.UserStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private User getUserOrThrow(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(Long userId) {
        User user = getUserOrThrow(userId);
        List<User> result = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            result.add(getUserOrThrow(friendId));
        }
        return result;
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        User user = getUserOrThrow(userId);
        User other = getUserOrThrow(otherId);
        Set<Long> commonFriends = new HashSet<>(user.getFriends());
        commonFriends.retainAll(other.getFriends());
        List<User> result = new ArrayList<>();
        for (Long id : commonFriends) {
            result.add(getUserOrThrow(id));
        }
        return result;
    }

    private void validate(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный email");
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

    public User create(User user) {
        validate(user);
        User createdUser = userStorage.create(user);
        log.info("Created user with id={}", createdUser.getId());
        return createdUser;
    }

    public User update(User user) {
        validate(user);
        User existing = getUserOrThrow(user.getId());
        user.setFriends(existing.getFriends());
        User updatedUser = userStorage.update(user);
        log.info("Updated user with id={}", updatedUser.getId());
        return updatedUser;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Long id) {
        return getUserOrThrow(id);

    }
}
