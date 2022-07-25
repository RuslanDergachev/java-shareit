package ru.practicum.shareIt.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareIt.exception.NotFoundException;
import ru.practicum.shareIt.exception.ValidationException;
import ru.practicum.shareIt.user.UserService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService{

    private final UserService userService;

    private ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public Item addNewItem(long userId, Item item) {
        if (userId <=0 || userService.getUser(userId) == null){
            throw new NullPointerException("ID меньше или равно 0");
        }
        if(item.getName() == null || item.getName().isEmpty()){
            log.error("Нет наименования вещи");
            throw new ValidationException("Нет наименования вещи");
        }
        if(item.getDescription() == null){
            log.error("Нет описания вещи");
            throw new ValidationException("Нет описания вещи");
        }
        if (item.getAvailable() == null){
            log.error("Нет статуса доступности вещи");
            throw new ValidationException("Отсутсвует статус доступности вещи");
        }
        return itemRepository.save(userId, item);
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        if (userId<=0 & itemId<=0){
            log.error("ID пользователя или ID вещи меньше или равно 0");
            throw new NullPointerException("ID меньше или равно 0");
        }
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }

    @Override
    public List<ItemDto> searchItem(long userId, String search) {
        if (userId <= 0) {
            log.error("ID пользователя меньше 0");
            throw new NullPointerException("ID пользователя меньше 0");
        }
        if(search.isEmpty()){
            log.error("Строка поиска пустая");
            return new ArrayList<>();
        }
        return itemRepository.searchItem(search);
    }

    public ItemDto getItemById(long userId, long itemId){
        if (userId <= 0 & itemId <= 0){
            log.error("ID равно 0");
            throw new NullPointerException("ID меньше или равно 0");
        }
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        if (userId <=0){
            log.error("ID пользователя равен 0");
            throw new NullPointerException("ID меньше или равно 0");
        }
        return itemRepository.findByUserId(userId);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, Item item) {
        if (userId <=0 || userService.getUser(userId) == null){
            log.error("ID пользователя или вещи равно 0");
            throw new NotFoundException("ID меньше или равно 0");
        }
        return itemRepository.updateItem(userId, itemId, item);
    }
}
