package ru.practicum.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Integer id;
    @NotEmpty(message = "The text's comment is empty")
    private String text;
    private String authorName;
    private LocalDateTime created = LocalDateTime.now();
}
