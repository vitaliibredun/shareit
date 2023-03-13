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
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfo;
import ru.practicum.shareit.item.dto.mapper.ItemForOwnerMapper;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.CommentValidation;
import ru.practicum.shareit.validation.ItemValidation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("itemServiceImpl")
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentsRepository commentsRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemValidation itemValidation;
    private final CommentValidation commentValidation;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final ItemForOwnerMapper infoMapper;

    public ItemServiceImpl(ItemRepository itemRepository,
                           CommentsRepository commentsRepository,
                           BookingRepository bookingRepository,
                           UserRepository userRepository,
                           @Qualifier("itemValidationRepository") ItemValidation itemValidation,
                           CommentValidation commentValidation,
                           ItemMapper itemMapper,
                           CommentMapper commentMapper,
                           ItemForOwnerMapper infoMapper) {
        this.itemRepository = itemRepository;
        this.commentsRepository = commentsRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemValidation = itemValidation;
        this.commentValidation = commentValidation;
        this.itemMapper = itemMapper;
        this.commentMapper = commentMapper;
        this.infoMapper = infoMapper;
    }

    @Override
    public ItemDto createItem(Integer userId, ItemDto itemDto) {
        itemValidation.checkItemData(userId, itemDto);
        Item item = itemMapper.toModel(itemDto);
        item.setOwner(userId);
        Item itemFromRepository = itemRepository.save(item);
        return itemMapper.toDto(itemFromRepository);
    }

    @Override
    public ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto) {
        itemValidation.checkOwnerOfItem(userId, itemId);
        Item item = itemMapper.toModel(itemDto);
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isPresent()) {
            Item itemFromRepository = optionalItem.get();
            if (item.getName() != null) {
                itemFromRepository.setName(item.getName());
                itemRepository.saveAndFlush(itemFromRepository);
            }
            if (item.getDescription() != null) {
                itemFromRepository.setDescription(item.getDescription());
                itemRepository.saveAndFlush(itemFromRepository);
            }
            if (item.getAvailable() != null) {
                itemFromRepository.setAvailable(item.getAvailable());
                itemRepository.saveAndFlush(itemFromRepository);
            }
        }
        return itemMapper.toDto(optionalItem.orElseThrow());
    }

    @Override
    public ItemInfo findItem(Integer userId, Integer itemId) {
        itemValidation.checkIfItemExist(itemId);
        Item item = itemRepository.findById(itemId).orElseThrow();
        List<CommentInfo> commentInfo = commentsRepository.findAllByItemId(itemId);
        boolean isOwner = item.getOwner().equals(userId);
        if (isOwner) {
            LastBooking lastBooking = bookingRepository.findLastBooking(itemId);
            NextBooking nextBooking = bookingRepository.findNextBooking(itemId);
            return infoMapper.toDto(item, commentInfo, lastBooking, nextBooking);
        }
        return infoMapper.toDto(item, commentInfo, null, null);
    }

    @Override
    public List<ItemInfo> findAllItemsByUser(Integer userId) {
        List<ItemInfo> itemsInfo = new ArrayList<>();
        List<Item> items = itemRepository.findAllByUser(userId);
        for (Item item : items) {
            List<CommentInfo> comments = commentsRepository.findAllByItemId(item.getId());
            LastBooking lastBooking = bookingRepository.findLastBooking(item.getId());
            NextBooking nextBooking = bookingRepository.findNextBooking(item.getId());

            itemsInfo.add(infoMapper.toDto(
                    item,
                    comments,
                    lastBooking,
                    nextBooking
            ));
        }
        return itemsInfo;
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
        Comment comment = commentMapper.toModel(commentDto);
        comment.setAuthorId(userId);
        comment.setItemId(itemId);
        comment.setCreated(LocalDateTime.now());
        Comment commentFromRepository = commentsRepository.save(comment);
        User user = userRepository.findById(userId).orElseThrow();
        return commentMapper.toDto(commentFromRepository, user);
    }
}
