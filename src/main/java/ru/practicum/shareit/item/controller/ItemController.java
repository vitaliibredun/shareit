package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfo;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @PathVariable Integer itemId, @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemInfo findItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                             @PathVariable Integer itemId) {
        return itemService.findItem(userId, itemId);
    }

    @GetMapping
    public List<ItemInfo> findAllItemsByUser(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                             @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return itemService.findAllItemsByUser(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemForRent(@RequestParam("text") String text,
                                           @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                           @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return itemService.searchItemForRent(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                 @PathVariable Integer itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
