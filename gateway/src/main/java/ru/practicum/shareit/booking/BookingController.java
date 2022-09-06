package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

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
		log.debug("Обновлено бронирование: {}", bookingId);
		return bookingClient.updateBooking(userId, bookingId, approved);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingByIdByOwner(@RequestHeader(USER_HEADER) long userId,
					@RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
					@PositiveOrZero @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
					@Positive @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {
		log.debug("Получен запрос бронирования вещи пользователя {}", userId);
		return bookingClient.getBookingByIdByOwner(userId, state, from, size);
	}

	@DeleteMapping("/{bookingId}")
	public void deleteBooking(@RequestHeader(USER_HEADER) long userId,
							  @PathVariable long bookingId) {
		log.debug("Получен запрос на удаление бронирования ID {}", bookingId);
		bookingClient.deleteBooking(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader(USER_HEADER) long userId,
			@RequestParam(name = "state", defaultValue = "ALL") String state,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

		log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader(USER_HEADER) long userId,
			@RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}}
