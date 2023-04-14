package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.client.RequestClient;
import ru.practicum.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final RequestClient client;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Creating ru.practicum.request {} from ru.practicum.user with id={}", itemRequestDto, userId);
        return client.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllItemRequestsByUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Get all requests to ru.practicum.user with id={}", userId);
        return client.findAllItemRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllItemRequests(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        log.info("Get all requests with ru.practicum.user id={}", userId);
        return client.findAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findItemRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                           @PathVariable Integer requestId) {
        return client.findItemRequest(userId, requestId);
    }
}
