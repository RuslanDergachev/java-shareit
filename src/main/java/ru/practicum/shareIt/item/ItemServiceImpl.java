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
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        if (userId <=0 || userService.getUser(userId) == null){
            throw new NullPointerException("ID меньше или равно 0");
        }
        if(itemDto.getName() == null || itemDto.getName().isEmpty()){
            log.info("Нет наименования вещи");
            throw new ValidationException("Нет наименования вещи");
        }
        if(itemDto.getDescription() == null){
            log.info("Нет описания вещи");
            throw new ValidationException("Нет описания вещи");
        }
        if (itemDto.getAvailable() == null){
            log.info("Нет статуса доступности вещи");
            throw new ValidationException("Отсутствует статус доступности вещи");
        }
        return itemRepository.save(userId, itemDto);
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        if (userId<=0){
            log.info("ID пользователя меньше или равно 0");
            throw new NullPointerException("ID пользователя меньше или равно 0");
        }
        if (itemId<=0){
            log.info("ID вещи меньше или равно 0");
            throw new NullPointerException("ID вещи меньше или равно 0");
        }
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }

    @Override
    public List<ItemDto> searchItem(long userId, String search) {
        if (userId <= 0) {
            log.info("ID пользователя меньше 0");
            throw new NullPointerException("ID пользователя меньше 0");
        }
        if(search.isEmpty()){
            log.info("Строка поиска пустая");
            return new ArrayList<>();
        }
        return itemRepository.searchItem(search);
    }

    public ItemDto getItemById(long userId, long itemId){
        if (userId <= 0 & itemId <= 0){
            log.info("ID равно 0");
            throw new NullPointerException("ID меньше или равно 0");
        }
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        if (userId <=0){
            log.info("ID пользователя равен 0");
            throw new NullPointerException("ID меньше или равно 0");
        }
        return itemRepository.findByUserId(userId);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        if (userId <=0){
            log.info("ID пользователя равно 0");
            throw new NotFoundException("ID пользователя меньше или равно 0");
        }
        if(userService.getUser(userId) == null){
            log.info("Пользователь \"+userId+\" не существует");
            throw new NotFoundException("Пользователь "+userId+" не существует");
        }
        return itemRepository.updateItem(userId, itemId, itemDto);
    }
}
