package ru.practicum.shareit.item.dao.impl;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component("itemStorageInMemory")
public class ItemStorageInMemory implements ItemStorage {
    private final Map<Integer, List<Item>> itemsByUsers = new HashMap<>();
    private Integer itemId = 0;
    
    @Override
    public Item createItem(Integer userId, Item item) {
        boolean userExist = itemsByUsers.containsKey(userId);
        if (!userExist) {
            List<Item> items = new ArrayList<>();
            item.setId(addId());
            item.setOwner(userId);
            items.add(item);
            itemsByUsers.put(userId, items);
        }
        if (userExist) {
            List<Item> items = itemsByUsers.get(userId);
            item.setId(addId());
            item.setOwner(userId);
            items.add(item);
        }
        return itemsByUsers.get(userId)
                .stream()
                .reduce((firstItem, ItemFromStorage) -> ItemFromStorage)
                .orElse(null);
    }

    @Override
    public Item updateItem(Integer userId, Integer itemId, Item item) {
        boolean updateItem = (item.getName() != null)
                && (item.getDescription() != null)
                && (item.getAvailable() != null);
        boolean updateItemName = (item.getName() != null)
                && (item.getDescription() == null)
                && (item.getAvailable() == null);
        boolean updateItemDescription = (item.getName() == null) && (item.getDescription() != null)
                && (item.getAvailable() == null);
        boolean updateItemAvailable = (item.getName() == null)
                && (item.getDescription() == null)
                && (item.getAvailable() != null);

        if (updateItem) {
            String newName = item.getName();
            String newDescription = item.getDescription();
            Boolean newAvailability = item.getAvailable();
            List<Item> items = itemsByUsers.get(userId);
            Item itemFromStorage = items.stream().filter(i -> i.getId().equals(itemId)).findFirst().orElseThrow();
            itemFromStorage.setName(newName);
            itemFromStorage.setDescription(newDescription);
            itemFromStorage.setAvailable(newAvailability);
        }
        if (updateItemName) {
            String newName = item.getName();
            List<Item> items = itemsByUsers.get(userId);
            Item itemFromStorage = items.stream().filter(i -> i.getId().equals(itemId)).findFirst().orElseThrow();
            itemFromStorage.setName(newName);
        }
        if (updateItemDescription) {
            String newDescription = item.getDescription();
            List<Item> items = itemsByUsers.get(userId);
            Item itemFromStorage = items.stream().filter(i -> i.getId().equals(itemId)).findFirst().orElseThrow();
            itemFromStorage.setDescription(newDescription);
        }
        if (updateItemAvailable) {
            Boolean newAvailable = item.getAvailable();
            List<Item> items = itemsByUsers.get(userId);
            Item itemFromStorage = items.stream().filter(i -> i.getId().equals(itemId)).findFirst().orElseThrow();
            itemFromStorage.setAvailable(newAvailable);
        }


        return itemsByUsers.get(userId)
                .stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Item findItem(Integer itemId) {
        return itemsByUsers.values()
                .stream()
                .flatMap(List::stream)
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Item> findAllItemsByUser(Integer userId) {
        return itemsByUsers.get(userId);
    }

    @Override
    public List<Item> searchItemForRent(String text) {
        return itemsByUsers.values()
                .stream()
                .flatMap(List::stream)
                .filter(item -> item.getAvailable().equals(true))
                .filter(item -> item.getDescription().toLowerCase().contains(text))
                .collect(Collectors.toList());
    }

    private Integer addId() {
        return ++itemId;
    }
}
