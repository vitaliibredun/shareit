package ru.practicum.shareit.comments.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.user.model.User;

@Mapper
public interface CommentMapper {
    default CommentDto toDto(Comment comment, User user) {
        if (comment == null || user == null) {
            return null;
        }

        CommentDto.CommentDtoBuilder commentDtoBuilder = CommentDto.builder();

        commentDtoBuilder.id(comment.getId());
        commentDtoBuilder.text(comment.getText());
        commentDtoBuilder.authorName(user.getName());
        commentDtoBuilder.created(comment.getCreated());

        return commentDtoBuilder.build();
    }

    default Comment toModel(CommentDto commentDto) {
        if (commentDto == null) {
            return null;
        }

        Comment.CommentBuilder commentBuilder = Comment.builder();

        commentBuilder.id(commentDto.getId());
        commentBuilder.text(commentDto.getText());
        commentBuilder.created(commentDto.getCreated());

        return commentBuilder.build();
    }
}
