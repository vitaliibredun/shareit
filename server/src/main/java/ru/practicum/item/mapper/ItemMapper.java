package ru.practicum.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.booking.dto.LastBooking;
import ru.practicum.booking.dto.NextBooking;
import ru.practicum.comments.dto.CommentInfo;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ItemInfo;
import ru.practicum.item.model.Item;
import ru.practicum.request.model.ItemRequest;

import java.util.List;

@Mapper
public interface ItemMapper {
    default ItemDto toDto(Item item) {
        if (item == null) {
            return null;
        }

        ItemDto.ItemDtoBuilder itemDtoBuilder = ItemDto.builder();

        itemDtoBuilder.id(item.getId());
        itemDtoBuilder.name(item.getName());
        itemDtoBuilder.description(item.getDescription());
        itemDtoBuilder.available(item.getAvailable());
        if (item.getRequest() != null) {
            itemDtoBuilder.requestId(item.getRequest().getId());
        }

        return itemDtoBuilder.build();
    }

    default Item toModel(ItemDto itemDto) {
        if (itemDto == null) {
            return null;
        }

        Item.ItemBuilder itemBuilder = Item.builder();

        itemBuilder.id(itemDto.getId());
        itemBuilder.name(itemDto.getName());
        itemBuilder.description(itemDto.getDescription());
        itemBuilder.available(itemDto.getAvailable());

        return itemBuilder.build();
    }

    default ItemInfo toDto(Item item, List<CommentInfo> comments,
                           LastBooking lastBooking, NextBooking nextBooking) {

        if (item == null) {
            return null;
        }

        ItemInfo.ItemInfoBuilder infoBuilder = ItemInfo.builder();

        infoBuilder.id(item.getId());
        infoBuilder.name(item.getName());
        infoBuilder.description(item.getDescription());
        infoBuilder.available(item.getAvailable());
        infoBuilder.comments(comments);
        infoBuilder.lastBooking(lastBooking);
        infoBuilder.nextBooking(nextBooking);

        return infoBuilder.build();
    }

    default Item toModel(ItemDto itemDto, ItemRequest itemRequest) {
        if (itemDto == null) {
            return null;
        }

        Item.ItemBuilder itemBuilder = Item.builder();

        itemBuilder.id(itemDto.getId());
        itemBuilder.name(itemDto.getName());
        itemBuilder.description(itemDto.getDescription());
        itemBuilder.available(itemDto.getAvailable());
        itemBuilder.request(itemRequest);

        return itemBuilder.build();
    }
}
