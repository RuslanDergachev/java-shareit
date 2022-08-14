package ru.practicum.shareIt.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareIt.exception.NotFoundException;
import ru.practicum.shareIt.exception.ValidationException;
import ru.practicum.shareIt.item.ItemDto;
import ru.practicum.shareIt.item.ItemMapper;
import ru.practicum.shareIt.item.ItemService;
import ru.practicum.shareIt.user.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private BookingRepository bookingRepository;
    private ItemService itemService;
    private UserService userService;

    public BookingServiceImpl(BookingRepository bookingRepository, ItemService itemService,
                              UserService userService) {
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public BookingDto addNewBooking(long userId, BookingDto bookingDto) {
        if (userService.getUser(userId) == null) {
            log.info("Пользователя {} не существует", userId);
            throw new NotFoundException("Пользователя ID " + userId + " не существует");
        }
        if (itemService.getItemById(userId, bookingDto.getItemId()) == null) {
            log.info("Вещь {} не найдена", bookingDto.getItemId());
            throw new NotFoundException("Вещь ID " + bookingDto.getItemId() + " не найдена");
        }
        Booking booking = BookingMapper.toBooking(bookingDto);
        ItemDto itemDto = itemService.getItemById(userId, bookingDto.getItemId());
        booking.setItem(ItemMapper.toItem(itemDto.getOwnerId(), itemDto));
        booking.setBookerId(userId);
        booking.setStatus(BookingStatus.WAITING);

        if (booking.getStart().toLocalDate().isBefore(LocalDate.now())) {
            log.info("День начала бронирования не может быть ранее текущей даты");
            throw new ValidationException("День начала бронирования не может быть ранее текущей даты");
        }
        if (!booking.getItem().getAvailable()) {
            log.info("Вещь {} недоступна для бронирования", booking.getItem());
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        if (booking.getItem().getOwnerId() == userId) {
            log.info("Пользователь не может забронировать свою вещь");
            throw new NotFoundException("Пользователь не может забронировать свою вещь");
        }
        return BookingMapper.toBookingDto(bookingRepository.saveAndFlush(booking));
    }

    @Override
    @Transactional
    public BookingUpdateDto updateBooking(long userId, long bookingId, String approved) {
        Booking booking = bookingRepository.getBookingById(bookingId);
        if (userId != booking.getItem().getOwnerId()) {
            throw new NotFoundException("Пользователь не является собственником вещи");
        }
        if (userService.getUser(userId) == null) {
            log.info("Пользователя {} не существует", userId);
            throw new NotFoundException("Пользователя ID " + userId + " не существует");
        }
        if (bookingRepository.getBookingById(bookingId) == null) {
            log.info("Бронь {} не существует", bookingId);
            throw new NotFoundException("Бронь ID " + bookingId + " не существует");
        }
        if (booking.getItem().getOwnerId() != userId) {
            throw new NotFoundException("Пользователь не является собственником вещи или автором бронирования");
        }
        if (approved.equals("true") & booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Статус " + BookingStatus.APPROVED + " уже установлен ранее");
        }
        if (approved.equals("true")) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        BookingUpdateDto bookingUpdateDto = BookingMapper.bookingDtoForUpdate(bookingRepository.save(booking));
        bookingUpdateDto.getBooker().setId(booking.getBookerId());
        return bookingUpdateDto;
    }

    @Override
    public BookingUpdateDto getBookingById(long userId, long bookingId) {
        if (userService.getUser(userId) == null) {
            log.info("Пользователя {} не существует", userId);
            throw new NotFoundException("Пользователя ID " + userId + " не существует");
        }
        if (bookingRepository.getBookingById(bookingId) == null) {
            log.info("Бронь {} не существует", bookingId);
            throw new NotFoundException("Бронь ID " + bookingId + " не существует");
        }
        return BookingMapper.bookingDtoForUpdate(bookingRepository.getBookingById(bookingId));
    }

    @Override
    public BookingUpdateDto getBookingByIdAndBookerId(long userId, long bookingId) {
        if (userService.getUser(userId) == null) {
            log.info("Пользователя {} не существует", userId);
            throw new NotFoundException("Пользователя ID " + userId + " не существует");
        }
        if (bookingRepository.getBookingById(bookingId) == null) {
            log.info("Бронь {} не существует", bookingId);
            throw new NotFoundException("Бронь ID " + bookingId + " не существует");
        }
        BookingUpdateDto bookingUpdateDto = BookingMapper.bookingDtoForUpdate(bookingRepository
                .getBookingById(bookingId));
        if (bookingUpdateDto.getBooker().getId() == userId || bookingUpdateDto.getItem().getOwnerId() == userId) {
            return bookingUpdateDto;
        }
        log.info("Пользователь {} не является владельцем брони {}", userId, bookingId);
        throw new NotFoundException("Пользователь не является владельцем брони");
    }

    @Override
    public List<BookingUpdateDto> getBookings(long userId, String state) {
        if (userService.getUser(userId) == null) {
            log.info("Пользователя {} не существует", userId);
            throw new NotFoundException("Пользователя ID " + userId + " не существует");
        }

        if (state.equals("CURRENT")) {
            return bookingRepository.getAllByBookerId(userId).stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .filter(b -> b.getStatus().equals(BookingStatus.REJECTED))
                    .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                    .filter(b -> b.getEnd().isAfter(LocalDateTime.now()))
                    .sorted(comparing(BookingUpdateDto::getId).reversed())
                    .collect(Collectors.toList());
        }
        if (state.equals("WAITING")) {
            return bookingRepository.getAllByBookerId(userId).stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .filter(b -> b.getStatus().equals(BookingStatus.WAITING))
                    .sorted(comparing(BookingUpdateDto::getId).reversed())
                    .collect(Collectors.toList());
        }
        if (state.equals("REJECTED")) {
            return bookingRepository.getAllByBookerId(userId).stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .filter(b -> b.getStatus().equals(BookingStatus.REJECTED))
                    .sorted(comparing(BookingUpdateDto::getId).reversed())
                    .collect(Collectors.toList());
        }
        if (state.equals("PAST")) {
            return bookingRepository.getAllByBookerId(userId).stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .filter(b -> b.getStatus().equals(BookingStatus.APPROVED))
                    .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                    .sorted(comparing(BookingUpdateDto::getId).reversed())
                    .collect(Collectors.toList());
        }
        if (state.equals("FUTURE")) {
            return bookingRepository.getAllByBookerId(userId).stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .filter(b -> b.getStatus().equals(BookingStatus.WAITING) ||
                            b.getStatus().equals(BookingStatus.APPROVED))
                    .sorted(comparing(BookingUpdateDto::getId).reversed())
                    .collect(Collectors.toList());
        }
        if (state.equals("ALL")) {
            return bookingRepository.getAllByBookerId(userId).stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .filter(b -> !b.getStatus().equals(BookingStatus.REJECTED))
                    .sorted(comparing(BookingUpdateDto::getId).reversed())
                    .collect(Collectors.toList());
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public List<BookingUpdateDto> getBookingByIdByOwner(long userId, String state) {
        if (userService.getUser(userId) == null) {
            log.info("Пользователя {} не существует", userId);
            throw new NotFoundException("Пользователя ID " + userId + " не существует");
        }
        if (state.equals("CURRENT")) {
            return bookingRepository.getBookingsByOwnerId(userId).stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .filter(b -> b.getStatus().equals(BookingStatus.REJECTED))
                    .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                    .filter(b -> b.getEnd().isAfter(LocalDateTime.now()))
                    .sorted(comparing(BookingUpdateDto::getId).reversed())
                    .collect(Collectors.toList());
        }
        if (state.equals("WAITING")) {
            return bookingRepository.getBookingsByOwnerId(userId).stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .filter(b -> b.getStatus().equals(BookingStatus.WAITING))
                    .sorted(comparing(BookingUpdateDto::getId).reversed())
                    .collect(Collectors.toList());
        }
        if (state.equals("REJECTED")) {
            return bookingRepository.getBookingsByOwnerId(userId).stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .filter(b -> b.getStatus().equals(BookingStatus.REJECTED))
                    .sorted(comparing(BookingUpdateDto::getId).reversed())
                    .collect(Collectors.toList());
        }
        if (state.equals("PAST")) {
            return bookingRepository.getBookingsByOwnerId(userId).stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .filter(b -> b.getStatus().equals(BookingStatus.APPROVED))
                    .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                    .sorted(comparing(BookingUpdateDto::getId).reversed())
                    .collect(Collectors.toList());
        }
        if (state.equals("FUTURE")) {
            return bookingRepository.getBookingsByOwnerId(userId).stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .filter(b -> !b.getStatus().equals(BookingStatus.REJECTED) ||
                            b.getStatus().equals(BookingStatus.APPROVED))
                    .sorted(comparing(BookingUpdateDto::getId).reversed())
                    .collect(Collectors.toList());
        }
        if (state.equals("ALL")) {
            return bookingRepository.getBookingsByOwnerId(userId).stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .filter(b -> !b.getStatus().equals(BookingStatus.REJECTED))
                    .sorted(comparing(BookingUpdateDto::getId).reversed())
                    .collect(Collectors.toList());
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public void deleteBooking(long userId, long bookingId) {
        bookingRepository.deleteBookingById(bookingId);
    }
}
