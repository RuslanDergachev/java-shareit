package ru.practicum.shareIt.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareIt.exception.FalseIdException;
import ru.practicum.shareIt.exception.NotFoundException;

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
        if (userId <= 0) {
            log.debug("ID пользователя меньше или равно 0");
            throw new FalseIdException("ID меньше или равно 0");
        }
        if (bookingDto == null) {
            log.debug("Запрос на бронирование отсутствует");
            throw new NotFoundException("Запрос на бронирование отсутствует");
        }
        return bookingService.addNewBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingUpdateDto update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId,
                                   @RequestParam String approved) {
        log.debug("Обновлено бронирование: {}", bookingId);
        if (userId <= 0) {
            log.debug("ID пользователя меньше или равно 0");
            throw new FalseIdException("ID меньше или равно 0");
        }
        if (bookingId <= 0) {
            log.debug("ID бронирования меньше или равно 0");
            throw new FalseIdException("ID меньше или равно 0");
        }
        if (approved == null || approved.isEmpty()) {
            throw new NotFoundException("статус бронирования отсутствует");
        }
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingUpdateDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        log.debug("Получен запрос бронирования ID");
        if (userId <= 0) {
            log.debug("ID пользователя меньше или равно 0");
            throw new FalseIdException("ID меньше или равно 0");
        }
        if (bookingId <= 0) {
            log.debug("ID бронирования меньше или равно 0");
            throw new FalseIdException("ID меньше или равно 0");
        }
        return bookingService.getBookingByIdAndBookerId(userId, bookingId);
    }

    @GetMapping()
    public List<BookingUpdateDto> get(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestParam(value = "state", required = false) String state) {
        log.debug("Получен запрос cписка бронирований");
        if (userId <= 0) {
            log.debug("ID пользователя меньше или равно 0");
            throw new FalseIdException("ID меньше или равно 0");
        }
        if (state == null) {
            state = "ALL";
        }
        return bookingService.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingUpdateDto> getBookingByIdByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @RequestParam(value = "state", required = false) String state) {
        log.debug("Получен запрос бронирования ID");
        if (userId <= 0) {
            log.debug("ID пользователя меньше или равно 0");
            throw new FalseIdException("ID меньше или равно 0");
        }
        if (state == null) {
            state = "ALL";
        }
        return bookingService.getBookingByIdByOwner(userId, state);
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long bookingId) {
        log.debug("Получен запрос на удаление бронирования");
        bookingService.deleteBooking(userId, bookingId);
    }
}
