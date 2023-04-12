package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto findUser(Integer userId);

    UserDto updateUser(Integer userId, UserDto userDto);

    void deleteUser(Integer userId);

    List<UserDto> findAllUsers();
}
