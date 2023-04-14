package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestInfo;
import ru.practicum.request.service.RequestService;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final RequestService service;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @RequestBody ItemRequestDto itemRequestDto) {
        return service.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestInfo> findAllItemRequestsByUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return service.findAllItemRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestInfo> findAllItemRequests(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        return service.findAllItemRequests(userId, PageRequest.of(from, size, Sort.by("created").descending()));
    }

    @GetMapping("/{requestId}")
    public ItemRequestInfo findItemRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                           @PathVariable Integer requestId) {
        return service.findItemRequest(userId, requestId);
    }
}
