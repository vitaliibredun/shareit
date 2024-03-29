package ru.practicum.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ItemInfo;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Integer userId, ItemDto itemDto);

    ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto);

    ItemInfo findItem(Integer userId, Integer itemId);

    List<ItemInfo> findAllItemsByUser(Integer userId, Pageable pageable);

    List<ItemDto> searchItemForRent(String text, Pageable pageable);

    CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto);
}
