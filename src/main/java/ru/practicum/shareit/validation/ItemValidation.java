package ru.practicum.shareit.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;

@Component
@Slf4j
public class ItemValidation {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    public ItemValidation(@Qualifier("userStorageInMemory") UserStorage userStorage,
                          @Qualifier("itemStorageInMemory") ItemStorage itemStorage) {
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }

    public void checkItemData(Integer userId, ItemDto itemDto) {
        checkUserExist(userId);
        checkFieldAvailability(itemDto);
        checkFieldName(itemDto);
        checkFieldDescription(itemDto);
    }

    public void checkOwnerOfItem(Integer userId, Integer itemId) {
        Item item = itemStorage.findItem(itemId);
        if (!item.getOwner().equals(userId)) {
            log.error("Validation failed. The wrong owner with id {} of item {}", userId, item);
            throw new WrongOwnerOfItemException("The wrong owner of item");
        }
    }

    private void checkUserExist(Integer userId) {
        boolean userExist = userStorage.findUser(userId) != null;
        if (!userExist) {
            log.error("Validation failed. The user with the id doesn't exists {}", userId);
            throw new UserNotFoundException("The user with the id doesn't exists");
        }
    }

    private void checkFieldAvailability(ItemDto itemDto) {
        boolean fieldAvailabilityIsEmpty = itemDto.getAvailable() == null;
        if (fieldAvailabilityIsEmpty) {
            log.error("Validation failed. The field of availability is empty");
            throw new ValidationException("The field of availability is empty");
        }
    }

    private void checkFieldName(ItemDto itemDto) {
        boolean fieldNameIsEmpty = itemDto.getName() == null || itemDto.getName().isEmpty();
        if (fieldNameIsEmpty) {
            log.error("Validation failed. The field of name is empty");
            throw new ValidationException("The field of name is empty");
        }
    }

    private void checkFieldDescription(ItemDto itemDto) {
        boolean fieldDescriptionIsEmpty = itemDto.getDescription() == null;
        if (fieldDescriptionIsEmpty) {
            log.error("Validation failed. The field of description is empty");
            throw new ValidationException("The field of description is empty");
        }
    }
}
