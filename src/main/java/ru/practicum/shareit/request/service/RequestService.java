package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfo;

import java.util.List;

public interface RequestService {

    ItemRequestDto createItemRequest(Integer userId, ItemRequestDto itemRequestDto);

    List<ItemRequestInfo> findAllItemRequestsByUser(Integer userId);

    List<ItemRequestInfo> findAllItemRequests(Integer userId, Pageable pageable);

    ItemRequestInfo findItemRequest(Integer userId, Integer requestId);
}
