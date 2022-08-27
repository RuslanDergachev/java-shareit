package ru.practicum.shareIt.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.data.domain.*;
import ru.practicum.shareIt.booking.entity.Booking;
import ru.practicum.shareIt.booking.entity.BookingDto;
import ru.practicum.shareIt.booking.entity.BookingStatus;
import ru.practicum.shareIt.booking.entity.BookingUpdateDto;
import ru.practicum.shareIt.booking.repository.BookingRepository;
import ru.practicum.shareIt.exception.FalseIdException;
import ru.practicum.shareIt.exception.NotFoundException;
import ru.practicum.shareIt.exception.ValidationException;
import ru.practicum.shareIt.item.entity.Item;
import ru.practicum.shareIt.item.entity.ItemDto;
import ru.practicum.shareIt.item.service.ItemService;
import ru.practicum.shareIt.user.entity.User;
import ru.practicum.shareIt.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository mockBookingRepository;
    @Mock
    private ItemService mockItemService;
    @Mock
    private UserService mockUserService;
    @Mock
    Logger log;
    @InjectMocks
    BookingServiceImpl bookingService;
    private Item item = Item.builder()
            .id(1L)
            .name("Перфоратор электрический")
            .description("Mocito")
            .available(true)
            .ownerId(2L)
            .requestId(1L)
            .build();
    private Booking booking = Booking.builder()
            .id(1L)
            .start(LocalDateTime.of(2022, 8, 1, 12, 10, 10))
            .end(LocalDateTime.of(2022, 8, 1, 13, 10, 10))
            .bookerId(1L)
            .item(item)
            .build();
    private BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .start(LocalDateTime.of(2022, 8, 28, 12, 10, 10))
            .end(LocalDateTime.of(2022, 8, 29, 12, 10, 10))
            .itemId(1L)
            .build();
    private ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .description("Mocito")
            .available(true)
            .ownerId(2L)
            .build();

    @Test
    void addNewBookingTest() {
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        Mockito
                .when(mockItemService.getItemById(1L, 1L))
                .thenReturn(itemDto);
        Mockito
                .when(mockBookingRepository.saveAndFlush(booking))
                .thenReturn(booking);
        BookingDto bookingDto1 = bookingService.addNewBooking(1L, bookingDto);

        assertEquals(bookingDto.getBookerId(), bookingDto1.getBookerId());
    }

    @Test
    void whenBookingIsNull_thanReturnException() {
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.addNewBooking(1L, null));

        assertEquals("Запрос на бронирование отсутствует", exception.getMessage());
    }

    @Test
    void whenItemForBookingNotFound_thanReturnException() {
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.addNewBooking(1L, bookingDto));

        assertEquals("Вещь ID 1 не найдена", exception.getMessage());
    }

    @Test
    void whenDataStartIsBeforeNow_thanReturnException() {
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 1, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 29, 12, 10, 10))
                .itemId(1L)
                .build();
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        Mockito
                .when(mockItemService.getItemById(1L, 1L))
                .thenReturn(itemDto);
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.addNewBooking(1L, bookingDto));

        assertEquals("День начала бронирования не может быть ранее текущей даты", exception.getMessage());
    }

    @Test
    void whenItemAvailableIsFalse_thanReturnException() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .description("Mocito")
                .available(false)
                .ownerId(2L)
                .build();
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        Mockito
                .when(mockItemService.getItemById(1L, 1L))
                .thenReturn(itemDto);
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.addNewBooking(1L, bookingDto));

        assertEquals("Вещь недоступна для бронирования", exception.getMessage());
    }

    @Test
    void whenItemOwnerIsUser_thanReturnException() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .description("Mocito")
                .available(true)
                .ownerId(1L)
                .build();
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        Mockito
                .when(mockItemService.getItemById(1L, 1L))
                .thenReturn(itemDto);
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.addNewBooking(1L, bookingDto));

        assertEquals("Пользователь не может забронировать свою вещь", exception.getMessage());
    }


    @Test
    void updateBookingTest() {
        Item item1 = Item.builder()
                .id(1L)
                .name("Перфоратор электрический")
                .description("Mocito")
                .available(true)
                .ownerId(2L)
                .requestId(1L)
                .build();
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 1, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 1, 13, 10, 10))
                .bookerId(1L)
                .status(BookingStatus.WAITING)
                .item(item1)
                .build();
        Booking booking2 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 1, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 1, 13, 10, 10))
                .bookerId(1L)
                .status(BookingStatus.APPROVED)
                .item(item1)
                .build();
        String approved = "true";
        Mockito
                .when(mockUserService.getUser(Mockito.eq(2L)))
                .thenReturn(new User(2L, "Ivan", "user@email.ru"));
        Mockito
                .when(mockBookingRepository.getBookingById(1L))
                .thenReturn(booking1);
        Mockito
                .when(mockBookingRepository.save(booking2))
                .thenReturn(booking2);
        BookingUpdateDto bookingUpdateDto = bookingService.updateBooking(2L, 1L, approved);
        assertEquals(1L, bookingUpdateDto.getItem().getId());
    }

    @Test
    void updateBookingStatusTest() {
        Item item1 = Item.builder()
                .id(1L)
                .name("Перфоратор электрический")
                .description("Mocito")
                .available(true)
                .ownerId(2L)
                .requestId(1L)
                .build();
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 1, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 1, 13, 10, 10))
                .bookerId(1L)
                .status(BookingStatus.WAITING)
                .item(item1)
                .build();
        Booking booking2 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 1, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 1, 13, 10, 10))
                .bookerId(1L)
                .status(BookingStatus.REJECTED)
                .item(item1)
                .build();
        String approved = "false";
        Mockito
                .when(mockUserService.getUser(Mockito.eq(2L)))
                .thenReturn(new User(2L, "Ivan", "user@email.ru"));
        Mockito
                .when(mockBookingRepository.getBookingById(1L))
                .thenReturn(booking1);
        Mockito
                .when(mockBookingRepository.save(booking2))
                .thenReturn(booking2);
        BookingUpdateDto bookingUpdateDto = bookingService.updateBooking(2L, 1L, approved);

        assertEquals("REJECTED", bookingUpdateDto.getStatus().name());
    }

    @Test
    void whenApprovedIsEmpty_thanReturnException() {
        String approved = "";
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        Mockito
                .when(mockBookingRepository.getBookingById(1L))
                .thenReturn(booking);
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.updateBooking(1L, 1L, approved));

        assertEquals("Статус бронирования отсутствует", exception.getMessage());
    }

    @Test
    void whenApprovedIsNull_thanReturnException() {
        String approved = null;
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        Mockito
                .when(mockBookingRepository.getBookingById(1L))
                .thenReturn(booking);
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.updateBooking(1L, 1L, approved));

        assertEquals("Статус бронирования отсутствует", exception.getMessage());
    }

    @Test
    void whenUserNotOwnerItem_thanReturnException() {
        String approved = "true";
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        Mockito
                .when(mockBookingRepository.getBookingById(1L))
                .thenReturn(booking);
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.updateBooking(1L, 1L, approved));

        assertEquals("Пользователь не является собственником вещи", exception.getMessage());
    }

    @Test
    void whenStatusApprovedIsSetBefore_thanReturnException() {
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 1, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 1, 13, 10, 10))
                .bookerId(1L)
                .status(BookingStatus.APPROVED)
                .item(item)
                .build();
        String approved = "true";
        Mockito
                .when(mockUserService.getUser(Mockito.eq(2L)))
                .thenReturn(new User(2L, "Ivan", "user@email.ru"));
        Mockito
                .when(mockBookingRepository.getBookingById(1L))
                .thenReturn(booking1);
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.updateBooking(2L, 1L, approved));

        assertEquals("Статус APPROVED уже установлен ранее", exception.getMessage());
    }

    @Test
    void getBookingByIdTest() {
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        Mockito
                .when(mockBookingRepository.getBookingById(1L))
                .thenReturn(booking);
        BookingUpdateDto bookingUpdateDto = bookingService.getBookingById(1L, 1L);
        assertEquals(1, bookingUpdateDto.getId());
    }

    @Test
    void getBookingByIdAndBookerIdTest() {
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        Mockito
                .when(mockBookingRepository.getBookingById(1L))
                .thenReturn(booking);
        BookingUpdateDto bookingUpdateDto = bookingService.getBookingByIdAndBookerId(1L, 1L);
        assertEquals(1, bookingUpdateDto.getId());
    }

    @Test
    void whenUserNotBooker_thanReturnException() {
        Mockito
                .when(mockUserService.getUser(Mockito.eq(3L)))
                .thenReturn(new User(3L, "Ivan", "user@email.ru"));
        Mockito
                .when(mockBookingRepository.getBookingById(1L))
                .thenReturn(booking);
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingByIdAndBookerId(3L, 1L));
        ;

        assertEquals("Пользователь не является владельцем брони", exception.getMessage());
    }

    @Test
    void getBookingsWithStateAllTest() {
        String state = "ALL";
        int from = 0;
        int size = 20;
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 1, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 1, 13, 10, 10))
                .bookerId(1L)
                .status(BookingStatus.WAITING)
                .item(item)
                .build();
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        List<Booking> bookingList = List.of(booking1);
        Pageable pageable = PageRequest.of(from, size, Sort.by("id").descending());
        Page<Booking> bookingPage = new PageImpl<>(bookingList, pageable, bookingList.size());
        Mockito
                .when(mockBookingRepository.getAllByBookerId(1L, pageable))
                .thenReturn(bookingPage);
        List<BookingUpdateDto> checkListBookingUpdateDto = bookingService.getBookings(1L, state, from, size);

        Assertions.assertEquals(1, checkListBookingUpdateDto.size());
        Assertions.assertEquals(1, checkListBookingUpdateDto.get(0).getId());

    }

    @Test
    void getBookingsWithStateCurrentTest() {
        String state = "CURRENT";
        int from = 0;
        int size = 20;
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 25, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 28, 13, 10, 10))
                .bookerId(1L)
                .status(BookingStatus.REJECTED)
                .item(item)
                .build();
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        List<Booking> bookingList = List.of(booking1);
        Pageable pageable = PageRequest.of(from, size, Sort.by("id").descending());
        Page<Booking> bookingPage = new PageImpl<>(bookingList, pageable, bookingList.size());
        Mockito
                .when(mockBookingRepository.getAllByBookerId(1L, pageable))
                .thenReturn(bookingPage);
        List<BookingUpdateDto> checkListBookingUpdateDto = bookingService.getBookings(1L, state, from, size);

        Assertions.assertEquals(1, checkListBookingUpdateDto.size());
        Assertions.assertEquals(1, checkListBookingUpdateDto.get(0).getId());

    }

    @Test
    void getBookingsWithStateWaitingTest() {
        String state = "WAITING";
        int from = 0;
        int size = 20;
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 25, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 28, 13, 10, 10))
                .bookerId(1L)
                .status(BookingStatus.WAITING)
                .item(item)
                .build();
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        List<Booking> bookingList = List.of(booking1);
        Pageable pageable = PageRequest.of(from, size, Sort.by("id").descending());
        Page<Booking> bookingPage = new PageImpl<>(bookingList, pageable, bookingList.size());
        Mockito
                .when(mockBookingRepository.getAllByBookerId(1L, pageable))
                .thenReturn(bookingPage);
        List<BookingUpdateDto> checkListBookingUpdateDto = bookingService.getBookings(1L, state, from, size);

        Assertions.assertEquals(1, checkListBookingUpdateDto.size());
        Assertions.assertEquals(1, checkListBookingUpdateDto.get(0).getId());
    }

    @Test
    void getBookingsWithStateRejectedTest() {
        String state = "REJECTED";
        int from = 0;
        int size = 20;
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 25, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 28, 13, 10, 10))
                .bookerId(1L)
                .status(BookingStatus.REJECTED)
                .item(item)
                .build();
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        List<Booking> bookingList = List.of(booking1);
        Pageable pageable = PageRequest.of(from, size, Sort.by("id").descending());
        Page<Booking> bookingPage = new PageImpl<>(bookingList, pageable, bookingList.size());
        Mockito
                .when(mockBookingRepository.getAllByBookerId(1L, pageable))
                .thenReturn(bookingPage);
        List<BookingUpdateDto> checkListBookingUpdateDto = bookingService.getBookings(1L, state, from, size);

        Assertions.assertEquals(1, checkListBookingUpdateDto.size());
        Assertions.assertEquals(1, checkListBookingUpdateDto.get(0).getId());
    }

    @Test
    void getBookingsWithStatePastTest() {
        String state = "PAST";
        int from = 0;
        int size = 20;
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 25, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 25, 13, 10, 10))
                .bookerId(1L)
                .status(BookingStatus.APPROVED)
                .item(item)
                .build();
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        List<Booking> bookingList = List.of(booking1);
        Pageable pageable = PageRequest.of(from, size, Sort.by("id").descending());
        Page<Booking> bookingPage = new PageImpl<>(bookingList, pageable, bookingList.size());
        Mockito
                .when(mockBookingRepository.getAllByBookerId(1L, pageable))
                .thenReturn(bookingPage);
        List<BookingUpdateDto> checkListBookingUpdateDto = bookingService.getBookings(1L, state, from, size);

        Assertions.assertEquals(1, checkListBookingUpdateDto.size());
        Assertions.assertEquals(1, checkListBookingUpdateDto.get(0).getId());
    }

    @Test
    void getBookingsWithStateFutureTest() {
        String state = "FUTURE";
        int from = 0;
        int size = 20;
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 25, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 25, 13, 10, 10))
                .bookerId(1L)
                .status(BookingStatus.APPROVED)
                .item(item)
                .build();
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        List<Booking> bookingList = List.of(booking1);
        Pageable pageable = PageRequest.of(from, size, Sort.by("id").descending());
        Page<Booking> bookingPage = new PageImpl<>(bookingList, pageable, bookingList.size());
        Mockito
                .when(mockBookingRepository.getAllByBookerId(1L, pageable))
                .thenReturn(bookingPage);
        List<BookingUpdateDto> checkListBookingUpdateDto = bookingService.getBookings(1L, state, from, size);

        Assertions.assertEquals(1, checkListBookingUpdateDto.size());
        Assertions.assertEquals(1, checkListBookingUpdateDto.get(0).getId());
    }

    @Test
    void getBookingsWithStateFutureAndStatusWaitingTest() {
        String state = "FUTURE";
        int from = 0;
        int size = 20;
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 25, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 25, 13, 10, 10))
                .bookerId(1L)
                .status(BookingStatus.WAITING)
                .item(item)
                .build();
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        List<Booking> bookingList = List.of(booking1);
        Pageable pageable = PageRequest.of(from, size, Sort.by("id").descending());
        Page<Booking> bookingPage = new PageImpl<>(bookingList, pageable, bookingList.size());
        Mockito
                .when(mockBookingRepository.getAllByBookerId(1L, pageable))
                .thenReturn(bookingPage);
        List<BookingUpdateDto> checkListBookingUpdateDto = bookingService.getBookings(1L, state, from, size);

        Assertions.assertEquals(1, checkListBookingUpdateDto.size());
        Assertions.assertEquals(1, checkListBookingUpdateDto.get(0).getId());
    }

    @Test
    void whenStateUnsupported_thanReturnException() {
        String state = "";
        int from = 0;
        int size = 20;
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 25, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 25, 13, 10, 10))
                .bookerId(1L)
                .status(BookingStatus.WAITING)
                .item(item)
                .build();
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        List<Booking> bookingList = List.of(booking1);
        Pageable pageable = PageRequest.of(from, size, Sort.by("id").descending());
        Page<Booking> bookingPage = new PageImpl<>(bookingList, pageable, bookingList.size());
        Mockito
                .when(mockBookingRepository.getAllByBookerId(1L, pageable))
                .thenReturn(bookingPage);
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.getBookings(1L, state, from, size));

        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void getBookingByIdByOwnerAndStatusIsCurrentTest() {
        String state = "CURRENT";
        int from = 0;
        int size = 20;
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 25, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 28, 13, 10, 10))
                .bookerId(1L)
                .status(BookingStatus.REJECTED)
                .item(item)
                .build();
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        List<Booking> bookingList = List.of(booking1);
        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));
        Page<Booking> bookingPage = new PageImpl<>(bookingList, pageable, bookingList.size());
        Mockito
                .when(mockBookingRepository.getBookingsByOwnerId(1L, pageable))
                .thenReturn(bookingPage);
        List<BookingUpdateDto> checkListBookingUpdateDto = bookingService.getBookingByIdByOwner(1L, state, from, size);

        Assertions.assertEquals(1, checkListBookingUpdateDto.size());
        Assertions.assertEquals(1, checkListBookingUpdateDto.get(0).getId());
    }

    @Test
    void getBookingByIdByOwnerAndStatusIsWaitingTest() {
        String state = "WAITING";
        int from = 0;
        int size = 20;
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 25, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 28, 13, 10, 10))
                .bookerId(1L)
                .status(BookingStatus.WAITING)
                .item(item)
                .build();
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        List<Booking> bookingList = List.of(booking1);
        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));
        Page<Booking> bookingPage = new PageImpl<>(bookingList, pageable, bookingList.size());
        Mockito
                .when(mockBookingRepository.getBookingsByOwnerId(1L, pageable))
                .thenReturn(bookingPage);
        List<BookingUpdateDto> checkListBookingUpdateDto = bookingService.getBookingByIdByOwner(1L, state, from, size);

        Assertions.assertEquals(1, checkListBookingUpdateDto.size());
        Assertions.assertEquals(1, checkListBookingUpdateDto.get(0).getId());
    }

    @Test
    void getBookingByIdByOwnerAndStatusIsRejectedTest() {
        String state = "REJECTED";
        int from = 0;
        int size = 20;
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 25, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 28, 13, 10, 10))
                .bookerId(1L)
                .status(BookingStatus.REJECTED)
                .item(item)
                .build();
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        List<Booking> bookingList = List.of(booking1);
        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));
        Page<Booking> bookingPage = new PageImpl<>(bookingList, pageable, bookingList.size());
        Mockito
                .when(mockBookingRepository.getBookingsByOwnerId(1L, pageable))
                .thenReturn(bookingPage);
        List<BookingUpdateDto> checkListBookingUpdateDto = bookingService.getBookingByIdByOwner(1L, state, from, size);

        Assertions.assertEquals(1, checkListBookingUpdateDto.size());
        Assertions.assertEquals(1, checkListBookingUpdateDto.get(0).getId());
    }

    @Test
    void getBookingByIdByOwnerAndStatusIsApprovedTest() {
        String state = "PAST";
        int from = 0;
        int size = 20;
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 25, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 25, 13, 10, 10))
                .bookerId(1L)
                .status(BookingStatus.APPROVED)
                .item(item)
                .build();
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        List<Booking> bookingList = List.of(booking1);
        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));
        Page<Booking> bookingPage = new PageImpl<>(bookingList, pageable, bookingList.size());
        Mockito
                .when(mockBookingRepository.getBookingsByOwnerId(1L, pageable))
                .thenReturn(bookingPage);
        List<BookingUpdateDto> checkListBookingUpdateDto = bookingService.getBookingByIdByOwner(1L, state, from, size);

        Assertions.assertEquals(1, checkListBookingUpdateDto.size());
        Assertions.assertEquals(1, checkListBookingUpdateDto.get(0).getId());
    }

    @Test
    void getBookingByIdByOwnerAndStatusIsFutureAndRejectedTest() {
        String state = "FUTURE";
        int from = 0;
        int size = 20;
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 25, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 25, 13, 10, 10))
                .bookerId(1L)
                .status(BookingStatus.WAITING)
                .item(item)
                .build();
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        List<Booking> bookingList = List.of(booking1);
        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));
        Page<Booking> bookingPage = new PageImpl<>(bookingList, pageable, bookingList.size());
        Mockito
                .when(mockBookingRepository.getBookingsByOwnerId(1L, pageable))
                .thenReturn(bookingPage);
        List<BookingUpdateDto> checkListBookingUpdateDto = bookingService.getBookingByIdByOwner(1L, state, from, size);

        Assertions.assertEquals(1, checkListBookingUpdateDto.size());
        Assertions.assertEquals(1, checkListBookingUpdateDto.get(0).getId());
    }

    @Test
    void getBookingByIdByOwnerAndStatusIsAllTest() {
        String state = "ALL";
        int from = 0;
        int size = 20;
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 25, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 25, 13, 10, 10))
                .bookerId(1L)
                .status(BookingStatus.WAITING)
                .item(item)
                .build();
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        List<Booking> bookingList = List.of(booking1);
        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));
        Page<Booking> bookingPage = new PageImpl<>(bookingList, pageable, bookingList.size());
        Mockito
                .when(mockBookingRepository.getBookingsByOwnerId(1L, pageable))
                .thenReturn(bookingPage);
        List<BookingUpdateDto> checkListBookingUpdateDto = bookingService.getBookingByIdByOwner(1L, state, from, size);

        Assertions.assertEquals(1, checkListBookingUpdateDto.size());
        Assertions.assertEquals(1, checkListBookingUpdateDto.get(0).getId());
    }

    @Test
    void whenBookingStateUnsupported_thanReturnException() {
        String state = "";
        int from = 0;
        int size = 20;

        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 25, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 25, 13, 10, 10))
                .bookerId(1L)
                .status(BookingStatus.WAITING)
                .item(item)
                .build();

        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));

        List<Booking> bookingList = List.of(booking1);
        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));
        Page<Booking> bookingPage = new PageImpl<>(bookingList, pageable, bookingList.size());

        Mockito
                .when(mockBookingRepository.getBookingsByOwnerId(1L, pageable))
                .thenReturn(bookingPage);

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.getBookingByIdByOwner(1L, state, from, size));

        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void deleteBookingTest() {
        bookingService.deleteBooking(1L, 1L);
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .deleteBookingById(1L);
    }

    @Test
    void validationByUserIdLessThenZero() {
        final FalseIdException exception = Assertions.assertThrows(
                FalseIdException.class,
                () -> bookingService.validationUserIdAndBookingId(-1L, 1L));
        assertEquals("ID меньше или равно 0", exception.getMessage());
    }

    @Test
    void validationByBookingIdLessThenZero() {
        final FalseIdException exception = Assertions.assertThrows(
                FalseIdException.class,
                () -> bookingService.validationUserIdAndBookingId(1L, -1L));
        assertEquals("ID меньше или равно 0", exception.getMessage());
    }

    @Test
    void whenUserNotFound_thenReturnException() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.validationUserIdAndBookingId(1L, 1L));
        assertEquals("Пользователя ID 1 не существует", exception.getMessage());
    }

    @Test
    void whenBookingNotFound_thenReturnException() {
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.validationUserIdAndBookingId(1L, 1L));
        assertEquals("Бронь ID 1 не существует", exception.getMessage());
    }

    @Test
    void whenPageFromLessZero_thenReturnException() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.validationPage(-1, 1));
        assertEquals("Параметр from не может быть меньше 0", exception.getMessage());
    }

    @Test
    void whenPageSizeLessZero_thenReturnException() {
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.validationPage(1, -1));
        assertEquals("Параметр size не может быть меньше или равен 0", exception.getMessage());
    }
}