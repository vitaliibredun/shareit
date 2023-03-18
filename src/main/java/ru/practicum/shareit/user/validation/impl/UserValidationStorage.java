package ru.practicum.shareit.user.validation.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EmailAlreadyExistException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.validation.UserValidation;

import java.util.Objects;

@Component
@Slf4j
public class UserValidationStorage implements UserValidation {
    private final UserStorage userStorage;

    public UserValidationStorage(@Qualifier("userStorageInMemory") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public void checkIfEmailAlreadyExist(UserDto userDto) {
        checkEmptyEmail(userDto);
        checkIncorrectType(userDto);
        checkSameEmailWithAnotherUser(userDto);
    }

    @Override
    public User checkUserExist(Integer userId) {
        throw new IllegalArgumentException("Not implemented");
    }

    private void checkSameEmailWithAnotherUser(UserDto userDto) {
        boolean sameEmailWithAnotherUser = userStorage.findAllUsers()
                .stream()
                .anyMatch(user -> user.getEmail().equals(userDto.getEmail()));
        if (sameEmailWithAnotherUser) {
            log.error("Validation failed. The email is already exist {}", userDto.getEmail());
            throw new EmailAlreadyExistException("The email is already exist");
        }
    }

    private void checkEmptyEmail(UserDto userDto) {
        boolean emptyEmail = userDto.getEmail() == null;
        if (emptyEmail) {
            log.error("Validation failed. The email field is empty");
            throw new ValidationException("The email field is empty");
        }
    }

    private void checkIncorrectType(UserDto userDto) {
        boolean incorrectType = Objects.requireNonNull(userDto.getEmail()).contains("@");
        if (!incorrectType) {
            log.error("Validation failed. The incorrect type of email {}", userDto.getEmail());
            throw new ValidationException("The incorrect type of email");
        }
    }
}
