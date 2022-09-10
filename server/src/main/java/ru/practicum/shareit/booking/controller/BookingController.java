package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.entity.BookingDto;
import ru.practicum.shareit.booking.entity.BookingUpdateDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.util.Util.USER_HEADER;


@Slf4j
@Validated
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto add(@RequestHeader(USER_HEADER) long userId,
                          @Valid @RequestBody BookingDto bookingDto) {
        log.info("Добавлен запрос на бронирование: {}", bookingDto);
        return bookingService.addNewBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingUpdateDto update(@RequestHeader(USER_HEADER) long userId, @PathVariable long bookingId,
                                   @RequestParam String approved) {
        log.info("Обновлено бронирование: {}", bookingId);
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingUpdateDto getBookingById(@RequestHeader(USER_HEADER) long userId, @PathVariable long bookingId) {
        log.info("Получен запрос бронирования вещи ID {}", bookingId);
        return bookingService.getBookingByIdAndBookerId(userId, bookingId);
    }

    @GetMapping()
    public List<BookingUpdateDto> get(@RequestHeader(USER_HEADER) long userId,
                                      @RequestParam(value = "state", required = false) String state,
                                      @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                      @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {
        log.info("Получен запрос бронирований пользователя {}", userId);
        return bookingService.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingUpdateDto> getBookingByIdByOwner(@RequestHeader(USER_HEADER) long userId,
                                     @RequestParam(value = "state", required = false) String state,
                                     @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                     @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {
        log.info("Получен запрос бронирования вещи пользователя {}", userId);
        return bookingService.getBookingByIdByOwner(userId, state, from, size);
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@RequestHeader(USER_HEADER) long userId,
                              @PathVariable long bookingId) {
        log.info("Получен запрос на удаление бронирования ID {}", bookingId);
        bookingService.deleteBooking(userId, bookingId);
    }
}
