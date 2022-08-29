package ru.practicum.shareIt.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareIt.booking.entity.Booking;
import ru.practicum.shareIt.booking.repository.BookingRepository;
import ru.practicum.shareIt.comments.entity.Comment;
import ru.practicum.shareIt.comments.repository.CommentRepository;
import ru.practicum.shareIt.exception.FalseIdException;
import ru.practicum.shareIt.exception.NotFoundException;
import ru.practicum.shareIt.exception.ValidationException;
import ru.practicum.shareIt.item.entity.Item;
import ru.practicum.shareIt.item.entity.ItemDto;
import ru.practicum.shareIt.item.repository.ItemRepository;
import ru.practicum.shareIt.user.entity.User;
import ru.practicum.shareIt.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private UserService mockUserService;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private BookingRepository mockBookingRepository;
    @Mock
    private CommentRepository mockCommentRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    private ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Перфоратор электрический")
            .description("Mocito")
            .available(true)
            .build();
    private Item item = Item.builder()
            .id(1L)
            .name("Перфоратор электрический")
            .description("Mocito")
            .available(true)
            .ownerId(1L)
            .requestId(1L)
            .build();

    @Test
    void shouldReturnNewItem() {
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        Mockito
                .when(mockItemRepository.save(Mockito.any()))
                .thenReturn(item);
        ItemDto itemDto1 = itemService.addNewItem(1L, itemDto);
        Mockito.verify(mockUserService, Mockito.times(1))
                .getUser(1L);

        assertEquals(itemDto.getId(), itemDto1.getId());
        assertEquals(itemDto.getName(), itemDto1.getName());
        assertEquals(itemDto.getDescription(), itemDto1.getDescription());
        assertEquals(itemDto.getAvailable(), itemDto1.getAvailable());
    }

    @Test
    void whenItemDtoNotEqualsName_thenReturnException() {
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        ItemDto itemDto2 = ItemDto.builder()
                .id(1L)
                .description("Mocito")
                .available(true)
                .build();
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.addNewItem(1L, itemDto2));

        assertEquals("Нет наименования вещи", exception.getMessage());
    }

    @Test
    void whenItemDtoNotEqualsDescription_thenReturnException() {
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        ItemDto itemDto2 = ItemDto.builder()
                .id(1L)
                .name("Отвертка")
                .available(true)
                .build();
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.addNewItem(1L, itemDto2));

        assertEquals("Нет описания вещи", exception.getMessage());
    }

    @Test
    void whenItemDtoNotEqualsAvailable_thenReturnException() {
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        ItemDto itemDto2 = ItemDto.builder()
                .id(1L)
                .name("Отвертка")
                .description("Mocito")
                .build();
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.addNewItem(1L, itemDto2));

        assertEquals("Отсутствует статус доступности вещи", exception.getMessage());
    }

    @Test
    void whenUserNotFound_thenReturnException() {
        ItemDto itemDto2 = ItemDto.builder()
                .id(1L)
                .name("Отвертка")
                .description("Mocito")
                .available(true)
                .build();
        final FalseIdException exception = Assertions.assertThrows(
                FalseIdException.class,
                () -> itemService.addNewItem(1L, itemDto2));

        assertEquals("Пользователя с ID 1 не существует", exception.getMessage());
    }

    @Test
    void shouldDeleteItem() {
        itemService.deleteItem(1L, 1L);
        Mockito.verify(mockItemRepository, Mockito.times(1))
                .deleteById(1L);
    }

    @Test
    void whenItemIdLessThenZero_thenReturnException() {
        final FalseIdException exception = Assertions.assertThrows(
                FalseIdException.class,
                () -> itemService.deleteItem(1L, -1L));

        assertEquals("ID вещи меньше или равно 0", exception.getMessage());
    }

    @Test
    void shouldReturnItemByText() {
        int from = 0;
        int size = 1;
        String text = "ПЕРфоратор";
        List<ItemDto> itemDtoList = List.of(itemDto);
        Item item = new Item(1L, "Перфоратор электрический", "Mocito"
                , true, 1L, 1L);
        List<Item> items = new ArrayList<>();
        items.add(item);
        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));
        Page<Item> itemPage = new PageImpl<>(items, pageable, items.size());
        Mockito
                .when(mockItemRepository.search(text, pageable))
                .thenReturn(itemPage);
        List<ItemDto> newItemDtoList = itemService.searchItem(1L, text, from, size);

        assertEquals(itemDtoList.size(), newItemDtoList.size());
        assertEquals(itemDtoList.get(0).getDescription(), newItemDtoList.get(0).getDescription());
    }

    @Test
    void whenSearchIsNull_thenReturnEmptyList() {
        int from = 0;
        int size = 1;
        String search = "";
        List<ItemDto> newItemDtoList = itemService.searchItem(1L, search, from, size);

        assertEquals(0, newItemDtoList.size());
    }

    @Test
    void shouldReturnItemById() {
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 1, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 1, 13, 10, 10))
                .build();
        Comment comment = Comment.builder()
                .id(1L)
                .text("Какой-то текст")
                .authorId(1L)
                .build();
        item.setRequestId(1L);
        List<Booking> bookings = List.of(booking);
        List<Comment> comments = List.of(comment);
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        Mockito
                .when(mockItemRepository.getItemById(Mockito.anyLong()))
                .thenReturn(item);
        Mockito
                .when(mockBookingRepository.getBookingsByItemById(Mockito.anyLong()))
                .thenReturn(bookings);
        Mockito
                .when(mockCommentRepository.getCommentById(Mockito.anyLong()))
                .thenReturn(comment);
        Mockito
                .when(mockCommentRepository.getAllByItemId(1L))
                .thenReturn(comments);

        ItemDto itemDto1 = itemService.getItemById(1L, 1L);

        assertEquals(itemDto.getId(), itemDto1.getId());
        assertEquals(itemDto.getName(), itemDto1.getName());
        assertEquals("Какой-то текст", itemDto1.getComments().get(0).getText());
        assertEquals(1L, itemDto1.getNextBooking().getId());

    }

    @Test
    void shouldReturnItemByIdNotBookingTest() {
        List<Comment> comments = new ArrayList<>();
        Mockito
                .when(mockItemRepository.getItemById(Mockito.anyLong()))
                .thenReturn(item);
        Mockito
                .when(mockBookingRepository.getBookingsByItemById(Mockito.anyLong()))
                .thenReturn(new ArrayList<>());
        Mockito
                .when(mockCommentRepository.getAllByItemId(1L))
                .thenReturn(comments);
        ItemDto itemDto1 = itemService.getItemById(2L, 1L);

        assertNull(itemDto1.getNextBooking());
        assertNull(itemDto1.getLastBooking());

    }

    @Test
    void whenItemNotInDataBase_thenReturnException() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.getItemById(1L, 1L));

        Assertions.assertEquals("Вещь ID 1 не найдена", exception.getMessage());
    }

    @Test
    void whenItemIdLessZero_thenReturnException() {
        final FalseIdException exception = Assertions.assertThrows(
                FalseIdException.class,
                () -> itemService.getItemById(1L, -1L));

        assertEquals("ID меньше или равно 0", exception.getMessage());
    }

    @Test
    void shouldReturnItemsList() {
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 8, 1, 12, 10, 10))
                .end(LocalDateTime.of(2022, 8, 1, 13, 10, 10))
                .build();
        List<Booking> bookings = List.of(booking);
        Mockito
                .when(mockItemRepository.findAll())
                .thenReturn(List.of(item));
        Mockito
                .when(mockBookingRepository.getBookingsByItemById(Mockito.anyLong()))
                .thenReturn(bookings);
        List<ItemDto> itemDtoList = itemService.getItems(1L);

        assertEquals(1, itemDtoList.size());
        assertEquals("Перфоратор электрический", itemDtoList.get(0).getName());
        assertEquals("Mocito", itemDtoList.get(0).getDescription());
        assertEquals(1, itemDtoList.get(0).getNextBooking().getId());
    }

    @Test
    void whenItemIdZero_thenReturnException() {
        final FalseIdException exception = Assertions.assertThrows(
                FalseIdException.class,
                () -> itemService.getItemsByRequestId(-1L));

        assertEquals("ID меньше или равно 0", exception.getMessage());
    }

    @Test
    void shouldReturnItemsByIdNotBookingTest() {
        Mockito
                .when(mockItemRepository.findAll())
                .thenReturn(List.of(item));
        Mockito
                .when(mockBookingRepository.getBookingsByItemById(Mockito.anyLong()))
                .thenReturn(new ArrayList<>());
        List<ItemDto> itemDtoList = itemService.getItems(1L);

        assertNull(itemDtoList.get(0).getNextBooking());
        assertNull(itemDtoList.get(0).getLastBooking());
    }

    @Test
    void shouldReturnItemsByRequestId() {
        Mockito
                .when(mockItemRepository.getItemsByRequestId(1L))
                .thenReturn(List.of(item));
        List<Item> items = itemService.getItemsByRequestId(1L);

        assertEquals(1, items.get(0).getId());
        assertEquals("Перфоратор электрический", items.get(0).getName());
        assertEquals("Mocito", items.get(0).getDescription());
        assertEquals(1, items.get(0).getRequestId());
    }

    @Test
    void whenItemsListIsNull_thenReturnEmptyList() {
        List<Item> newItemList = itemService.getItemsByRequestId(1L);
        assertEquals(0, newItemList.size());
    }

    @Test
    void shouldReturnUpdateItem() {
        Item item1 = Item.builder()
                .id(1L)
                .name("Дрель электрическая")
                .description("Mocito")
                .available(true)
                .ownerId(1L)
                .requestId(1L)
                .build();
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        Mockito
                .when(mockItemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Mockito
                .when(mockItemRepository.save(item))
                .thenReturn(item1);
        Item newItem = itemService.updateItem(1L, item);

        assertEquals("Дрель электрическая", newItem.getName());
    }

    @Test
    void whenUserIdZero_thenReturnException() {
        final FalseIdException exception = Assertions.assertThrows(
                FalseIdException.class,
                () -> itemService.updateItem(2L, item));

        assertEquals("Пользователь 2 не существует", exception.getMessage());
    }

    @Test
    void whenUserNotOwnerItem_thenReturnException() {
        Mockito
                .when(mockUserService.getUser(Mockito.eq(2L)))
                .thenReturn(new User(2L, "Ivan", "user@email.ru"));
        Mockito
                .when(mockItemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(2L, item));

        assertEquals("Пользователь 2 не является владельцем вещи 1 Перфоратор электрический"
                , exception.getMessage());
    }

    @Test
    void whenUserIdLessThenZero_thenReturnException() {
        final FalseIdException exception1 = Assertions.assertThrows(
                FalseIdException.class,
                () -> ItemServiceImpl.validationUserId(-1L));

        assertEquals("ID меньше или равно 0", exception1.getMessage());
    }
}