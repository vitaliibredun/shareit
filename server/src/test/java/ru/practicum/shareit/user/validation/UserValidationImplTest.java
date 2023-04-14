package ru.practicum.shareit.user.validation;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.UserNotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.user.validation.UserValidation;

import javax.persistence.EntityManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
public class UserValidationImplTest {
    private final UserValidation validation;
    private final UserRepository repository;
    private final EntityManager entityManager;

    @BeforeEach
    void setUp() {
        User userToSave = makeUser("John", "email@mail.com");
        repository.save(userToSave);
    }

    @AfterEach
    void cleanUp() {
        resetIdColumns();
    }

    @Test
    void verifyUserExistException() {
        assertThat(repository.findAll(), notNullValue());

        User someUser = makeUser("Bond", "some@email.com");
        someUser.setId(100);

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class, () -> validation.checkUserExist(someUser.getId()));

        assertThat("The user doesn't exist", is(exception.getMessage()));
    }

    private User makeUser(String name, String email) {
        User.UserBuilder builder = User.builder();

        builder.name(name);
        builder.email(email);

        return builder.build();
    }

    private void resetIdColumns() {
        entityManager.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }
}
