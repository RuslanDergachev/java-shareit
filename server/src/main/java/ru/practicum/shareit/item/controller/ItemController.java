package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comments.entity.Comment;
import ru.practicum.shareit.comments.entity.CommentDto;
import ru.practicum.shareit.comments.service.CommentService;
import ru.practicum.shareit.item.entity.ItemDto;
import ru.practicum.shareit.item.entity.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.util.Util.USER_HEADER;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto add(@RequestHeader(USER_HEADER) long userId,
                       @Valid @RequestBody ItemDto itemDto) {
        log.debug("Добавлена вещь: {}", itemDto);
        return itemService.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_HEADER) long userId, @PathVariable long itemId,
                          @RequestBody ItemDto itemDto) {
        log.debug("Обновлена вещь: {}", itemDto);
        itemDto.setId(itemId);
        return ItemMapper.toItemDto(itemService.updateItem(userId, ItemMapper.toItem(userId, itemDto)));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(USER_HEADER) long userId, @PathVariable long itemId) {
        log.debug("Получен запрос вещи по ID");
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping()
    public List<ItemDto> get(@RequestHeader(USER_HEADER) long userId) {
        log.debug("Получен запрос cписка вещей");
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader(USER_HEADER) long userId, @RequestParam String text,
                                    @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                    @RequestParam(value = "size", required = false, defaultValue = "20") Integer size) {
        log.debug("Получен запрос вещей по наименованию");
        return itemService.searchItem(userId, text, from, size);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(USER_HEADER) long userId,
                           @PathVariable long itemId) {
        log.debug("Получен запрос на удаление вещи");
        itemService.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(USER_HEADER) long userId, @PathVariable long itemId,
                                    @Valid @RequestBody Comment comment) {
        log.debug("Добавлен комментарий к вещи: {}", itemId);
        return commentService.createComment(userId, itemId, comment);
    }
}
