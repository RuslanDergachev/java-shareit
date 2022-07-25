package ru.practicum.shareIt.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item add(@RequestHeader("X-Sharer-User-Id") long userId,
                    @Valid @RequestBody Item item) {
        log.debug("Добавлена вещь: {}", item);
        return itemService.addNewItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                       @RequestBody Item item){
        log.debug("Обновлена вещь: {}", item);
        return itemService.updateItem(userId, itemId, item);
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
}
