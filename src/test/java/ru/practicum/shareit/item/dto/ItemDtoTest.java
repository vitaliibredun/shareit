package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.LastBooking;
import ru.practicum.shareit.booking.dto.NextBooking;
import ru.practicum.shareit.comments.dto.CommentInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> itemDtoTester;
    @Autowired
    private JacksonTester<ItemInfo> itemInfoTester;

    @Test
    void verifyCreateItemDto() throws IOException {
        ItemDto itemDto = makeItemDto(1, "Item1", "For something", true, 1);

        JsonContent<ItemDto> result = itemDtoTester.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Item1");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("For something");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    void verifyCreateItemInfo() throws IOException {
        ArrayList<CommentInfo> comments = new ArrayList<>();
        LastBooking lastBooking = LastBooking.builder().build();
        NextBooking nextBooking = NextBooking.builder().build();

        ItemInfo itemInfo = makeItemInfo(1,
                "Item1", "For something", false, comments, lastBooking, nextBooking);

        JsonContent<ItemInfo> result = itemInfoTester.write(itemInfo);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Item1");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("For something");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(false);
        assertThat(result).extractingJsonPathArrayValue("$.comments").isEqualTo(comments);
        assertThat(result).extractingJsonPathValue("$.lastBooking.id").isEqualTo(lastBooking.getId());
        assertThat(result).extractingJsonPathValue("$.nextBooking.id").isEqualTo(nextBooking.getId());
    }

    private ItemInfo makeItemInfo(Integer id,
                                  String name,
                                  String description,
                                  Boolean available,
                                  List<CommentInfo> comments,
                                  LastBooking lastBooking,
                                  NextBooking nextBooking) {

        ItemInfo.ItemInfoBuilder builder = ItemInfo.builder();

        builder.id(id);
        builder.name(name);
        builder.description(description);
        builder.available(available);
        builder.comments(comments);
        builder.lastBooking(lastBooking);
        builder.nextBooking(nextBooking);

        return builder.build();
    }

    private ItemDto makeItemDto(Integer id, String name, String description, Boolean available, Integer requestId) {
        ItemDto.ItemDtoBuilder builder = ItemDto.builder();

        builder.id(id);
        builder.name(name);
        builder.description(description);
        builder.available(available);
        builder.requestId(requestId);

        return builder.build();
    }
}
