package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Booker {
    private Integer id;
    private String name;
}
