package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.data.domain.*;
import ru.practicum.shareit.exception.FalseIdException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.entity.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestServiceImpl;
    @Mock
    private ItemRequestRepository mockItemRequestRepository;
    @Mock
    private UserService mockUserService;
    @Mock
    private ItemService mockItemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private Logger log;

    @Test
    void shouldReturnNewItemRequest() {

        ItemRequestDto itemRequestDto = ItemRequestMapper
                .toItemRequestDto(new ItemRequest(1L, "перфоратор", 1L));
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        Mockito
                .when(mockItemRequestRepository.save(Mockito.any()))
                .thenReturn(ItemRequestMapper.toItemRequest(1L, itemRequestDto));
        ItemRequestDto itemRequestDto1 = itemRequestServiceImpl.addNewItemRequest(1L, itemRequestDto);
        Mockito.verify(mockUserService, Mockito.times(1))
                .getUser(1L);

        Assertions.assertEquals(itemRequestDto.getId(), itemRequestDto1.getId());
        Assertions.assertEquals(itemRequestDto.getDescription(), itemRequestDto1.getDescription());
        Assertions.assertEquals(itemRequestDto.getRequestor(), itemRequestDto1.getRequestor());
        Assertions.assertEquals(itemRequestDto.getCreated().getMinute(), itemRequestDto1.getCreated().getMinute());
    }

    @Test
    void whenItemRequestEqualsDescriptionIsNull_thenReturnException() {
        ItemRequestDto itemRequestDto1 = ItemRequestMapper
                .toItemRequestDto(new ItemRequest(1L, null, 1L));
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemRequestServiceImpl.addNewItemRequest(1L, itemRequestDto1));

        Assertions.assertEquals("Запрос пользователя ID 1 пустой", exception.getMessage());
    }

    @Test
    void shouldReturnItemRequestsList() {
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        Item item = new Item(1L, "перфоратор", "перфоратор электрический"
                , true, 1L, 1L);
        List<Item> items = new ArrayList<>();
        items.add(item);
        List<ItemRequest> itemRequests = List.of(new ItemRequest(1L, "перфоратор электрический", 1L));
        List<ItemRequestDto> itemRequestDtos = List.of(new ItemRequestDto(1L, "перфоратор электрический"
                , 1L, LocalDateTime.now(), List.of(item)));
        Mockito
                .when(mockItemRequestRepository.findAllByRequestorId(Mockito.eq(1L)))
                .thenReturn(itemRequests);
        Mockito
                .when(mockItemService.getItemsByRequestId(Mockito.anyLong()))
                .thenReturn(List.of(item));

        Assertions.assertEquals(itemRequestDtos.size(), itemRequestServiceImpl.getItemRequests(1L).size());
        Assertions.assertEquals(itemRequestDtos.get(0).getDescription()
                , itemRequestServiceImpl.getItemRequests(1L).get(0).getDescription());
        Assertions.assertEquals(itemRequestDtos.get(0).getItems(), itemRequestServiceImpl.getItemRequests(1L)
                .get(0).getItems());
        Assertions.assertEquals(itemRequestDtos.size(), itemRequestServiceImpl.getItemRequests(1L)
                .size());
    }

    @Test
    void shouldReturnAllItemRequests() {
        int from = 0;
        int size = 20;
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        Item item = new Item(1L, "болгарка", "болгарка электрическая"
                , true, 1L, 1L);
        List<Item> items = new ArrayList<>();
        items.add(item);
        List<ItemRequest> itemRequests = List.of(new ItemRequest(1L, "болгарка электрическая", 2L));
        List<ItemRequestDto> itemRequestDtos = List.of(new ItemRequestDto(1L, "болгарка электрическая"
                , 2L, LocalDateTime.now(), List.of(item)));
        Pageable pageable = PageRequest.of(from, size, Sort.by("id").descending());
        Page<ItemRequest> itemRequest = new PageImpl<>(itemRequests, pageable, itemRequests.size());
        Mockito
                .when(mockItemRequestRepository.findItemRequestsByRequestorIdNot(1L, pageable))
                .thenReturn(itemRequest);
        List<ItemRequestDto> checkListItemRequestDto = itemRequestServiceImpl.getAllItemRequests(1L, from, size);

        Assertions.assertEquals(itemRequestDtos.size(), checkListItemRequestDto.size());
        Assertions.assertEquals(itemRequestDtos.get(0).getDescription(), checkListItemRequestDto.get(0).getDescription());
    }

    @Test
    void whenSizeIsNull_thenReturnException() {
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemRequestServiceImpl.getAllItemRequests(1L, 0, 0));

        Assertions.assertEquals("Параметр size не может быть меньше или равен 0", exception.getMessage());
    }

    @Test
    void whenFromLessThenZero_thenReturnException() {
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemRequestServiceImpl.getAllItemRequests(1L, -1, 2));

        Assertions.assertEquals("Параметр from не может быть меньше 0", exception.getMessage());
    }

    @Test
    void shouldReturnRequestById() {
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        Item item = new Item(1L, "перфоратор", "перфоратор электрический"
                , true, 1L, 1L);
        List<Item> items = new ArrayList<>();
        items.add(item);
        ItemRequest itemRequest = new ItemRequest(1L, "перфоратор электрический", 1L);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(items);
        Mockito
                .when(mockItemRequestRepository.findItemRequestsById(Mockito.anyLong()))
                .thenReturn(itemRequest);
        Mockito
                .when(itemRepository.getItemsByRequestId(Mockito.anyLong()))
                .thenReturn(items);

        Assertions.assertEquals(itemRequestDto.getId(), itemRequestServiceImpl
                .getRequestById(1L, 1L).getId());
        Assertions.assertEquals(itemRequestDto.getItems().size(), itemRequestServiceImpl
                .getRequestById(1L, 1L).getItems().size());
        Assertions.assertEquals(itemRequestDto.getDescription(), itemRequestServiceImpl
                .getRequestById(1L, 1L).getDescription());
    }

    @Test
    void whenRequestIdIsNull_thenReturnException() {
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemRequestServiceImpl.getRequestById(1L, 0));

        Assertions.assertEquals("ID запроса меньше или равно 0", exception.getMessage());
    }

    @Test
    void whenRequestIdLessThenZero_thenReturnException() {
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemRequestServiceImpl.getRequestById(1L, -1));

        Assertions.assertEquals("ID запроса меньше или равно 0", exception.getMessage());
    }

    @Test
    void whenItemRequestIsNotEquals_thenReturnException() {
        Mockito
                .when(mockUserService.getUser(Mockito.eq(1L)))
                .thenReturn(new User(1L, "Ivan", "user@email.ru"));
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestServiceImpl.getRequestById(1L, 2L));

        Assertions.assertEquals("Запроса с ID 2 не существует", exception.getMessage());
    }

    @Test
    void whenUserDoesNotExist_thenReturnException() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> ItemRequestServiceImpl.validationByUserId(2L, mockUserService, log));

        Assertions.assertEquals("Пользователя ID 2 не существует", exception.getMessage());
    }

    @Test
    void whenUserIdLessThenZero_thenReturnException() {
        final FalseIdException exception1 = Assertions.assertThrows(
                FalseIdException.class,
                () -> ItemRequestServiceImpl.validationByUserId(-1L, mockUserService, log));

        Assertions.assertEquals("ID меньше или равно 0", exception1.getMessage());
    }
}