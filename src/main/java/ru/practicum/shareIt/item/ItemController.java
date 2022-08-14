package ru.practicum.shareIt.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareIt.comments.Comment;
import ru.practicum.shareIt.comments.CommentDto;
import ru.practicum.shareIt.comments.CommentService;

import javax.validation.Valid;
import java.util.List;

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
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                       @Valid @RequestBody ItemDto itemDto) {
        log.debug("Добавлена вещь: {}", itemDto);
        return itemService.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                          @RequestBody ItemDto itemDto) {
        log.debug("Обновлена вещь: {}", itemDto);
        itemDto.setId(itemId);
        return ItemMapper.toItemDto(itemService.updateItem(userId, ItemMapper.toItem(userId, itemDto)));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.debug("Получен запрос вещи по ID");
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping()
    public List<ItemDto> get(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Получен запрос cписка вещей");
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam String text) {
        log.debug("Получен запрос вещей по наименованию");
        return itemService.searchItem(userId, text);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        log.debug("Получен запрос на удаление вещи");
        itemService.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                                    @Valid @RequestBody Comment comment) {
        log.debug("Добавлен комментарий к вещи: {}", itemId);
        return commentService.createComment(userId, itemId, comment);
    }
}
