package ru.practicum.shareit.validation;

import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemValidation {
    void checkItemData(Integer userId, ItemDto itemDto);

    void checkOwnerOfItem(Integer userId, Integer itemId);

    void checkIfItemExist(Integer itemId);
}
