package ru.practicum.shareIt.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareIt.booking.entity.*;
import ru.practicum.shareIt.booking.repository.BookingRepository;
import ru.practicum.shareIt.exception.FalseIdException;
import ru.practicum.shareIt.exception.NotFoundException;
import ru.practicum.shareIt.exception.ValidationException;
import ru.practicum.shareIt.item.entity.ItemDto;
import ru.practicum.shareIt.item.entity.ItemMapper;
import ru.practicum.shareIt.item.service.ItemService;
import ru.practicum.shareIt.request.service.ItemRequestServiceImpl;
import ru.practicum.shareIt.user.service.UserService;

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
        validationUserId(userId);
        if (bookingDto == null) {
            log.debug("Запрос на бронирование отсутствует");
            throw new NotFoundException("Запрос на бронирование отсутствует");
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
        validationUserIdAndBookingId(userId, bookingId);
        Booking booking = bookingRepository.getBookingById(bookingId);
        if (approved == null || approved.isEmpty()) {
            throw new NotFoundException("Статус бронирования отсутствует");
        }
        if (userId != booking.getItem().getOwnerId()) {
            throw new NotFoundException("Пользователь не является собственником вещи");
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
        validationUserIdAndBookingId(userId, bookingId);
        return BookingMapper.bookingDtoForUpdate(bookingRepository.getBookingById(bookingId));
    }

    @Override
    public BookingUpdateDto getBookingByIdAndBookerId(long userId, long bookingId) {
        validationUserIdAndBookingId(userId, bookingId);
        BookingUpdateDto bookingUpdateDto = BookingMapper.bookingDtoForUpdate(bookingRepository
                .getBookingById(bookingId));
        if (bookingUpdateDto.getBooker().getId() == userId || bookingUpdateDto.getItem().getOwnerId() == userId) {
            return bookingUpdateDto;
        }
        log.info("Пользователь {} не является владельцем брони {}", userId, bookingId);
        throw new NotFoundException("Пользователь не является владельцем брони");
    }

    @Override
    public List<BookingUpdateDto> getBookings(long userId, String state, int from, int size) {
        validationUserId(userId);
        validationPage(from, size);
        if (state == null) {
            state = "ALL";
        }
        if (from > 0) {
            --from;
        }
        Pageable pageable = PageRequest.of(from, size, Sort.by("id").descending());
        Page<Booking> bookingsPageable = bookingRepository.getAllByBookerId(userId, pageable);
        if (state.equals("CURRENT")) {
            return bookingsPageable.stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .filter(b -> b.getStatus().equals(BookingStatus.REJECTED))
                    .filter(b -> b.getStart().isBefore(LocalDateTime.now().withNano(0)))
                    .filter(b -> b.getEnd().isAfter(LocalDateTime.now().withNano(0)))
                    .sorted(comparing(BookingUpdateDto::getId).reversed())
                    .collect(Collectors.toList());
        }
        if (state.equals("WAITING")) {
            return bookingsPageable.stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .filter(b -> b.getStatus().equals(BookingStatus.WAITING))
                    .sorted(comparing(BookingUpdateDto::getId).reversed())
                    .collect(Collectors.toList());
        }
        if (state.equals("REJECTED")) {
            return bookingsPageable.stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .filter(b -> b.getStatus().equals(BookingStatus.REJECTED))
                    .sorted(comparing(BookingUpdateDto::getId).reversed())
                    .collect(Collectors.toList());
        }
        if (state.equals("PAST")) {
            return bookingsPageable.stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .filter(b -> b.getStatus().equals(BookingStatus.APPROVED))
                    .filter(b -> b.getEnd().isBefore(LocalDateTime.now().withNano(0)))
                    .sorted(comparing(BookingUpdateDto::getId).reversed())
                    .collect(Collectors.toList());
        }
        if (state.equals("FUTURE")) {
            return bookingsPageable.stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .filter(b -> b.getStatus().equals(BookingStatus.WAITING) ||
                            b.getStatus().equals(BookingStatus.APPROVED))
                    .sorted(comparing(BookingUpdateDto::getId).reversed())
                    .collect(Collectors.toList());
        }
        if (state.equals("ALL")) {
            return bookingsPageable.stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .collect(Collectors.toList());
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public List<BookingUpdateDto> getBookingByIdByOwner(long userId, String state, int from, int size) {
        validationUserId(userId);
        validationPage(from, size);
        if (state == null) {
            state = "ALL";
        }
        if (from > 0) {
            --from;
        }
        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));
        Page<Booking> bookingsPageable = bookingRepository.getBookingsByOwnerId(userId, pageable);
        if (state.equals("CURRENT")) {
            return bookingsPageable.stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .filter(b -> b.getStatus().equals(BookingStatus.REJECTED))
                    .filter(b -> b.getStart().isBefore(LocalDateTime.now().withNano(0)))
                    .filter(b -> b.getEnd().isAfter(LocalDateTime.now().withNano(0)))
                    .sorted(comparing(BookingUpdateDto::getId).reversed())
                    .collect(Collectors.toList());
        }
        if (state.equals("WAITING")) {
            return bookingsPageable.stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .filter(b -> b.getStatus().equals(BookingStatus.WAITING))
                    .sorted(comparing(BookingUpdateDto::getId).reversed())
                    .collect(Collectors.toList());
        }
        if (state.equals("REJECTED")) {
            return bookingsPageable.stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .filter(b -> b.getStatus().equals(BookingStatus.REJECTED))
                    .sorted(comparing(BookingUpdateDto::getId).reversed())
                    .collect(Collectors.toList());
        }
        if (state.equals("PAST")) {
            return bookingsPageable.stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .filter(b -> b.getStatus().equals(BookingStatus.APPROVED))
                    .filter(b -> b.getEnd().isBefore(LocalDateTime.now().withNano(0)))
                    .sorted(comparing(BookingUpdateDto::getId).reversed())
                    .collect(Collectors.toList());
        }
        if (state.equals("FUTURE")) {
            return bookingsPageable.stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .filter(b -> !b.getStatus().equals(BookingStatus.REJECTED) ||
                            b.getStatus().equals(BookingStatus.APPROVED))
                    .sorted(comparing(BookingUpdateDto::getId).reversed())
                    .collect(Collectors.toList());
        }
        if (state.equals("ALL")) {
            return bookingsPageable.stream()
                    .map(BookingMapper::bookingDtoForUpdate)
                    .sorted(comparing(BookingUpdateDto::getId).reversed())
                    .collect(Collectors.toList());
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public void deleteBooking(long userId, long bookingId) {
        bookingRepository.deleteBookingById(bookingId);
    }

    public void validationUserIdAndBookingId(long userId, long bookingId) {
        if (userId <= 0) {
            log.debug("ID пользователя меньше или равно 0");
            throw new FalseIdException("ID меньше или равно 0");
        }
        if (bookingId <= 0) {
            log.debug("ID бронирования меньше или равно 0");
            throw new FalseIdException("ID меньше или равно 0");
        }
        if (userService.getUser(userId) == null) {
            log.info("Пользователя {} не существует", userId);
            throw new NotFoundException("Пользователя ID " + userId + " не существует");
        }
        if (bookingRepository.getBookingById(bookingId) == null) {
            log.info("Бронь {} не существует", bookingId);
            throw new NotFoundException("Бронь ID " + bookingId + " не существует");
        }
    }

    private void validationUserId(long userId) {
        ItemRequestServiceImpl.validationByUserId(userId, userService, log);
    }

    public void validationPage(int from, int size) {
        if (from < 0) {
            log.info("Параметр from не может быть меньше 0 и равен {}", from);
            throw new ValidationException("Параметр from не может быть меньше 0");
        }
        if (size <= 0) {
            log.info("Параметр size не может быть меньше или равен 0 и равен {}", size);
            throw new ValidationException("Параметр size не может быть меньше или равен 0");
        }
    }
}
