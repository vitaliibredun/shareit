package ru.practicum.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestInfo;

import java.util.List;

public interface RequestService {

    ItemRequestDto createItemRequest(Integer userId, ItemRequestDto itemRequestDto);

    List<ItemRequestInfo> findAllItemRequestsByUser(Integer userId);

    List<ItemRequestInfo> findAllItemRequests(Integer userId, Pageable pageable);

    ItemRequestInfo findItemRequest(Integer userId, Integer requestId);
}
