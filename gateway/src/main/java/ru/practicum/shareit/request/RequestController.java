package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;

import static ru.practicum.shareit.util.Util.USER_HEADER;

@Slf4j
@Validated
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> add(@RequestHeader(USER_HEADER) long userId,
                                      @Valid @RequestBody RequestDto requestDto) {
        log.debug("Добавлен запрос вещи: {}", requestDto);
        return requestClient.addNewRequest(userId, requestDto);
    }

    @GetMapping()
    public ResponseEntity<Object> get(@RequestHeader(USER_HEADER) long userId) {
        log.debug("Получен список запросов пользователя {} на поиск вещей для бронирования", userId);
        return requestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(USER_HEADER) long userId,
                                  @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                  @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {
        log.debug("Получен список всех запросов");
        return requestClient.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(USER_HEADER) long userId, @PathVariable long requestId) {
        log.debug("Получен список всех запросов");
        return requestClient.getRequestById(userId, requestId);
    }
}
