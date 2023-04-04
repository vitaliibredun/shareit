package ru.practicum.shareit.comments.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> commentDtoTester;
    @Autowired
    private JacksonTester<CommentInfo> commentInfoTester;

    @Test
    void verifyCreateCommentDto() throws IOException {
        CommentDto commentDto = makeCommentDto(1, "Thanks a lot", "Kent");

        JsonContent<CommentDto> result = commentDtoTester.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Thanks a lot");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Kent");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-04-01T12:30:00");
    }

    @Test
    void verifyCreateCommentInfo() throws IOException {
        CommentInfo commentInfo = makeCommentInfo(1, "Thanks a lot", "Kent");

        JsonContent<CommentInfo> result = commentInfoTester.write(commentInfo);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Thanks a lot");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Kent");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-04-01T12:30:00");
    }

    private CommentInfo makeCommentInfo(Integer id, String text, String authorName) {
        CommentInfo.CommentInfoBuilder builder = CommentInfo.builder();

        builder.id(id);
        builder.text(text);
        builder.authorName(authorName);
        builder.created(LocalDateTime.of(2023, 4, 1, 12, 30));

        return builder.build();
    }

    private CommentDto makeCommentDto(Integer id, String text, String authorName) {
        CommentDto.CommentDtoBuilder builder = CommentDto.builder();

        builder.id(id);
        builder.text(text);
        builder.authorName(authorName);
        builder.created(LocalDateTime.of(2023, 4, 1, 12, 30));

        return builder.build();
    }
}