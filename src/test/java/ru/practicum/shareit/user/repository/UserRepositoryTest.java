package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    private final TestEntityManager entityManager;
    private final UserRepository repository;

    @AfterEach
    void cleanUp() {
        resetIdColumns();
    }

    @Test
    void verifyRepositoryIsEmpty() {
        List<User> users = repository.findAll();

        assertThat(users, empty());
    }

    @Test
    void verifyPersistingOfUser() {
        User user = User
                .builder()
                .name("Bond")
                .email("email@mail.com")
                .build();

        assertThat(entityManager.getId(user), nullValue());
        entityManager.persist(user);
        assertThat(entityManager.getId(user), notNullValue());
    }

    @Test
    void verifyUserSaveInRepository() {
        User user = User
                .builder()
                .name("Bond")
                .email("email@mail.com")
                .build();

        assertThat(entityManager.getId(user), nullValue());
        repository.save(user);
        assertThat(entityManager.getId(user), notNullValue());
    }

    @Test
    void verifyUserIsSameEntity() {
        User user = User
                .builder()
                .name("Bond")
                .email("email@mail.com")
                .build();

        assertThat(entityManager.getId(user), nullValue());
        User userFromRepository = repository.save(user);
        assertThat(entityManager.getId(user), notNullValue());
        assertThat(user.getName(), sameInstance(userFromRepository.getName()));
        assertThat(user.getEmail(), sameInstance(userFromRepository.getEmail()));
    }

    @Test
    void verifyRepositorySaveSameQuantityOfEntities() {
        Integer expectedSize = 2;

        User user1 = User
                .builder()
                .name("Bond")
                .email("email@mail.com")
                .build();

        User user2 = User
                .builder()
                .name("James")
                .email("mymail@email.com")
                .build();

        assertThat(repository.findAll(), empty());
        assertThat(entityManager.getId(user1), nullValue());
        assertThat(entityManager.getId(user2), nullValue());
        repository.save(user1);
        repository.save(user2);
        assertThat(entityManager.getId(user1), notNullValue());
        assertThat(entityManager.getId(user2), notNullValue());
        assertThat(repository.findAll().size(), equalTo(expectedSize));
    }

    private void resetIdColumns() {
        entityManager.getEntityManager().createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }
}
