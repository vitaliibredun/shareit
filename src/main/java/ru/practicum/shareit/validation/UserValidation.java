package ru.practicum.shareit.validation;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserValidation {
    void checkUserData(UserDto userDto);

    void checkUserExist(Integer userId);
}
