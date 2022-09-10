package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.FalseIdException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.entity.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@Slf4j
@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private UserService userService;
    private ItemRequestRepository itemRequestRepository;
    private ItemService itemService;
    private ItemRepository itemRepository;

    @Override
    public ItemRequestDto addNewItemRequest(long userId, ItemRequestDto itemRequestDto) {
        validationByUserId(userId, userService, log);
        if (itemRequestDto.getDescription() == null) {
            log.warn("Запрос пользователя {} пустой", userId);
            throw new ValidationException("Запрос пользователя ID " + userId + " пустой");
        }
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(userId, itemRequestDto);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getItemRequests(long userId) {
        validationByUserId(userId, userService, log);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(userId);
        List<ItemRequestDto> itemRequestDtos = itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .peek(itemRequestDto -> itemRequestDto.setItems(itemService.getItemsByRequestId(itemRequestDto.getId())))
                .sorted(comparing(ItemRequestDto::getCreated).reversed())
                .collect(Collectors.toList());
        return itemRequestDtos;
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(long userId, int from, int size) {
        validationByUserId(userId, userService, log);
        if (from < 0) {
            log.info("Параметр from не может быть меньше 0 и равен {}", from);
            throw new ValidationException("Параметр from не может быть меньше 0");
        }
        if (size <= 0) {
            log.warn("Параметр size не может быть меньше или равен 0 и равен {}", size);
            throw new ValidationException("Параметр size не может быть меньше или равен 0");
        }
        Pageable pageable = PageRequest.of(from, size, Sort.by("id").descending());
        Page<ItemRequest> itemRequestPage = itemRequestRepository.findItemRequestsByRequestorIdNot(userId, pageable);

        return itemRequestPage.stream().map(ItemRequestMapper::toItemRequestDto)
                .peek(itemRequestDto ->
                        itemRequestDto.setItems(itemRepository.getItemsByRequestId(itemRequestDto.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(long userId, long requestId) {
        validationByUserId(userId, userService, log);
        if (requestId <= 0) {
            log.warn("ID запроса {} меньше или равно 0", requestId);
            throw new ValidationException("ID запроса меньше или равно 0");
        }
        if (itemRequestRepository.findItemRequestsById(requestId) == null) {
            log.warn("Запроса с ID {} не существует", requestId);
            throw new NotFoundException("Запроса с ID " + requestId + " не существует");
        }
        ItemRequestDto itemRequestDto = ItemRequestMapper
                .toItemRequestDto(itemRequestRepository.findItemRequestsById(requestId));
        itemRequestDto.setItems(itemRepository.getItemsByRequestId(itemRequestDto.getId()));
        itemRequestDto.setCreated(LocalDateTime.now().withNano(0));
        return itemRequestDto;
    }

    public static void validationByUserId(long userId, UserService userService, Logger log) {
        if (userId <= 0) {
            log.warn("ID пользователя меньше или равно 0");
            throw new FalseIdException("ID меньше или равно 0");
        }
        if (userService.getUser(userId) == null) {
            log.warn("Пользователя {} не существует", userId);
            throw new NotFoundException("Пользователя ID " + userId + " не существует");
        }
    }
}
