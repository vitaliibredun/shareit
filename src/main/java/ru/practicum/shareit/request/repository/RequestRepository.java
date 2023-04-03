package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;


import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<ItemRequest, Integer> {

    @Query("select i " +
            "from ItemRequest i " +
            "where i.requestor.id = ?1 " +
            "order by i.created desc")
    List<ItemRequest> findAllBuRequestor(Integer userId);

    @Query("select i " +
            "from ItemRequest i " +
            "where i.requestor.id != ?1 " +
            "order by i.created desc")
    List<ItemRequest> findAllExceptRequestor(Integer userId);
}
