package ru.practicum.shareit.user.validation;

import ru.practicum.shareit.user.model.User;

public interface UserValidation {

    User checkUserExist(Integer userId);
}
