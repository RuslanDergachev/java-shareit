package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.Validation;

import javax.validation.Valid;

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
        Validation.validateId(userId);
        Validation.validateItemDto(itemDto);

        log.info("Добавлена вещь: {}", itemDto);
        return itemClient.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object>  update(@RequestHeader(USER_HEADER) long userId, @PathVariable long itemId,
                          @RequestBody ItemDto itemDto) {
        Validation.validateId(userId);
        Validation.validateId(itemId);
        log.info("Обновлена вещь: {}", itemDto);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(USER_HEADER) long userId, @PathVariable long itemId) {
        log.info("Получен запрос вещи по ID");
        Validation.validateId(userId);
        Validation.validateId(itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping()
    public ResponseEntity<Object> get(@RequestHeader(USER_HEADER) long userId) {
        log.info("Получен запрос cписка вещей");
        Validation.validateId(userId);
        return itemClient.getItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(USER_HEADER) long userId,
                                    @RequestParam String text,
                                    @RequestParam(required = false, defaultValue = "0") Integer from,
                                    @RequestParam(required = false, defaultValue = "20") Integer size) {
        log.info("Получен запрос вещей по наименованию");
        Validation.validateId(userId);
        return itemClient.searchItemsByText(userId, text, from, size);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(USER_HEADER) long userId,
                           @PathVariable long itemId) {
        Validation.validateId(userId);
        Validation.validateId(itemId);
        log.info("Получен запрос на удаление вещи");
        itemClient.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_HEADER) long userId, @PathVariable long itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        Validation.validateId(userId);
        Validation.validateId(itemId);
        log.info("Добавлен комментарий к вещи: {}", itemId);
        return itemClient.createComment(userId, itemId, commentDto);
    }
}
