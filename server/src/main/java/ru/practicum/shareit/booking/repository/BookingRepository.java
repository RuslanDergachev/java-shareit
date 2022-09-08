package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.entity.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking getBookingById(Long bookingId);

    Page<Booking> getAllByBookerId(Long userId, Pageable pageable);

    @Transactional(readOnly = true)
    @Query(nativeQuery = true, value = "select * from bookings where item_id =:itemId")
    List<Booking> getBookingsByItemById(Long itemId);

    void deleteBookingById(Long bookingId);

    @Query("select b from Booking b where b.item.ownerId = ?1")
    Page<Booking> getBookingsByOwnerId(Long userId, Pageable pageable);
}
