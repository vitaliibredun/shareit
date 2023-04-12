package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.request.controller.ItemRequestController;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestInfo;
import ru.practicum.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @MockBean
    private RequestService service;
    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    private ItemRequestDto requestDto;
    private ItemRequestInfo requestInfo1;
    private ItemRequestInfo requestInfo2;
    private ItemRequestInfo requestInfo3;

    @BeforeEach
    void setUp() {
        requestDto = makeRequestDto("I want a hammer");

        requestInfo1 = makeRequestInfo("I want a hammer");
        requestInfo2 = makeRequestInfo("I want something");
        requestInfo3 = makeRequestInfo("I want a screw driver");
    }

    @Test
    void createItemRequest() throws Exception {
        when(service.createItemRequest(anyInt(), any(ItemRequestDto.class)))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(requestDto.getCreated().toString())));
    }

    @Test
    void findAllItemRequestsByUser() throws Exception {
        Integer expectedSize = 3;

        when(service.findAllItemRequestsByUser(anyInt()))
                .thenReturn(List.of(requestInfo1, requestInfo2, requestInfo3));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("size()", is(expectedSize)))
                .andExpect(jsonPath("$.[0].description", is(requestInfo1.getDescription())))
                .andExpect(jsonPath("$.[0].created", is(requestInfo1.getCreated())))
                .andExpect(jsonPath("$.[1].description", is(requestInfo2.getDescription())))
                .andExpect(jsonPath("$.[1].created", is(requestInfo2.getCreated())))
                .andExpect(jsonPath("$.[2].description", is(requestInfo3.getDescription())))
                .andExpect(jsonPath("$.[2].created", is(requestInfo3.getCreated())));
    }

    @Test
    void findAllItemRequests() throws Exception {
        Integer expectedSize = 3;

        when(service.findAllItemRequests(anyInt(), any(Pageable.class)))
                .thenReturn(List.of(requestInfo1, requestInfo2, requestInfo3));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("size()", is(expectedSize)))
                .andExpect(jsonPath("$.[0].description", is(requestInfo1.getDescription())))
                .andExpect(jsonPath("$.[0].created", is(requestInfo1.getCreated())))
                .andExpect(jsonPath("$.[1].description", is(requestInfo2.getDescription())))
                .andExpect(jsonPath("$.[1].created", is(requestInfo2.getCreated())))
                .andExpect(jsonPath("$.[2].description", is(requestInfo3.getDescription())))
                .andExpect(jsonPath("$.[2].created", is(requestInfo3.getCreated())));
    }

    @Test
    void findItemRequest() throws Exception {
        when(service.findItemRequest(anyInt(), any()))
                .thenReturn(requestInfo1);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(requestInfo1.getDescription())))
                .andExpect(jsonPath("$.created", is(requestInfo1.getCreated())));
    }

    private ItemRequestInfo makeRequestInfo(String description) {
        ItemRequestInfo.ItemRequestInfoBuilder builder = ItemRequestInfo.builder();

        builder.description(description);

        return builder.build();
    }

    private ItemRequestDto makeRequestDto(String description) {
        ItemRequestDto.ItemRequestDtoBuilder builder = ItemRequestDto.builder();

        builder.description(description);

        return builder.build();
    }
}
