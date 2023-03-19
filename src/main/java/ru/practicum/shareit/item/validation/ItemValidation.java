package ru.practicum.shareit.item.validation;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public interface ItemValidation {
    User checkItemData(Integer userId, ItemDto itemDto);

    Item checkOwnerOfItem(Integer userId, Integer itemId);

    Item checkIfItemExist(Integer itemId);
}
