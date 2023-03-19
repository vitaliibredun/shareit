package ru.practicum.shareit.item.repository.impl;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component("itemStorageInMemory")
public class ItemStorageImpl implements ItemStorage {
    private final Map<Integer, Item> allItems = new HashMap<>();
    private Integer itemId = 0;

    @Override
    public Item createItem(Integer userId, Item item) {
        item.setId(addId());
        item.setOwner(User.builder().id(userId).build());
        allItems.put(itemId, item);
        return allItems.get(item.getId());
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
            Item itemFromStorage = allItems.get(itemId);
            itemFromStorage.setName(newName);
            itemFromStorage.setDescription(newDescription);
            itemFromStorage.setAvailable(newAvailability);
        }
        if (updateItemName) {
            String newName = item.getName();
            Item itemFromStorage = allItems.get(itemId);
            itemFromStorage.setName(newName);
        }
        if (updateItemDescription) {
            String newDescription = item.getDescription();
            Item itemFromStorage = allItems.get(itemId);
            itemFromStorage.setDescription(newDescription);
        }
        if (updateItemAvailable) {
            Boolean newAvailable = item.getAvailable();
            Item itemFromStorage = allItems.get(itemId);
            itemFromStorage.setAvailable(newAvailable);
        }
        return allItems.get(itemId);
    }

    @Override
    public Item findItem(Integer itemId) {
        return allItems.get(itemId);
    }

    @Override
    public List<Item> findAllItemsByUser(Integer userId) {
        List<Item> items = new ArrayList<>();
        for (Item item : allItems.values()) {
            boolean itemFromUser = item.getOwner().getId().equals(userId);
            if (itemFromUser) {
                items.add(item);
            }
        }
        return items;
    }

    @Override
    public List<Item> searchItemForRent(String text) {
        List<Item> items = new ArrayList<>();
        for (Item item : allItems.values()) {
            boolean isInName = item.getName().toLowerCase().contains(text);
            boolean isInDescription = item.getDescription().toLowerCase().contains(text);
            boolean isAvailable = item.getAvailable().equals(true);
            if ((isInName || isInDescription) && isAvailable) {
                items.add(item);
            }
        }
        return items;
    }

    private Integer addId() {
        return ++itemId;
    }
}
