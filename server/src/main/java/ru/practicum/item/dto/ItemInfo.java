package ru.practicum.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.booking.dto.LastBooking;
import ru.practicum.booking.dto.NextBooking;
import ru.practicum.comments.dto.CommentInfo;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemInfo {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentInfo> comments;
    private LastBooking lastBooking;
    private NextBooking nextBooking;
}
