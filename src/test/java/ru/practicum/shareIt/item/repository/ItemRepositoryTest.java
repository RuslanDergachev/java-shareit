package ru.practicum.shareIt.item.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareIt.item.entity.Item;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.List;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource(properties = {"db.name=testjpa"})
class ItemRepositoryTest {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;
    private final Item item = Item.builder()
            .name("Дрель электрическая")
            .description("Mocito")
            .available(true)
            .ownerId(1L)
            .requestId(1L)
            .build();

    @Test
    void shouldReturnItemByText() {
        int from = 0;
        int size = 1;
        String text = "Дрель";
        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));
        itemRepository.save(item);
        Page<Item> item1 = itemRepository.search(text, pageable);
        List<Item> items = item1.toList();

        Assertions.assertEquals(item.getId(), items.get(0).getId());
        Assertions.assertEquals(item.getDescription(), items.get(0).getDescription());
        Assertions.assertEquals(item.getAvailable(), items.get(0).getAvailable());
        Assertions.assertEquals(item.getOwnerId(), items.get(0).getOwnerId());
    }

    @Test
    void shouldReturnItemsByRequestId() {
        itemRepository.save(item);
        List<Item> items = itemRepository.getItemsByRequestId(1L);

        Assertions.assertEquals(item.getId(), items.get(0).getId());
        Assertions.assertEquals(item.getDescription(), items.get(0).getDescription());
        Assertions.assertEquals(item.getAvailable(), items.get(0).getAvailable());
        Assertions.assertEquals(item.getOwnerId(), items.get(0).getOwnerId());
        Assertions.assertEquals(item.getOwnerId(), items.get(0).getRequestId());
    }
}