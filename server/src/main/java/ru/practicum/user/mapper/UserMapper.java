package ru.practicum.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

@Mapper
public interface UserMapper {
    UserDto toDto(User user);

    User toModel(UserDto userDto);
}
