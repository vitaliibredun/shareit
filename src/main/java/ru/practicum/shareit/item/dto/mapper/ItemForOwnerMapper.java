package ru.practicum.shareit.item.dto.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.LastBooking;
import ru.practicum.shareit.booking.dto.NextBooking;
import ru.practicum.shareit.comments.dto.CommentInfo;
import ru.practicum.shareit.item.dto.ItemInfo;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
public class ItemForOwnerMapper {
    public ItemInfo toDto(Item item, List<CommentInfo> comments,
                          LastBooking lastBooking, NextBooking nextBooking) {
        return ItemInfo
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comments)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();
    }
}
