package ru.practicum.shareit.validation.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.UserValidation;

import java.util.Objects;

@Component("userValidationRepository")
@Slf4j
@RequiredArgsConstructor
public class UserValidationRepository implements UserValidation {
    private final UserRepository repository;

    @Override
    public void checkUserData(UserDto userDto) {
        checkEmptyEmail(userDto);
        checkIncorrectType(userDto);
    }

    @Override
    public void checkUserExist(Integer userId) {
        boolean userNotExist = repository.findById(userId).isEmpty();
        if (userNotExist) {
            log.error("Validation failed. The user with id {} doesn't exist", userId);
            throw new UserNotFoundException("The user doesn't exist");
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
