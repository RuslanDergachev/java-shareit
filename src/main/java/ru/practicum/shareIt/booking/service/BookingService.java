package ru.practicum.shareIt.booking.service;

import ru.practicum.shareIt.booking.entity.BookingDto;
import ru.practicum.shareIt.booking.entity.BookingUpdateDto;

import java.util.List;

public interface BookingService {

    BookingDto addNewBooking(long userId, BookingDto bookingDto);

    BookingUpdateDto updateBooking(long userId, long bookingId, String approved);

    BookingUpdateDto getBookingById(long userId, long bookingId);

    BookingUpdateDto getBookingByIdAndBookerId(long userId, long bookingId);

    List<BookingUpdateDto> getBookings(long userId, String state);

    List<BookingUpdateDto> getBookingByIdByOwner(long userId, String state);

    void deleteBooking(long userId, long bookingId);

}
