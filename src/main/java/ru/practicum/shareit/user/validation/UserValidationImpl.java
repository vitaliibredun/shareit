package ru.practicum.shareit.user.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

@Component("userValidationRepository")
@Slf4j
@RequiredArgsConstructor
public class UserValidationImpl implements UserValidation {
    private final UserRepository repository;

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
