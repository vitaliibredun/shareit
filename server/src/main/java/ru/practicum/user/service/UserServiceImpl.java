package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.user.validation.UserValidation;

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
    @Cacheable(cacheNames = {"findUser"}, key = "#userId")
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
    @CachePut(cacheNames = {"updateUser"}, key = "#userId")
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
    @CacheEvict(cacheNames = {"deleteUser"}, key = "#userId")
    public void deleteUser(Integer userId) {
        repository.deleteById(userId);
    }
}
