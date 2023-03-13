package ru.practicum.shareit.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentInfo {
    private Integer id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
