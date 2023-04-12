package ru.practicum.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    @Query("select i " +
            "from Item i " +
            "where (i.available = true) " +
            "and upper(i.description) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1)) " +
            "or upper(i.name) like upper(concat(?1, '%')) " +
            "or upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.name) like upper(concat('%', ?1))")
    List<Item> searchItemByInput(String text, Pageable pageable);

    @Query("select i " +
            "from Item i " +
            "where i.owner.id = ?1")
    List<Item> findAllByOwner(Integer userId, Pageable pageable);

    @Query("select new ru.practicum.item.dto.ItemDto" +
            "(i.id, i.name, i.description, i.available, i.request.id) " +
            "from Item i " +
            "where i.request.id = ?1")
    List<ItemDto> findItemsByRequest(Integer requestId);
}
