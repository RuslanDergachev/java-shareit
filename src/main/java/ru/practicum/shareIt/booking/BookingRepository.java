package ru.practicum.shareIt.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking getBookingById(Long bookingId);

    List<Booking> getAllByBookerId(Long userId);

    @Transactional(readOnly = true)
    @Query(nativeQuery = true, value = "select * from bookings where item_id =:itemId")
    List<Booking> getBookingsByItemById(Long itemId);

    void deleteBookingById(Long bookingId);

    @Query("select b from Booking b where b.item.ownerId = ?1")
    List<Booking> getBookingsByOwnerId(Long userId);
}
