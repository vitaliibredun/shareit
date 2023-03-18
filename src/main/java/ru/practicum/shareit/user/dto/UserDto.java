package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class UserDto {
    private Integer id;
    private String name;
    @NotEmpty(message = "The email field is empty")
    @Email(message = "The incorrect type of email")
    private String email;
}
