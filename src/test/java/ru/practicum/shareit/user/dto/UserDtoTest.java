package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> userDtoTester;

    @Test
    void verifyCreateUserDto() throws IOException {
        UserDto userDto = makeUserDto(1, "John", "email@mail.com");

        JsonContent<UserDto> result = userDtoTester.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("email@mail.com");
    }

    private UserDto makeUserDto(Integer id, String name, String email) {
        UserDto.UserDtoBuilder builder = UserDto.builder();

        builder.id(id);
        builder.name(name);
        builder.email(email);

        return builder.build();
    }
}