package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.request.dto.ItemRequestInfo;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final RequestService service;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return service.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestInfo> findAllItemRequestsByUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return service.findAllItemRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestInfo> findAllItemRequests(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                     @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                                     @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return service.findAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestInfo findItemRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                           @PathVariable Integer requestId) {
        return service.findItemRequest(userId, requestId);
    }
}
