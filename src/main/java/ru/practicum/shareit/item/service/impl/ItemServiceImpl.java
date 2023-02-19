package ru.practicum.shareit.item.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.ItemValidation;

import java.util.ArrayList;
import java.util.List;

@Service("itemServiceImpl")
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final ItemValidation itemValidation;

    @Autowired
    public ItemServiceImpl(@Qualifier("itemStorageInMemory") ItemStorage itemStorage, ItemValidation itemValidation) {
        this.itemStorage = itemStorage;
        this.itemValidation = itemValidation;
    }

    @Override
    public ItemDto createItem(Integer userId, ItemDto itemDto) {
        itemValidation.checkItemData(userId, itemDto);
        Item item = ItemMapper.toItem(itemDto);
        Item itemFromStorage = itemStorage.createItem(userId, item);
        return ItemMapper.toItemDto(itemFromStorage);
    }

    @Override
    public ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto) {
        itemValidation.checkOwnerOfItem(userId, itemId);
        Item item = ItemMapper.toItem(itemDto);
        Item itemFromStorage = itemStorage.updateItem(userId, itemId, item);
        return ItemMapper.toItemDto(itemFromStorage);
    }

    @Override
    public ItemDto findItem(Integer itemId) {
        Item item = itemStorage.findItem(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> findAllItemsByUser(Integer userId) {
        List<Item> allItemsByUser = itemStorage.findAllItemsByUser(userId);
        return ItemMapper.toItemDtoList(allItemsByUser);
    }

    @Override
    public List<ItemDto> searchItemForRent(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        String textToStorage = text.toLowerCase();
        List<Item> items = itemStorage.searchItemForRent(textToStorage);
        return ItemMapper.toItemDtoList(items);
    }
}
