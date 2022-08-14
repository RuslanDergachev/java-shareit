package ru.practicum.shareIt.booking;

import ru.practicum.shareIt.user.User;

public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                null,
                null,
                null
        );
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .start(booking.getStart())
                .end(booking.getEnd()).build();
    }

    public static BookingUpdateDto bookingDtoForUpdate(Booking booking) {
        User booker = new User();
        booker.setId(booking.getBookerId());
        return BookingUpdateDto.builder()
                .id(booking.getId())
                .status(booking.getStatus())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(booker)
                .item(booking.getItem()).build();
    }

    public static LastBooking toBookingBefore(Booking booking) {
        return LastBooking.builder()
                .id(booking.getId())
                .bookerId(booking.getBookerId())
                .build();
    }

    public static NextBooking toBookingAfter(Booking booking) {
        return NextBooking.builder()
                .id(booking.getId())
                .bookerId(booking.getBookerId())
                .build();
    }
}
