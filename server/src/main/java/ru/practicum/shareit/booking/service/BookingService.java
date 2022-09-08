package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.entity.BookingDto;
import ru.practicum.shareit.booking.entity.BookingUpdateDto;

import java.util.List;


public interface BookingService {

    BookingDto addNewBooking(long userId, BookingDto bookingDto);

    BookingUpdateDto updateBooking(long userId, long bookingId, String approved);

    BookingUpdateDto getBookingById(long userId, long bookingId);

    BookingUpdateDto getBookingByIdAndBookerId(long userId, long bookingId);

    List<BookingUpdateDto> getBookings(long userId, String state, int from, int size);

    List<BookingUpdateDto> getBookingByIdByOwner(long userId, String state, int from, int size);

    void deleteBooking(long userId, long bookingId);

}
