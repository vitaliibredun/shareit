package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

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
    List<Item> searchItemByInput(String text);

    @Query("select i " +
            "from Item i " +
            "where i.owner.id = ?1")
    List<Item> findAllByOwner(Integer userId);

    @Query("select new ru.practicum.shareit.item.dto.ItemDto" +
            "(i.id, i.name, i.description, i.available, i.request.id) " +
            "from Item i " +
            "where i.request.id = ?1")
    List<ItemDto> findItemsByRequest(Integer requestId);
}
