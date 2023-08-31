package ru.practicum.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.booking.dto.LastBooking;
import ru.practicum.booking.dto.NextBooking;
import ru.practicum.comments.dto.CommentInfo;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ItemInfo;
import ru.practicum.item.model.Item;
import ru.practicum.request.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    ItemDto toDto(Item item);

    Item toModel(ItemDto itemDto);

    @Mapping(target = "id", source = "item.id")
    ItemInfo toDto(Item item, List<CommentInfo> comments,
                   LastBooking lastBooking, NextBooking nextBooking);

    @Mapping(target = "id", source = "itemDto.id")
    @Mapping(target = "description", source = "itemDto.description")
    Item toModel(ItemDto itemDto, ItemRequest itemRequest);
}
