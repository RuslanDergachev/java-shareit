package ru.practicum.shareIt.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareIt.request.model.dto.ItemRequestDto;
import ru.practicum.shareIt.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("Добавлен запрос вещи: {}", itemRequestDto);
        return itemRequestService.addNewItemRequest(userId, itemRequestDto);
    }

    @GetMapping()
    public List<ItemRequestDto> get(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Получен список запросов пользователя {} на поиск вещей для бронирования", userId);
        return itemRequestService.getItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                       @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {
        log.debug("Получен список всех запросов");
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        log.debug("Получен список всех запросов");
        return itemRequestService.getRequestById(userId, requestId);
    }
}
