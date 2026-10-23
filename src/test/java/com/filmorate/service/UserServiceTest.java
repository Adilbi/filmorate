package com.filmorate.service;

import com.filmorate.exception.NotFoundException;
import com.filmorate.model.User;
import com.filmorate.storage.user.InMemoryUserStorage;
import com.filmorate.storage.user.UserStorage;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {
    private UserStorage userStorage = new InMemoryUserStorage();
    private UserService userService = new UserService(userStorage);

    private User createUser(String login) {
        User user = new User();
        user.setLogin(login);
        user.setEmail(login + "@gmail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return userService.create(user);

    }


    @Test
    void shouldAddFriendToBoth() {
        User adilbi = createUser("adilbi");
        User kamila = createUser("kamila");

        userService.addFriend(adilbi.getId(), kamila.getId());

        assertTrue(userService.findById(adilbi.getId()).getFriends().contains(kamila.getId()));
        assertTrue(userService.findById(kamila.getId()).getFriends().contains(adilbi.getId()));
    }

    @Test
    void shouldRemoveFriendFromBoth() {
        User adilbi = createUser("adilbi");
        User kamila = createUser("kamila");

        userService.addFriend(adilbi.getId(), kamila.getId());

        userService.removeFriend(adilbi.getId(), kamila.getId());
        assertTrue(userService.findById(adilbi.getId()).getFriends().isEmpty());
        assertTrue(userService.findById(kamila.getId()).getFriends().isEmpty());
    }

    @Test
    void shouldReturnFriendsList() {
        User adilbi = createUser("adilbi");
        User kamila = createUser("kamila");
        User dilnaz = createUser("dilnaz");

        userService.addFriend(adilbi.getId(), kamila.getId());
        userService.addFriend(adilbi.getId(), dilnaz.getId());

        List<User> friends = userService.getFriends(adilbi.getId());
        assertEquals(2, friends.size(), "У Адилби должно быть ровно 2 друга");

    }

    @Test
    void shouldReturnCommonFriends() {
        User adilbi = createUser("adilbi");
        User kamila = createUser("kamila");
        User dilnaz = createUser("dilnaz");
        userService.addFriend(adilbi.getId(), kamila.getId());
        userService.addFriend(kamila.getId(), dilnaz.getId());
        List<User> commonFriends = userService.getCommonFriends(adilbi.getId(), dilnaz.getId());
        assertEquals(1, commonFriends.size());
        assertEquals(kamila.getId(), commonFriends.get(0).getId());
    }

    @Test
    void shouldKeepFriendsAfterUpdate() {
        User adilbi = createUser("adilbi");
        User kamila = createUser("kamila");
        userService.addFriend(adilbi.getId(), kamila.getId());

        User update = new User();
        update.setId(adilbi.getId());
        update.setEmail("adilbi@gmail.com");
        update.setLogin("adilbi");
        update.setName("adilbi new");
        update.setBirthday(LocalDate.of(2000, 1, 1));

        userService.update(update);
        User saved = userService.findById(adilbi.getId());
        assertEquals("adilbi new", saved.getName());
        assertTrue(saved.getFriends().contains(kamila.getId()));
    }

    @Test
    void shouldThrowWhenFriendNotFound() {
        User adilbi = createUser("adilbi");
        assertThrows(NotFoundException.class, () -> userService.addFriend(adilbi.getId(), 99L));
    }


}
