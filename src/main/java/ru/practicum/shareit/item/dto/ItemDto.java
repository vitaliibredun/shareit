package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Integer id;
    @NotEmpty(message = "The field of name is empty")
    private String name;
    @NotEmpty(message = "The field of description is empty")
    private String description;
    @NotNull(message = "The field of availability is empty")
    private Boolean available;
}