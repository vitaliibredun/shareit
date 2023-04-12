package ru.practicum.comments.validation;

import ru.practicum.comments.dto.CommentDto;

public interface CommentValidation {
    void checkUser(Integer userId, Integer itemId, CommentDto commentDto);
}
