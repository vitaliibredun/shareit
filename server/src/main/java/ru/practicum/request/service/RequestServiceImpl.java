package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.RequestNotFoundException;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestInfo;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.validation.UserValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("requestServiceImpl")
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final RequestMapper mapper;
    private final UserValidation userValidation;

    @Override
    public ItemRequestDto createItemRequest(Integer userId, ItemRequestDto itemRequestDto) {
        User user = userValidation.checkUserExist(userId);
        ItemRequest itemRequest = mapper.toModel(itemRequestDto);
        itemRequest.setRequestor(user);
        ItemRequest itemFromRepository = requestRepository.save(itemRequest);
        return mapper.toDto(itemFromRepository);
    }

    @Override
    @Cacheable(cacheNames = {"findAllItemRequestsByUser"}, key = "#userId")
    public List<ItemRequestInfo> findAllItemRequestsByUser(Integer userId) {
        userValidation.checkUserExist(userId);
        List<ItemRequestInfo> requestList = new ArrayList<>();
        List<ItemRequest> itemRequests = requestRepository.findAllBuRequestor(userId);
        for (ItemRequest request : itemRequests) {
            List<ItemDto> items = itemRepository.findItemsByRequest(request.getId());
            ItemRequestInfo itemRequestInfo = mapper.toDto(request, items);
            requestList.add(itemRequestInfo);
        }
        return requestList;
    }

    @Override
    @Cacheable(cacheNames = {"findAllItemRequests"}, key = "{#userId, #pageable.pageNumber, #pageable.pageSize}")
    public List<ItemRequestInfo> findAllItemRequests(Integer userId, Pageable pageable) {
        List<ItemRequestInfo> requestList = new ArrayList<>();
        List<ItemRequest> itemRequests = requestRepository.findAllExceptRequestor(userId, pageable);
        for (ItemRequest request : itemRequests) {
            List<ItemDto> items = itemRepository.findItemsByRequest(request.getId());
            ItemRequestInfo itemRequestInfo = mapper.toDto(request, items);
            requestList.add(itemRequestInfo);
        }
        return requestList;
    }

    @Override
    @Cacheable(cacheNames = {"findItemRequest"}, key = "{#userId, #requestId}")
    public ItemRequestInfo findItemRequest(Integer userId, Integer requestId) {
        userValidation.checkUserExist(userId);
        Optional<ItemRequest> itemRequest = requestRepository.findById(requestId);
        if (itemRequest.isPresent()) {
            List<ItemDto> items = itemRepository.findItemsByRequest(itemRequest.get().getId());
            return mapper.toDto(itemRequest.get(), items);
        }
        throw new RequestNotFoundException("The ru.practicum.item ru.practicum.request doesn't exist");
    }
}
