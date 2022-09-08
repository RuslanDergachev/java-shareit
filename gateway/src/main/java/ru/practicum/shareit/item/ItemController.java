package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.FalseIdException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.ValidationMetods;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import java.util.Collections;

import static ru.practicum.shareit.util.Util.USER_HEADER;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> add(@RequestHeader(USER_HEADER) long userId,
                                      @Valid @RequestBody ItemDto itemDto) {
        ValidationMetods.validationId(userId);
        ValidationMetods.validationItemDto(itemDto);

        log.debug("Добавлена вещь: {}", itemDto);
        return itemClient.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object>  update(@RequestHeader(USER_HEADER) long userId, @PathVariable long itemId,
                          @RequestBody ItemDto itemDto) {
        ValidationMetods.validationId(userId);
        ValidationMetods.validationId(itemId);
        log.debug("Обновлена вещь: {}", itemDto);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(USER_HEADER) long userId, @PathVariable long itemId) {
        log.debug("Получен запрос вещи по ID");
        ValidationMetods.validationId(userId);
        ValidationMetods.validationId(itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping()
    public ResponseEntity<Object> get(@RequestHeader(USER_HEADER) long userId) {
        log.debug("Получен запрос cписка вещей");
        ValidationMetods.validationId(userId);
        return itemClient.getItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(USER_HEADER) long userId,
                                    @RequestParam String text,
                                    @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                    @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {
        log.debug("Получен запрос вещей по наименованию");
        ValidationMetods.validationId(userId);
        return itemClient.searchItemsByText(userId, text, from, size);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(USER_HEADER) long userId,
                           @PathVariable long itemId) {
        ValidationMetods.validationId(userId);
        ValidationMetods.validationId(itemId);
        log.debug("Получен запрос на удаление вещи");
        itemClient.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_HEADER) long userId, @PathVariable long itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        ValidationMetods.validationId(userId);
        ValidationMetods.validationId(itemId);
        log.debug("Добавлен комментарий к вещи: {}", itemId);
        return itemClient.createComment(userId, itemId, commentDto);
    }
}
