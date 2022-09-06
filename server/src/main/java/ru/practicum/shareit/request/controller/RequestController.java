package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.util.Util.USER_HEADER;

@Slf4j
@Validated
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto add(@RequestHeader(USER_HEADER) long userId,
                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("Добавлен запрос вещи: {}", itemRequestDto);
        return itemRequestService.addNewItemRequest(userId, itemRequestDto);
    }

    @GetMapping()
    public List<ItemRequestDto> get(@RequestHeader(USER_HEADER) long userId) {
        log.debug("Получен список запросов пользователя {} на поиск вещей для бронирования", userId);
        return itemRequestService.getItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader(USER_HEADER) long userId,
                                       @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                       @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {
        log.debug("Получен список всех запросов");
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(USER_HEADER) long userId, @PathVariable long requestId) {
        log.debug("Получен список всех запросов");
        return itemRequestService.getRequestById(userId, requestId);
    }
}
