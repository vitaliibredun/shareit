package ru.practicum.shareit.comments.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "comments", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "text")
    private String text;
    @Column(name = "item_id")
    private Integer itemId;
    @Column(name = "author_id")
    private Integer authorId;
    @Column(name = "created")
    private LocalDateTime created = LocalDateTime.now();
}
