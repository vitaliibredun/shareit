package ru.practicum.shareit.item.validation.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.WrongOwnerOfItemException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.item.validation.ItemValidation;

import java.util.Optional;

@Component("itemValidationRepository")
@Slf4j
@RequiredArgsConstructor
public class ItemValidationRepository implements ItemValidation {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public User checkItemData(Integer userId, ItemDto itemDto) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.error("Validation failed. The user with the id {} doesn't exists", userId);
            throw new UserNotFoundException("The user with the id doesn't exists");
        }
        return user.get();
    }

    @Override
    public Item checkOwnerOfItem(Integer userId, Integer itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent()) {
            boolean ownerOfItem = item.get().getOwner().getId().equals(userId);
            if (!ownerOfItem) {
                log.error("Validation failed. The wrong owner with id {} of item {}", userId, item);
                throw new WrongOwnerOfItemException("The wrong owner of item");
            }
        }
        return item.get();
    }

    @Override
    public Item checkIfItemExist(Integer itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            log.error("Validation failed. The item with the id {} doesn't exists", itemId);
            throw new ItemNotFoundException("The item with the id doesn't exists");
        }
        return item.get();
    }
}
