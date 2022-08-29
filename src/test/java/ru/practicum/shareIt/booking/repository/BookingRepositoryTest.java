package ru.practicum.shareIt.booking.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareIt.booking.entity.Booking;
import ru.practicum.shareIt.booking.entity.BookingStatus;
import ru.practicum.shareIt.item.entity.Item;
import ru.practicum.shareIt.item.repository.ItemRepository;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource(properties = {"db.name=testjpa"})
class BookingRepositoryTest {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private final Item item = Item.builder()
            .name("Дрель электрическая")
            .description("Mocito")
            .available(true)
            .ownerId(1L)
            .requestId(1L)
            .build();

    private final Booking booking = Booking.builder()
            .start(LocalDateTime.of(2022, 8, 25, 12, 10, 10))
            .end(LocalDateTime.of(2022, 8, 25, 13, 10, 10))
            .bookerId(1L)
            .status(BookingStatus.WAITING)
            .item(item)
            .build();

    @Test
    @Rollback
    void shouldReturnBookingsByItemByIdTest() {
        itemRepository.save(item);
        bookingRepository.save(booking);
        List<Booking> bookingList = bookingRepository.getBookingsByItemById(item.getId());

        assertEquals(booking.getId(), bookingList.get(0).getId());
        assertEquals(booking.getItem().getName(), bookingList.get(0).getItem().getName());
    }

    @Test
    @Rollback
    void shouldReturnBookingsByOwnerIdTest() {
        itemRepository.save(item);
        bookingRepository.save(booking);
        int from = 0;
        int size = 1;
        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));
        Page<Booking> bookingPage = bookingRepository.getBookingsByOwnerId(1L, pageable);
        List<Booking> bookingList = bookingPage.toList();

        assertEquals(booking.getId(), bookingList.get(0).getId());
        assertEquals(booking.getItem().getName(), bookingList.get(0).getItem().getName());
    }
}