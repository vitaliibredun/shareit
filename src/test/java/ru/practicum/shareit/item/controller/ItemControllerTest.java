package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfo;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @MockBean
    private ItemService service;
    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private ItemDto itemDto3;
    private ItemInfo itemInfo1;
    private ItemInfo itemInfo2;
    private ItemInfo itemInfo3;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto1 = makeItemDto("Item1", "For something", true);
        itemDto2 = makeItemDto("Item2", "For somebody", true);
        itemDto3 = makeItemDto("Item3", "It's a really good stuff", true);

        itemInfo1 = makeItemInfo("Item1", "For something", true);
        itemInfo2 = makeItemInfo("Item2", "For somebody", true);
        itemInfo3 = makeItemInfo("Item3", "It's a really good stuff", true);

        commentDto = makeCommentDto("It was great, thanks", "John");
    }

    @Test
    void createItem() throws Exception {
        when(service.createItem(anyInt(), any(ItemDto.class)))
                .thenReturn(itemDto1);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id",1)
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto1.getAvailable())));
    }

    @Test
    void updateItem() throws Exception {
        when(service.updateItem(anyInt(), anyInt(), any(ItemDto.class)))
                .thenReturn(itemDto1);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id",1)
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto1.getAvailable())));
    }

    @Test
    void findItem() throws Exception {
        when(service.findItem(anyInt(), anyInt()))
                .thenReturn(itemInfo1);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemInfo1.getName())))
                .andExpect(jsonPath("$.description", is(itemInfo1.getDescription())))
                .andExpect(jsonPath("$.available", is(itemInfo1.getAvailable())));
    }

    @Test
    void findAllItemsByUser() throws Exception {
        Integer expectedSize = 3;

        when(service.findAllItemsByUser(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(itemInfo1, itemInfo2, itemInfo3));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("size()", is(expectedSize)))
                .andExpect(jsonPath("$.[0].name", is(itemInfo1.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemInfo1.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemInfo1.getAvailable())))
                .andExpect(jsonPath("$.[1].name", is(itemInfo2.getName())))
                .andExpect(jsonPath("$.[1].description", is(itemInfo2.getDescription())))
                .andExpect(jsonPath("$.[1].available", is(itemInfo2.getAvailable())))
                .andExpect(jsonPath("$.[2].name", is(itemInfo3.getName())))
                .andExpect(jsonPath("$.[2].description", is(itemInfo3.getDescription())))
                .andExpect(jsonPath("$.[2].available", is(itemInfo3.getAvailable())));
    }

    @Test
    void searchItemForRent() throws Exception {
        Integer expectedSize = 1;

        when(service.searchItemForRent(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto1));

        mvc.perform(get("/items/search")
                        .param("text", "some item")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("size()", is(expectedSize)))
                .andExpect(jsonPath("$.[0].name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto1.getAvailable())));
    }

    @Test
    void addComment() throws Exception {
        when(service.addComment(anyInt(), anyInt(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .param("itemId", "1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().toString())));
    }

    private ItemInfo makeItemInfo(String name, String description, Boolean available) {
        ItemInfo.ItemInfoBuilder builder = ItemInfo.builder();

        builder.name(name);
        builder.description(description);
        builder.available(available);

        return builder.build();
    }

    private CommentDto makeCommentDto(String text, String authorName) {
        CommentDto.CommentDtoBuilder builder = CommentDto.builder();

        builder.text(text);
        builder.authorName(authorName);

        return builder.build();
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        ItemDto.ItemDtoBuilder builder = ItemDto.builder();

        builder.name(name);
        builder.description(description);
        builder.available(available);

        return builder.build();
    }
}
