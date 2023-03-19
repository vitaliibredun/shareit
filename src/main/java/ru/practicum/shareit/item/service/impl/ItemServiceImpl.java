package ru.practicum.shareit.item.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.LastBooking;
import ru.practicum.shareit.booking.dto.NextBooking;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.dto.CommentInfo;
import ru.practicum.shareit.comments.dto.mapper.CommentMapper;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.comments.repository.CommentsRepository;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfo;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.comments.validation.CommentValidation;
import ru.practicum.shareit.item.validation.ItemValidation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("itemServiceImpl")
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentsRepository commentsRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemValidation itemValidation;
    private final CommentValidation commentValidation;
    private final CommentMapper commentMapper;
    private final ItemMapper itemMapper;

    public ItemServiceImpl(ItemRepository itemRepository,
                           CommentsRepository commentsRepository,
                           BookingRepository bookingRepository,
                           UserRepository userRepository,
                           @Qualifier("itemValidationRepository") ItemValidation itemValidation,
                           CommentValidation commentValidation, CommentMapper commentMapper, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.commentsRepository = commentsRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemValidation = itemValidation;
        this.commentValidation = commentValidation;
        this.commentMapper = commentMapper;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemDto createItem(Integer userId, ItemDto itemDto) {
        User user = itemValidation.checkItemData(userId, itemDto);
        Item item = itemMapper.toModel(itemDto);
        item.setOwner(user);
        Item itemFromRepository = itemRepository.save(item);
        return itemMapper.toDto(itemFromRepository);
    }

    @Override
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
    public List<ItemInfo> findAllItemsByUser(Integer userId) {
        List<ItemInfo> itemInfoList = new ArrayList<>();
        List<Item> items = itemRepository.findAllByUser(userId);
        for (Item item : items) {
            List<CommentInfo> comments = commentsRepository.findAllByItemId(item.getId());
            LastBooking lastBooking = bookingRepository.findLastBooking(item.getId());
            NextBooking nextBooking = bookingRepository.findNextBooking(item.getId());
            itemInfoList.add(itemMapper.toDto(item, comments, lastBooking, nextBooking));
        }
        return itemInfoList;
    }

    @Override
    public List<ItemDto> searchItemForRent(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.searchItemByInput(text)
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
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
