package ru.practicum.shareit.user.dto.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Mapper
public interface UserMapper {
    UserDto toDto(User user);

    User toModel(UserDto userDto);
}
