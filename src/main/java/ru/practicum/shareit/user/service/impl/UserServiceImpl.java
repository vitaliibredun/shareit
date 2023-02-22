package ru.practicum.shareit.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.UserValidation;

import java.util.List;

@Service("userServiceImpl")
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserValidation userValidation;

    @Autowired
    public UserServiceImpl(@Qualifier("userStorageInMemory") UserStorage userStorage, UserValidation userValidation) {
        this.userStorage = userStorage;
        this.userValidation = userValidation;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userValidation.checkUserData(userDto);
        User user = UserMapper.toUser(userDto);
        User userFromStorage = userStorage.createUser(user);
        return UserMapper.toUserDto(userFromStorage);
    }

    @Override
    public UserDto findUser(Integer userId) {
        User userFromStorage = userStorage.findUser(userId);
        return UserMapper.toUserDto(userFromStorage);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> allUsers = userStorage.findAllUsers();
        return UserMapper.toUserDtoList(allUsers);
    }

    @Override
    public UserDto updateUser(Integer userId, UserDto userDto) {
        userValidation.checkEmail(userId, userDto);
        userDto.setId(userId);
        User user = UserMapper.toUser(userDto);
        User userFromStorage = userStorage.updateUser(userId, user);
        return UserMapper.toUserDto(userFromStorage);
    }

    @Override
    public boolean deleteUser(Integer userId) {
        return userStorage.deleteUser(userId);
    }
}
