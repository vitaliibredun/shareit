package ru.practicum.shareit.user.validation.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EmailAlreadyExistException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.validation.UserValidation;

import java.util.List;
import java.util.Optional;

@Component("userValidationRepository")
@Slf4j
@RequiredArgsConstructor
public class UserValidationRepository implements UserValidation {
    private final UserRepository repository;

    @Override
    public void checkIfEmailAlreadyExist(UserDto userDto) {
        List<User> users = repository.findAll();
        boolean emailIsAlreadyExist = users.stream().anyMatch(user -> user.getEmail().equals(userDto.getEmail()));
        if (emailIsAlreadyExist) {
            log.error("Validation failed. The email {} is already exist", userDto.getEmail());
            throw new EmailAlreadyExistException("The email is already exist");
        }
    }

    @Override
    public User checkUserExist(Integer userId) {
        Optional<User> user = repository.findById(userId);
        if (user.isEmpty()) {
            log.error("Validation failed. The user with id {} doesn't exist", userId);
            throw new UserNotFoundException("The user doesn't exist");
        }
        return user.get();
    }
}
