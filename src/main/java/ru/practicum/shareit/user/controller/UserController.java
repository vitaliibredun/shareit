package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto findUser(@PathVariable Integer userId) {
        return userService.findUser(userId);
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        return userService.findAllUsers();
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Integer userId, @RequestBody UserDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
    }
}
