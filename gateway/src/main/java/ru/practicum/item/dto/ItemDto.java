package ru.practicum.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Integer id;
    @NotEmpty(message = "The field of name is empty")
    private String name;
    @NotEmpty(message = "The field of description is empty")
    private String description;
    @NotNull(message = "The field of availability is empty")
    private Boolean available;
    private Integer requestId;
}
