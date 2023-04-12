package ru.practicum.item.validation;

import ru.practicum.item.model.Item;

public interface ItemValidation {
    Item checkOwnerOfItem(Integer userId, Integer itemId);

    Item checkIfItemExist(Integer itemId);
}
