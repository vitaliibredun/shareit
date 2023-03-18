package ru.practicum.shareit.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Integer id;
    @NotEmpty(message = "The text's comment is empty")
    private String text;
    private String authorName;
    @Builder.Default
    private LocalDateTime created = LocalDateTime.now();
}
