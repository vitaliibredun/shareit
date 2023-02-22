package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User findUser(Integer userId);

    User updateUser(Integer userId, User user);

    boolean deleteUser(Integer userId);

    List<User> findAllUsers();
}
