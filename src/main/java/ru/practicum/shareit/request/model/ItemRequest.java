package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequest {
    private Integer id;
    private String description;
    private Integer requestor;
    private final LocalDateTime created;
}
