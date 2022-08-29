package ru.practicum.shareIt.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareIt.booking.entity.Booking;
import ru.practicum.shareIt.booking.entity.BookingMapper;
import ru.practicum.shareIt.booking.repository.BookingRepository;
import ru.practicum.shareIt.comments.entity.CommentDto;
import ru.practicum.shareIt.comments.entity.CommentMapper;
import ru.practicum.shareIt.comments.repository.CommentRepository;
import ru.practicum.shareIt.exception.FalseIdException;
import ru.practicum.shareIt.exception.NotFoundException;
import ru.practicum.shareIt.exception.ValidationException;
import ru.practicum.shareIt.item.entity.Item;
import ru.practicum.shareIt.item.entity.ItemDto;
import ru.practicum.shareIt.item.entity.ItemMapper;
import ru.practicum.shareIt.item.repository.ItemRepository;
import ru.practicum.shareIt.user.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemServiceImpl(UserService userService, ItemRepository itemRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.userService = userService;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        validationUserId(userId);
        if (userService.getUser(userId) == null) {
            throw new FalseIdException("Пользователя с ID " + userId + " не существует");
        }
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            log.info("Нет наименования вещи");
            throw new ValidationException("Нет наименования вещи");
        }
        if (itemDto.getDescription() == null) {
            log.info("Нет описания вещи");
            throw new ValidationException("Нет описания вещи");
        }
        if (itemDto.getAvailable() == null) {
            log.info("Нет статуса доступности вещи");
            throw new ValidationException("Отсутствует статус доступности вещи");
        }
        Item item = ItemMapper.toItem(userId, itemDto);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    public void deleteItem(long userId, long itemId) {
        validationUserId(userId);
        if (itemId <= 0) {
            log.info("ID вещи меньше или равно 0");
            throw new FalseIdException("ID вещи меньше или равно 0");
        }
        itemRepository.deleteById(itemId);
    }

    public List<ItemDto> searchItem(long userId, String search, int from, int size) {
        validationUserId(userId);
        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));

        if (search.isEmpty()) {
            log.info("Строка поиска пустая");
            return Collections.emptyList();
        }
        return itemRepository.search(search, pageable).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto getItemById(long userId, long itemId) {
        validationUserId(userId);
        if (itemId <= 0) {
            log.info("ID меньше или равно 0");
            throw new FalseIdException("ID меньше или равно 0");
        }
        if (itemRepository.getItemById(itemId) == null) {
            log.info("Вещь {} не найдена", itemId);
            throw new NotFoundException("Вещь ID " + itemId + " не найдена");
        }
        List<Booking> booking = getBookingByItemIdByStart(itemId);
        List<Booking> bookings2 = getBookingByItemIdByEnd(booking);
        ItemDto item = ItemMapper.toItemDto(itemRepository.getItemById(itemId));
        if (item.getOwnerId() == userId) {
            if (booking.size() != 0) {
                item.setLastBooking(BookingMapper.toBookingBefore(booking.get(0)));
                item.setNextBooking(BookingMapper.toBookingAfter(bookings2.get(0)));
            }
        } else {
            item.setLastBooking(null);
            item.setNextBooking(null);
        }
        List<CommentDto> commentDtos = new ArrayList<>();
        if (commentRepository.getAllByItemId(itemId).size() != 0) {
            commentDtos = commentRepository.getAllByItemId(itemId).stream()
                    .map(CommentMapper::toCommentDto)
                    .peek(c -> c.setAuthorName(userService.getUser(commentRepository.getCommentById(c.getId())
                            .getAuthorId()).getName()))
                    .collect(Collectors.toList());
        }
        item.setComments(commentDtos);
        return item;
    }

    public List<ItemDto> getItems(long userId) {
        List<Booking> booking;
        List<Booking> bookings2;
        List<ItemDto> allItems = new ArrayList<>();
        ItemDto itemDto;
        validationUserId(userId);

        List<Item> items = itemRepository.findAll().stream()
                .filter(item -> item.getOwnerId() == userId)
                .collect(Collectors.toList());
        for (Item item : items) {
            booking = getBookingByItemIdByStart(item.getId());
            bookings2 = getBookingByItemIdByEnd(booking);
            itemDto = ItemMapper.toItemDto(item);
            if (booking.size() != 0) {
                itemDto.setLastBooking(BookingMapper.toBookingBefore(booking.get(0)));
                itemDto.setNextBooking(BookingMapper.toBookingAfter(bookings2.get(0)));
            } else {
                itemDto.setLastBooking(null);
                itemDto.setNextBooking(null);
            }
            allItems.add(itemDto);
        }
        allItems = allItems.stream().sorted(comparing(ItemDto::getId)).collect(Collectors.toList());
        return allItems;
    }

    public List<Item> getItemsByRequestId(long requestId) {
        if (requestId <= 0) {
            throw new FalseIdException("ID меньше или равно 0");
        }
        List<Item> items = itemRepository.getItemsByRequestId(requestId);
        if (items.size() == 0) {
            return Collections.emptyList();
        }
        return items;
    }

    @Transactional
    public Item updateItem(Long userId, Item item) {
        Optional<Item> bazeItem = itemRepository.findById(item.getId());
        Item newItem = new Item();
        if (bazeItem.isPresent()) {
            newItem = bazeItem.get();
        }
        validationUserId(userId);
        if (userService.getUser(userId) == null) {
            log.info("Пользователь {} не существует", userId);
            throw new FalseIdException("Пользователь " + userId + " не существует");
        }
        if (!userId.equals(newItem.getOwnerId())) {
            log.info("Пользователь {} не вляется владельцем вещи {}", userId, item.getId());
            throw new NotFoundException("Пользователь " + userId + " не является владельцем вещи "
                    + item.getId() + " " + item.getName());
        }
        if (item.getName() != null) {
            newItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            newItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            newItem.setAvailable(item.getAvailable());
        }
        return itemRepository.save(newItem);
    }

    public static void validationUserId(long userId) {
        if (userId <= 0) {
            log.info("ID пользователя равен 0");
            throw new FalseIdException("ID меньше или равно 0");
        }
    }

    public List<Booking> getBookingByItemIdByStart(long itemId) {
        return bookingRepository.getBookingsByItemById(itemId)
                .stream().sorted(comparing(Booking::getStart)).collect(Collectors.toList());
    }

    public List<Booking> getBookingByItemIdByEnd(List<Booking> booking) {
        return booking.stream().sorted(comparing(Booking::getEnd).reversed())
                .collect(Collectors.toList());
    }
}
