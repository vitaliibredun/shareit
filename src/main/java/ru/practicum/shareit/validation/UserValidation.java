package ru.practicum.shareit.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EmailAlreadyExistException;
import ru.practicum.shareit.exceptions.EmailFieldIsEmptyException;
import ru.practicum.shareit.exceptions.IncorrectTypeEmailException;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Objects;

@Component
@Slf4j
public class UserValidation {
    private final UserStorage userStorage;

    public UserValidation(@Qualifier("userStorageInMemory") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void checkUserData(UserDto userDto) {
        checkEmptyEmail(userDto);
        checkIncorrectType(userDto);
        checkSameEmailWithAnotherUser(userDto);
    }

    public void checkEmail(Integer userId, UserDto userDto) {
        checkAsSameEmail(userId, userDto);
    }

    private void checkAsSameEmail(Integer userId, UserDto userDto) {
        boolean sameEmailToSameUser = userStorage.findUser(userId).getEmail().equals(userDto.getEmail());
        boolean sameEmailWithAnotherUser = userStorage.findAllUsers()
                .stream()
                .anyMatch(user -> user.getEmail().equals(userDto.getEmail()));
        if (sameEmailToSameUser) {
            return;
        }
        if (sameEmailWithAnotherUser) {
            log.error("Validation failed. The email is already exist {}", userDto.getEmail());
            throw new EmailAlreadyExistException("The email is already exist");
        }
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
            throw new EmailFieldIsEmptyException("The email field is empty");
        }
    }

    private void checkIncorrectType(UserDto userDto) {
        boolean incorrectType = Objects.requireNonNull(userDto.getEmail()).contains("@");
        if (!incorrectType) {
            log.error("Validation failed. The incorrect type of email {}", userDto.getEmail());
            throw new IncorrectTypeEmailException("The incorrect type of email");
        }
    }
}
