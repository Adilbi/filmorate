package com.filmorate.storage.user;
import com.filmorate.exception.NotFoundException;
import com.filmorate.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage{
    private long currentId = 0;
    private final Map<Long, User> users = new HashMap<>();
    @Override
    public User create(User user) {
        user.setId(++currentId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (users.get(user.getId()) == null) {
            throw new NotFoundException("Пользователь с ID" + user.getId() + "не найден");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long id) {users.remove(id);
    }

    @Override
    public Collection<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
}
