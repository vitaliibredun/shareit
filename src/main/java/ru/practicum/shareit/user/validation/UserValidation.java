package ru.practicum.shareit.user.validation;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserValidation {
    void checkIfEmailAlreadyExist(UserDto userDto);

    User checkUserExist(Integer userId);
}
