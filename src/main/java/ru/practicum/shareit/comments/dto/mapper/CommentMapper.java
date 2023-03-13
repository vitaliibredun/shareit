package ru.practicum.shareit.comments.dto.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.user.model.User;

@Component
public class CommentMapper {
    public CommentDto toDto(Comment comment, User user) {
        return CommentDto
                .builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(comment.getAuthorId())
                .authorName(user.getName())
                .created(comment.getCreated())
                .build();
    }

    public Comment toModel(CommentDto commentDto) {
        return Comment
                .builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .authorId(commentDto.getAuthor())
                .created(commentDto.getCreated())
                .build();
    }
}
