package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfo;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Integer userId, ItemDto itemDto);

    ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto);

    ItemInfo findItem(Integer userId, Integer itemId);

    List<ItemInfo> findAllItemsByUser(Integer userId, Integer from, Integer size);

    List<ItemDto> searchItemForRent(String text, Integer from, Integer size);

    CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto);
}
