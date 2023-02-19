package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item createItem(Integer userId, Item item);

    Item updateItem(Integer userId, Integer itemId, Item item);

    Item findItem(Integer itemId);

    List<Item> findAllItemsByUser(Integer userId);

    List<Item> searchItemForRent(String text);
}
