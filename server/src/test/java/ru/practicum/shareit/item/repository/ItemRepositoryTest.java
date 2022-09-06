package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.model.entity.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource(properties = {"db.name=testjpa"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemRepositoryTest {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private final Item item = Item.builder()
            .name("Дрель электрическая")
            .description("Mocito")
            .available(true)
            .ownerId(1L)
            .requestId(1L)
            .build();

    private ItemRequest itemRequest = ItemRequest.builder()
            .description("Mocito")
            .requestorId(null)
            .build();
    private User user = User.builder()
            .name("Vasya")
            .email("user@user.ru")
            .build();

    @BeforeAll
    void createData(){
        userRepository.save(user);
        itemRequestRepository.save(itemRequest);
        itemRepository.save(item);
    }

    @Test

    void shouldReturnItemByText() {

        int from = 0;
        int size = 1;
        String text = "Дрель";
        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));

        Page<Item> item1 = itemRepository.search(text, pageable);
        List<Item> items = item1.toList();

        Assertions.assertEquals(item.getId(), items.get(0).getId());
        Assertions.assertEquals(item.getDescription(), items.get(0).getDescription());
        Assertions.assertEquals(item.getAvailable(), items.get(0).getAvailable());
        Assertions.assertEquals(item.getOwnerId(), items.get(0).getOwnerId());
    }

    @Test

    void shouldReturnItemsByRequestId() {

        List<Item> items = itemRepository.getItemsByRequestId(1L);

        Assertions.assertEquals(item.getId(), items.get(0).getId());
        Assertions.assertEquals(item.getDescription(), items.get(0).getDescription());
        Assertions.assertEquals(item.getAvailable(), items.get(0).getAvailable());
        Assertions.assertEquals(item.getOwnerId(), items.get(0).getOwnerId());
        Assertions.assertEquals(item.getOwnerId(), items.get(0).getRequestId());
    }
}