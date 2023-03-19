package ru.practicum.shareit.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.comments.dto.CommentInfo;
import ru.practicum.shareit.comments.model.Comment;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comment, Integer> {

    @Query("select new ru.practicum.shareit.comments.dto.CommentInfo" +
            "(c.id, c.text, c.author.name, c.created) " +
            "from Comment c " +
            "where c.item.id = ?1")
    List<CommentInfo> findAllByItemId(Integer itemId);
}