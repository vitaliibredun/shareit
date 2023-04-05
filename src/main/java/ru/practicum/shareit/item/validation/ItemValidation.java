package ru.practicum.shareit.item.validation;

import ru.practicum.shareit.item.model.Item;

public interface ItemValidation {
    Item checkOwnerOfItem(Integer userId, Integer itemId);

    Item checkIfItemExist(Integer itemId);
}
