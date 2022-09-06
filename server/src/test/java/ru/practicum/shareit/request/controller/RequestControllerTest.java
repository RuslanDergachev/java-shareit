package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L,
            "перфоратор электрический",
            1L,
            null,
            null
    );
    private final ItemRequestDto itemRequestDto1 = new ItemRequestDto(
            2L,
            "дрель аккумуляторная",
            1L,
            null,
            null
    );
    private final List<ItemRequestDto> itemRequestDtoList = List.of(itemRequestDto, itemRequestDto1);

    @Test
    void shouldReturnNewItemRequest() throws Exception {
        when(itemRequestService.addNewItemRequest(anyLong(), any()))
                .thenReturn(itemRequestDto);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestor", is(itemRequestDto.getRequestor()), Long.class))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated()), LocalDateTime.class))
                .andExpect(jsonPath("$.items", is(itemRequestDto.getItems()), Item.class));
    }

    @Test
    void shouldReturnListItemRequestDtoByUserIdTest() throws Exception {
        when(itemRequestService.getItemRequests(1L))
                .thenReturn(itemRequestDtoList);
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].description", is("перфоратор электрический")));
    }

    @Test
    void shouldReturnAllItemRequestDtoWithPage() throws Exception {
        when(itemRequestService.getAllItemRequests(1L, 0, 1))
                .thenReturn(itemRequestDtoList);
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].description", is("перфоратор электрический")));
    }

    @Test
    void shouldReturnRequestById() throws Exception {
        when(itemRequestService.getRequestById(1L, 2L))
                .thenReturn(itemRequestDto1);
        mvc.perform(get("/requests/2")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.description", is("дрель аккумуляторная")));
    }
}