package ru.practicum.shareIt.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareIt.item.entity.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i where upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%')) and i.available = true")
    Page<Item> search(String text, Pageable pageable);

    Item getItemById(Long id);

    @Transactional(readOnly = true)
    @Query(nativeQuery = true, value = "select * from items where request_id =:requestId")
    List<Item> getItemsByRequestId(long requestId);
}
