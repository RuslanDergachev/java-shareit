package ru.practicum.shareIt.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareIt.booking.entity.BookingDto;
import ru.practicum.shareIt.booking.entity.BookingUpdateDto;
import ru.practicum.shareIt.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@Validated
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Valid @RequestBody BookingDto bookingDto) {
        log.debug("Добавлен запрос на бронирование: {}", bookingDto);
        return bookingService.addNewBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingUpdateDto update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId,
                                   @RequestParam String approved) {
        log.debug("Обновлено бронирование: {}", bookingId);
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingUpdateDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        log.debug("Получен запрос бронирования вещи ID {}", bookingId);
        return bookingService.getBookingByIdAndBookerId(userId, bookingId);
    }

    @GetMapping()
    public List<BookingUpdateDto> get(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestParam(value = "state", required = false) String state,
                                      @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                      @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {
        log.debug("Получен запрос бронирований пользователя {}", userId);
        return bookingService.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingUpdateDto> getBookingByIdByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @RequestParam(value = "state", required = false) String state,
                                  @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                  @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {
        log.debug("Получен запрос бронирования вещи пользователя {}", userId);
        return bookingService.getBookingByIdByOwner(userId, state, from, size);
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long bookingId) {
        log.debug("Получен запрос на удаление бронирования ID {}", bookingId);
        bookingService.deleteBooking(userId, bookingId);
    }
}
