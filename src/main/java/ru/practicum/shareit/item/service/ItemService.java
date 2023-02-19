package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Integer userId, ItemDto itemDto);

    ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto);

    ItemDto findItem(Integer itemId);

    List<ItemDto> findAllItemsByUser(Integer userId);

    List<ItemDto> searchItemForRent(String text);
}
