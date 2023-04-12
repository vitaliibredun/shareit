package ru.practicum.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestInfo;
import ru.practicum.request.model.ItemRequest;

import java.util.List;

@Mapper
public interface RequestMapper {
    ItemRequestDto toDto(ItemRequest itemRequest);

    ItemRequest toModel(ItemRequestDto itemRequestDto);

    default ItemRequestInfo toDto(ItemRequest itemRequest, List<ItemDto> items) {
        if (itemRequest == null) {
            return null;
        }

        ItemRequestInfo.ItemRequestInfoBuilder infoBuilder = ItemRequestInfo.builder();

        infoBuilder.id(itemRequest.getId());
        infoBuilder.description(itemRequest.getDescription());
        infoBuilder.created(itemRequest.getCreated());
        infoBuilder.items(items);

        return infoBuilder.build();
    }
}
