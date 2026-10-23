package com.filmorate.storage.user;

import com.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;


public interface UserStorage {
    User create(User user);

    User update(User user);

    void delete(Long id);

    Collection<User> findAll();

    Optional<User> findById(Long id);
}
