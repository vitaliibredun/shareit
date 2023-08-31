package ru.practicum.comments.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "authorName", source = "user.name")
    CommentDto toDto(Comment comment, User user);

    Comment toModel(CommentDto commentDto);
}
