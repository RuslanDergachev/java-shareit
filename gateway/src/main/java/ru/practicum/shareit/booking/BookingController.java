package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.validation.Validation;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Util.USER_HEADER;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@RequestHeader(USER_HEADER) long userId, @PathVariable long bookingId,
                                         @RequestParam Boolean approved) {
        if (approved == null) {
            throw new NotFoundException("Статус бронирования отсутствует");
        }
        Validation.validateId(userId);
        Validation.validateId(bookingId);
        log.debug("Обновлено бронирование: {}", bookingId);
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingByIdByOwner(@RequestHeader(USER_HEADER) long userId,
             @RequestParam(required = false, defaultValue = "ALL") String state,
             @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
             @Positive @RequestParam(required = false, defaultValue = "20") Integer size) {
        Validation.validateId(userId);
        log.debug("Получен запрос бронирования вещи пользователя {}", userId);
        return bookingClient.getBookingByIdByOwner(userId, state, from, size);
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@RequestHeader(USER_HEADER) long userId,
                              @PathVariable long bookingId) {
        Validation.validateId(userId);
        Validation.validateId(bookingId);
        log.debug("Получен запрос на удаление бронирования ID {}", bookingId);
        bookingClient.deleteBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(USER_HEADER) long userId,
                                  @RequestParam(defaultValue = "ALL") String state,
                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        Validation.validateId(userId);
        log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addNewBookingItem(@RequestHeader(USER_HEADER) long userId,
                                                    @RequestBody @Valid BookingDto bookingDto) {
        Validation.validateId(userId);
        if (bookingDto == null) {
            log.warn("Запрос на бронирование отсутствует");
            throw new NotFoundException("Запрос на бронирование отсутствует");
        }
        log.info("Creating booking {}, userId={}", bookingDto, userId);
        return bookingClient.newBookItem(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_HEADER) long userId,
                                             @PathVariable Long bookingId) {
        Validation.validateId(userId);
        Validation.validateId(bookingId);
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }
}
