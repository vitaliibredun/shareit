package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplIT {
    private final EntityManager entityManager;
    private final UserService service;
    private final UserMapper mapper;

    @AfterEach
    void cleanUp() {
        resetIdColumns();
    }

    @Test
    void createUserTest() {
        UserDto userDto = makeUserDto("John", "mail@email.com");
        service.createUser(userDto);

        TypedQuery<User> query = entityManager
                .createQuery("select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void findAllUsersTest() {
        assertThat(service.findAllUsers(), empty());

        UserDto userDto1 = makeUserDto("John", "mail@email.com");
        UserDto userDto2 = makeUserDto("Smith", "yahoo@mail.com");
        UserDto userDto3 = makeUserDto("James", "mymail@email.com");
        service.createUser(userDto1);
        service.createUser(userDto2);
        service.createUser(userDto3);

        TypedQuery<User> query = entityManager.createQuery("select u from User u", User.class);
        List<User> users = query.getResultList();
        List<UserDto> userDtoList = users.stream().map(mapper::toDto).collect(Collectors.toList());

        assertThat(userDtoList.size(), notNullValue());
        assertThat(userDto1.getEmail(), equalTo(userDtoList.get(0).getEmail()));
        assertThat(userDto2.getEmail(), equalTo(userDtoList.get(1).getEmail()));
        assertThat(userDto3.getEmail(), equalTo(userDtoList.get(2).getEmail()));
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto.UserDtoBuilder builder = UserDto.builder();

        builder.name(name);
        builder.email(email);

        return builder.build();
    }

    private void resetIdColumns() {
        entityManager.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }
}
