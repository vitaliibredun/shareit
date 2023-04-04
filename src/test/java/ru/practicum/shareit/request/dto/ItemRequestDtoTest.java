package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestInfo> requestInfoTester;
    @Autowired
    private JacksonTester<ItemRequestDto> requestDtoTester;

    @Test
    void verifyCreateRequestInfo() throws IOException {
        LocalDateTime created = LocalDateTime.of(2034, 4, 1, 11, 30);
        ArrayList<ItemDto> items = new ArrayList<>();

        ItemRequestInfo requestInfo = makeRequestInfo(1, "I'm looking for a hammer", created, items);

        JsonContent<ItemRequestInfo> result = requestInfoTester.write(requestInfo);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("I'm looking for a hammer");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2034-04-01T11:30:00");
        assertThat(result).extractingJsonPathArrayValue("$.items").isEqualTo(items);
    }

    @Test
    void verifyCreateRequestDto() throws IOException {
        LocalDateTime created = LocalDateTime.of(2034, 4, 1, 11, 30);

        ItemRequestDto requestDto = makeRequestDto(1, "I'm looking for a hammer", created);

        JsonContent<ItemRequestDto> result = requestDtoTester.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("I'm looking for a hammer");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2034-04-01T11:30:00");
    }

    private ItemRequestDto makeRequestDto(Integer id, String description, LocalDateTime created) {
        ItemRequestDto.ItemRequestDtoBuilder builder = ItemRequestDto.builder();

        builder.id(id);
        builder.description(description);
        builder.created(created);

        return builder.build();
    }

    private ItemRequestInfo makeRequestInfo(Integer id, String description, LocalDateTime created, List<ItemDto> items) {
        ItemRequestInfo.ItemRequestInfoBuilder builder = ItemRequestInfo.builder();

        builder.id(id);
        builder.description(description);
        builder.created(created);
        builder.items(items);

        return builder.build();
    }
}
