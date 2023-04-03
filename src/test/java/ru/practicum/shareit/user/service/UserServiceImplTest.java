package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
public class UserServiceImplTest {
    private final UserService service;
    private final UserRepository repository;
    private final EntityManager entityManager;
    private final UserMapper mapper;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        User userToSave = makeUser("John", "email@mail.com");
        User user = repository.save(userToSave);
        userDto = mapper.toDto(user);
    }

    @AfterEach
    void cleanUp() {
        resetIdColumns();
    }

    @Test
    void findUserTest() {
        assertThat(repository.findAll(), notNullValue());

        UserDto userFromRepository = service.findUser(userDto.getId());

        assertThat(userDto.getId(), is(userFromRepository.getId()));
        assertThat(userDto.getName(), is(userFromRepository.getName()));
        assertThat(userDto.getEmail(), is(userFromRepository.getEmail()));
    }

    @Test
    void updateUserNameTest() {
        assertThat(repository.findAll(), notNullValue());

        UserDto userWithNewName = UserDto.builder().name("Kent").build();
        UserDto userFromRepository = service.updateUser(userDto.getId(), userWithNewName);

        assertThat(userDto.getId(), is(userFromRepository.getId()));
        assertThat(userWithNewName.getName(), is(userFromRepository.getName()));
        assertThat(userDto.getEmail(), is(userFromRepository.getEmail()));
    }

    @Test
    void updateUserEmailTest() {
        assertThat(repository.findAll(), notNullValue());

        UserDto userWithNewEmail = UserDto.builder().email("super@mail.com").build();
        UserDto userFromRepository = service.updateUser(userDto.getId(), userWithNewEmail);

        assertThat(userDto.getId(), is(userFromRepository.getId()));
        assertThat(userDto.getName(), is(userFromRepository.getName()));
        assertThat(userWithNewEmail.getEmail(), is(userFromRepository.getEmail()));
    }

    @Test
    void updateUserTest() {
        assertThat(repository.findAll(), notNullValue());

        UserDto userWithNewNameAndEmail = UserDto.builder().name("Kent").email("super@mail.com").build();
        UserDto userFromRepository = service.updateUser(userDto.getId(), userWithNewNameAndEmail);

        assertThat(userDto.getId(), is(userFromRepository.getId()));
        assertThat(userWithNewNameAndEmail.getName(), is(userFromRepository.getName()));
        assertThat(userWithNewNameAndEmail.getEmail(), is(userFromRepository.getEmail()));
    }

    @Test
    void deleteUserTest() {
        assertThat(repository.findAll(), notNullValue());

        service.deleteUser(userDto.getId());

        assertThat(repository.findAll(), empty());
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
