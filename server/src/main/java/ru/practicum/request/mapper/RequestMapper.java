package ru.practicum.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestInfo;
import ru.practicum.request.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RequestMapper {

    ItemRequestDto toDto(ItemRequest itemRequest);

    ItemRequest toModel(ItemRequestDto itemRequestDto);

    ItemRequestInfo toDto(ItemRequest itemRequest, List<ItemDto> items);
}
