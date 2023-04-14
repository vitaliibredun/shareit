package ru.practicum.item.controller;

import ru.practicum.item.client.ItemClient;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                     @Valid @RequestBody ItemDto itemDto) {
        log.info("Creating ru.practicum.item {} from ru.practicum.user with id={}", itemDto, userId);
        return client.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @PathVariable Integer itemId, @RequestBody ItemDto itemDto) {
        log.info("Update ru.practicum.item with id={} from ru.practicum.user with id={} with body {}", itemId, userId, itemDto);
        return client.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                             @PathVariable Integer itemId) {
        log.info("Get ru.practicum.item with id={} from ru.practicum.user with id={}", itemId, userId);
        return client.findItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllItemsByUser(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        log.info("Get all items to ru.practicum.user with id={}", userId);
        return client.findAllItemsByUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemForRent(
            @RequestParam("text") String text,
            @PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        if (text.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        log.info("Get items for rent, by input={}", text);
        return client.searchItemForRent(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                 @PathVariable Integer itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return client.addComment(userId, itemId, commentDto);
    }
}
