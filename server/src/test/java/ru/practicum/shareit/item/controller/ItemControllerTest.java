package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.entity.LastBooking;
import ru.practicum.shareit.booking.entity.NextBooking;
import ru.practicum.shareit.comments.entity.CommentDto;
import ru.practicum.shareit.comments.service.CommentService;
import ru.practicum.shareit.item.entity.ItemDto;
import ru.practicum.shareit.item.entity.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @MockBean
    private CommentService commentService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Дрель электрическая")
            .description("Mocito")
            .available(false)
            .ownerId(1L)
            .start(null)
            .end(null)
            .lastBooking(null)
            .nextBooking(null)
            .comments(null)
            .requestId(null)
            .build();

    private final ItemDto itemDto1 = ItemDto.builder()
            .id(1L)
            .name("Отвертка электрическая")
            .description("Mocito")
            .available(false)
            .ownerId(1L)
            .start(null)
            .end(null)
            .lastBooking(null)
            .nextBooking(null)
            .comments(null)
            .requestId(null)
            .build();

    private final List<ItemDto> itemDtoList = List.of(itemDto);
    private final CommentDto commentDto = new CommentDto(
            1L,
            "Какой-то комментарий",
            "Vasya",
            null
    );

    @Test
    void shouldReturnNewItemDtoTest() throws Exception {
        when(itemService.addNewItem(anyLong(), any()))
                .thenReturn(itemDto);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.lastBooking", is(itemDto.getLastBooking()), LastBooking.class))
                .andExpect(jsonPath("$.nextBooking", is(itemDto.getNextBooking()), NextBooking.class))
                .andExpect(jsonPath("$.comments", is(itemDto.getComments()), List.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void shouldReturnUpdateItemDto() throws Exception {
        when(itemService.updateItem(1L, ItemMapper.toItem(1L, itemDto)))
                .thenReturn(ItemMapper.toItem(1L, itemDto1));
        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto1.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.ownerId", is(itemDto1.getOwnerId()), Long.class));
    }

    @Test
    void shouldReturnItemDtoByIdTest() throws Exception {
        when(itemService.getItemById(1L, 1L))
                .thenReturn(itemDto);
        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId()), Long.class));
    }

    @Test
    void shouldReturnItemsDtoListTest() throws Exception {
        when(itemService.getItems(1L))
                .thenReturn(itemDtoList);
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is("Mocito")));
    }

    @Test
    void shouldReturnItemByTextTest() throws Exception {
        when(itemService.searchItem(1L, "Дрель", 0, 1))
                .thenReturn(itemDtoList);
        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "Дрель")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Дрель электрическая")));
    }

    @Test
    void shouldDeleteItemTest() throws Exception {
        mvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNewCommentTest() throws Exception {
        when(commentService.createComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);
        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("itemId", "1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }
}