package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.client.UserClient;
import ru.practicum.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient client;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Creating ru.practicum.user {}", userDto);
        return client.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findUser(@PathVariable Integer userId) {
        log.info("Get ru.practicum.user with id={}", userId);
        return client.findUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllUsers() {
        log.info("Get all users");
        return client.findAllUsers();
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Integer userId, @RequestBody UserDto userDto) {
        log.info("Update ru.practicum.user with id={} and body {}", userId, userDto);
        return client.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Integer userId) {
        log.info("Delete ru.practicum.user with id ={}", userId);
        return client.deleteUser(userId);
    }
}
