package ru.practicum.shareIt.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareIt.booking.entity.BookingDto;
import ru.practicum.shareIt.booking.entity.BookingStatus;
import ru.practicum.shareIt.booking.entity.BookingUpdateDto;
import ru.practicum.shareIt.booking.service.BookingService;
import ru.practicum.shareIt.item.entity.Item;
import ru.practicum.shareIt.user.entity.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @MockBean
    private BookingService mockBookingService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .status(BookingStatus.WAITING)
            .bookerId(1L)
            .itemId(1L)
            .itemName("Дрель электрическая")
            .start(LocalDateTime.of(2022, 8, 1, 12, 30, 30))
            .end(LocalDateTime.of(2022, 8, 2, 12, 30, 30))
            .build();

    private final BookingDto bookingDto1 = BookingDto.builder()
            .bookerId(1L)
            .itemId(1L)
            .start(LocalDateTime.of(2022, 8, 1, 12, 30, 30))
            .end(LocalDateTime.of(2022, 8, 2, 12, 30, 30))
            .build();

    private final Item item = new Item(1L, "перфоратор", "перфоратор электрический"
            , true, 1L, 1L);
    private final BookingUpdateDto bookingUpdateDto = BookingUpdateDto.builder()
            .id(1L)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.of(2022, 8, 1, 12, 30, 30))
            .end(LocalDateTime.of(2022, 8, 2, 12, 30, 30))
            .booker(new User(1L, "Ivan", "user@email.ru"))
            .item(item)
            .build();

    @Test
    void shouldReturnNewBookingTest() throws Exception {
        when(mockBookingService.addNewBooking(1L, bookingDto1))
                .thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())))
                .andExpect(jsonPath("$.bookerId", is(bookingDto.getBookerId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.itemName", is(bookingDto.getItemName())))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())));
    }

    @Test
    void shouldReturnUpdateBookingTest() throws Exception {
        String approved = "true";
        when(mockBookingService.updateBooking(1L, 1L, approved))
                .thenReturn(bookingUpdateDto);
        mvc.perform(patch("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(bookingUpdateDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingUpdateDto.getStatus().name())))
                .andExpect(jsonPath("$.start", is(bookingUpdateDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingUpdateDto.getEnd().toString())))
                .andExpect(jsonPath("$.booker", is(bookingUpdateDto.getBooker()), User.class))
                .andExpect(jsonPath("$.item", is(bookingUpdateDto.getItem()), Item.class));
    }

    @Test
    void shouldReturnBookingByIdTest() throws Exception {
        when(mockBookingService.getBookingByIdAndBookerId(1L, 1L))
                .thenReturn(bookingUpdateDto);
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(bookingUpdateDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingUpdateDto.getStatus().name())))
                .andExpect(jsonPath("$.start", is(bookingUpdateDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingUpdateDto.getEnd().toString())))
                .andExpect(jsonPath("$.booker", is(bookingUpdateDto.getBooker()), User.class))
                .andExpect(jsonPath("$.item", is(bookingUpdateDto.getItem()), Item.class));
    }

    @Test
    void shouldReturnBookingsListTest() throws Exception {
        String state = "ALL";
        when(mockBookingService.getBookings(1L, state, 0, 1))
                .thenReturn(List.of(bookingUpdateDto));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("APPROVED")));
    }

    @Test
    void shouldReturnBookingByIdByOwnerTest() throws Exception {
        String state = "ALL";
        when(mockBookingService.getBookingByIdByOwner(1L, state, 0, 1))
                .thenReturn(List.of(bookingUpdateDto));
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("APPROVED")));
    }

    @Test
    void shouldDeleteBookingTest() throws Exception {
        mvc.perform(delete("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("bookingId", "1"))
                .andExpect(status().isOk());
    }
}