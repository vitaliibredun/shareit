package ru.practicum.shareit.validation.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.WrongOwnerOfItemException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validation.ItemValidation;

@Component("itemValidationRepository")
@Slf4j
@RequiredArgsConstructor
public class ItemValidationRepository implements ItemValidation {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public void checkItemData(Integer userId, ItemDto itemDto) {
        checkFieldAvailability(itemDto);
        checkFieldName(itemDto);
        checkFieldDescription(itemDto);
        checkUserExist(userId);
    }

    @Override
    public void checkOwnerOfItem(Integer userId, Integer itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        boolean ownerOfItem = item.getOwner().equals(userId);
        if (!ownerOfItem) {
            log.error("Validation failed. The wrong owner with id {} of item {}", userId, item);
            throw new WrongOwnerOfItemException("The wrong owner of item");
        }
    }

    @Override
    public void checkIfItemExist(Integer itemId) {
        boolean noItemExist = itemRepository.findById(itemId).isEmpty();
        if (noItemExist) {
            log.error("Validation failed. The item with the id {} doesn't exists", itemId);
            throw new ItemNotFoundException("The item with the id doesn't exists");
        }
    }

    private void checkUserExist(Integer userId) {
        boolean userExist = userRepository.findById(userId).isPresent();
        if (!userExist) {
            log.error("Validation failed. The user with the id {} doesn't exists", userId);
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
