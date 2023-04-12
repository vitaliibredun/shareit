package ru.practicum.user.validation;

import ru.practicum.user.model.User;

public interface UserValidation {

    User checkUserExist(Integer userId);
}
