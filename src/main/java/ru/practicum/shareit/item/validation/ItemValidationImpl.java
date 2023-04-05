package ru.practicum.shareit.item.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.WrongOwnerOfItemException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;

@Component("itemValidationRepository")
@Slf4j
@RequiredArgsConstructor
public class ItemValidationImpl implements ItemValidation {
    private final ItemRepository itemRepository;

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
        return item.orElseThrow();
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
