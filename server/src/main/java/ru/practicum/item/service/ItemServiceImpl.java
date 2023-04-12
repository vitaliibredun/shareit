package ru.practicum.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.booking.repository.BookingRepository;
import ru.practicum.booking.dto.LastBooking;
import ru.practicum.booking.dto.NextBooking;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentInfo;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentsRepository;
import ru.practicum.item.mapper.ItemMapper;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ItemInfo;
import ru.practicum.item.model.Item;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.user.model.User;
import ru.practicum.comments.validation.CommentValidation;
import ru.practicum.item.validation.ItemValidation;
import ru.practicum.user.validation.UserValidation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("itemServiceImpl")
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentsRepository commentsRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final ItemValidation itemValidation;
    private final CommentValidation commentValidation;
    private final UserValidation userValidation;
    private final CommentMapper commentMapper;
    private final ItemMapper itemMapper;

    @Override
    @CachePut(cacheNames = {"recordsCache"}, key = "#userId")
    public ItemDto createItem(Integer userId, ItemDto itemDto) {
        User user = userValidation.checkUserExist(userId);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = requestRepository.findById(itemDto.getRequestId()).orElseThrow();
            Item item = itemMapper.toModel(itemDto, itemRequest);
            item.setOwner(user);
            Item itemFromRepository = itemRepository.save(item);
            return itemMapper.toDto(itemFromRepository);
        }
        Item item = itemMapper.toModel(itemDto);
        item.setOwner(user);
        Item itemFromRepository = itemRepository.save(item);
        return itemMapper.toDto(itemFromRepository);
    }

    @Override
    @CachePut(cacheNames = {"recordsCache"}, key = "{#userId, #itemId}")
    public ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto) {
        Item item = itemValidation.checkOwnerOfItem(userId, itemId);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        Item itemFromRepository = itemRepository.saveAndFlush(item);
        return itemMapper.toDto(itemFromRepository);
    }

    @Override
    @Cacheable(cacheNames = {"recordsCache"}, key = "{#userId, #itemId}")
    public ItemInfo findItem(Integer userId, Integer itemId) {
        Item item = itemValidation.checkIfItemExist(itemId);
        List<CommentInfo> comments = commentsRepository.findAllByItemId(itemId);
        boolean isOwner = item.getOwner().getId().equals(userId);
        if (isOwner) {
            LastBooking lastBooking = bookingRepository.findLastBooking(itemId);
            NextBooking nextBooking = bookingRepository.findNextBooking(itemId);
            return itemMapper.toDto(item, comments, lastBooking, nextBooking);
        }
        return itemMapper.toDto(item, comments, null, null);
    }

    @Override
    @Cacheable(cacheNames = {"recordsCache"}, key = "{#userId, #pageable.pageNumber, #pageable.pageSize}")
    public List<ItemInfo> findAllItemsByUser(Integer userId, Pageable pageable) {
        List<ItemInfo> itemInfoList = new ArrayList<>();
        List<Item> items = itemRepository.findAllByOwner(userId, pageable);
        for (Item item : items) {
            List<CommentInfo> comments = commentsRepository.findAllByItemId(item.getId());
            LastBooking lastBooking = bookingRepository.findLastBooking(item.getId());
            NextBooking nextBooking = bookingRepository.findNextBooking(item.getId());
            itemInfoList.add(itemMapper.toDto(item, comments, lastBooking, nextBooking));
        }
        return itemInfoList;
    }

    @Override
    @Cacheable(cacheNames = {"recordsCache"}, key = "{#text, #pageable.pageNumber, #pageable.pageSize}")
    public List<ItemDto> searchItemForRent(String text, Pageable pageable) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.searchItemByInput(text, pageable)
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @CachePut(cacheNames = {"recordsCache"}, key = "{#userId, #itemId}")
    public CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto) {
        commentValidation.checkUser(userId, itemId, commentDto);
        User user = userRepository.findById(userId).orElseThrow();
        Item item = itemRepository.findById(itemId).orElseThrow();
        Comment comment = commentMapper.toModel(commentDto);
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        Comment commentFromRepository = commentsRepository.save(comment);
        return commentMapper.toDto(commentFromRepository, user);
    }
}
