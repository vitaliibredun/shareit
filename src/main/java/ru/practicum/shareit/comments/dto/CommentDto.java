package ru.practicum.shareit.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class CommentDto {
    private Integer id;
    private String text;
    private Integer author;
    private String authorName;
    @Builder.Default
    private LocalDateTime created = LocalDateTime.now();
}
