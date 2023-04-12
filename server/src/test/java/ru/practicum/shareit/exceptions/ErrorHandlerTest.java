package ru.practicum.shareit.exceptions;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.exceptions.controller.ErrorHandler;
import ru.practicum.exceptions.model.ErrorResponse;



import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ErrorHandlerTest {
    private final ErrorHandler handler;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() throws Exception {
        mockMvc = webAppContextSetup(this.wac).build();
    }

    @Test
    void verifyControllerException() throws Exception {
        Integer userId = 1;

        mockMvc.perform(get("/users/all")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void verifyException() {
        Exception exception = new Exception("Exception");
        ErrorResponse errorResponse = handler.handleException(exception);
        assertThat(errorResponse, notNullValue());
        assertThat(errorResponse.getError(), is(exception.getMessage()));
    }
}