package ru.practicum.shareit.user.repository.impl;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.repository.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component("userStorageInMemory")
public class UserStorageImpl implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer counter = 0;

    @Override
    public User createUser(User user) {
        user.setId(addId());
        users.put(counter, user);
        return users.get(user.getId());
    }

    @Override
    public User findUser(Integer userId) {
        return users.get(userId);
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(Integer userId, User user) {
        boolean updateUserNameAndEmail = (user.getName() != null) && (user.getEmail() != null);
        boolean updateUserName = (user.getName() != null) && (user.getEmail() == null);
        boolean updateUserEmail = (user.getEmail() != null) && (user.getName() == null);
        if (updateUserNameAndEmail) {
            users.replace(userId, user);
        }
        if (updateUserName) {
            String newName = user.getName();
            users.get(userId).setName(newName);
        }
        if (updateUserEmail) {
            String newEmail = user.getEmail();
            users.get(userId).setEmail(newEmail);
        }
        return users.get(userId);
    }

    @Override
    public boolean deleteUser(Integer userId) {
        return users.remove(userId) != null;
    }

    private Integer addId() {
        return ++counter;
    }
}
