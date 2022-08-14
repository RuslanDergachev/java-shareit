package ru.practicum.shareIt.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareIt.booking.Booking;
import ru.practicum.shareIt.booking.BookingMapper;
import ru.practicum.shareIt.booking.BookingRepository;
import ru.practicum.shareIt.comments.CommentDto;
import ru.practicum.shareIt.comments.CommentMapper;
import ru.practicum.shareIt.comments.CommentRepository;
import ru.practicum.shareIt.exception.FalseIdException;
import ru.practicum.shareIt.exception.NotFoundException;
import ru.practicum.shareIt.exception.ValidationException;
import ru.practicum.shareIt.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;

    public ItemServiceImpl(UserService userService, ItemRepository itemRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.userService = userService;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        if (userId <= 0 || userService.getUser(userId) == null) {
            throw new FalseIdException("ID меньше или равно 0");
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
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(userId, itemDto)));
    }

    @Transactional
    public void deleteItem(long userId, long itemId) {
        if (userId <= 0) {
            log.info("ID пользователя меньше или равно 0");
            throw new FalseIdException("ID пользователя меньше или равно 0");
        }
        if (itemId <= 0) {
            log.info("ID вещи меньше или равно 0");
            throw new FalseIdException("ID вещи меньше или равно 0");
        }
        itemRepository.deleteById(itemId);
    }

    public List<ItemDto> searchItem(long userId, String search) {
        if (userId <= 0) {
            log.info("ID пользователя меньше 0");
            throw new FalseIdException("ID пользователя меньше 0");
        }
        if (search.isEmpty()) {
            log.info("Строка поиска пустая");
            return new ArrayList<>();
        }
        return itemRepository.search(search).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto getItemById(long userId, long itemId) {
        if (userId <= 0 & itemId <= 0) {
            log.info("ID равно 0");
            throw new FalseIdException("ID меньше или равно 0");
        }
        if (itemRepository.getItemById(itemId) == null) {
            log.info("Вещь {} не найдена", itemId);
            throw new NotFoundException("Вещь ID " + itemId + " не найдена");
        }
        List<Booking> booking = bookingRepository.getBookingsByItemById(itemId)
                .stream().sorted(comparing(Booking::getStart)).collect(Collectors.toList());
        List<Booking> bookings2 = booking.stream().sorted(comparing(Booking::getEnd).reversed())
                .collect(Collectors.toList());
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
            commentDtos = commentRepository.getAllByItemId(itemId).stream().map(CommentMapper::toCommentDto)
                    .peek(c -> c.setAuthorName(userService.getUser(commentRepository.getCommentById(c.getId())
                            .getAuthorId()).getName())).collect(Collectors.toList());
        }
        item.setComments(commentDtos);
        return item;
    }

    public List<ItemDto> getItems(long userId) {
        List<Booking> booking;
        List<Booking> bookings2;
        List<ItemDto> allItems = new ArrayList<>();
        ItemDto itemDto;

        if (userId <= 0) {
            log.info("ID пользователя равен 0");
            throw new FalseIdException("ID меньше или равно 0");
        }
        List<Item> items = itemRepository.findAll().stream()
                .filter(item -> item.getOwnerId() == userId)
                .collect(Collectors.toList());
        for (Item item : items) {
            booking = bookingRepository.getBookingsByItemById(item.getId())
                    .stream().sorted(comparing(Booking::getStart)).collect(Collectors.toList());
            bookings2 = booking.stream().sorted(comparing(Booking::getEnd).reversed())
                    .collect(Collectors.toList());
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

    @Transactional
    public Item updateItem(Long userId, Item item) {
        Optional<Item> bazeItem = itemRepository.findById(item.getId());
        Item newItem = new Item();
        if (bazeItem.isPresent()) {
            newItem = bazeItem.get();
        }
        if (userId <= 0) {
            log.info("ID пользователя равно 0");
            throw new FalseIdException("ID пользователя меньше или равно 0");
        }
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
}
