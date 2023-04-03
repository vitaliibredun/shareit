package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.validation.UserValidation;

import java.util.List;
import java.util.stream.Collectors;

@Service("userServiceImpl")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserValidation validation;
    private final UserMapper mapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = mapper.toModel(userDto);
        User userFromRepository = repository.save(user);
        return mapper.toDto(userFromRepository);
    }

    @Override
    public UserDto findUser(Integer userId) {
        User user = validation.checkUserExist(userId);
        return mapper.toDto(user);
    }

    @Override
    public List<UserDto> findAllUsers() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(Integer userId, UserDto userDto) {
        User user = validation.checkUserExist(userId);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        User userFromRepository = repository.saveAndFlush(user);
        return mapper.toDto(userFromRepository);
    }

    @Override
    public void deleteUser(Integer userId) {
        repository.deleteById(userId);
    }
}
