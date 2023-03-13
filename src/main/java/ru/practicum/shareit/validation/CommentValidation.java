package ru.practicum.shareit.validation;

import ru.practicum.shareit.comments.dto.CommentDto;

public interface CommentValidation {
    void checkUser(Integer userId, Integer itemId, CommentDto commentDto);
}
