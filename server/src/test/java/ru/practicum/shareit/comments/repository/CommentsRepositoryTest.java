package ru.practicum.shareit.comments.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.comments.dto.CommentInfo;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentsRepository;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentsRepositoryTest {
    private final TestEntityManager entityManager;
    private final CommentsRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private User user1;
    private User user2;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        user1 = User
                .builder()
                .name("Bond")
                .email("email@mail.com")
                .build();

        user2 = User
                .builder()
                .name("James")
                .email("mymail@email.com")
                .build();

        item1 = Item
                .builder()
                .name("Item1")
                .description("For something")
                .available(true)
                .owner(user1)
                .build();

        item2 = Item
                .builder()
                .name("Item2")
                .description("For something else")
                .available(true)
                .owner(user2)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
    }

    @AfterEach
    void cleanUp() {
        resetIdColumns();
    }

    @Test
    void verifyRepositoryIsEmpty() {
        List<Comment> comments = repository.findAll();

        assertThat(comments, empty());
    }

    @Test
    void verifyPersistingOfComment() {
        Comment comment = Comment
                .builder()
                .text("Thanks a lot")
                .item(item1)
                .author(user1)
                .build();

        assertThat(entityManager.getId(comment), nullValue());
        entityManager.persist(comment);
        assertThat(entityManager.getId(comment), notNullValue());
    }

    @Test
    void verifyCommentSaveInRepository() {
        Comment comment = Comment
                .builder()
                .text("Thanks a lot")
                .item(item1)
                .author(user1)
                .build();

        assertThat(entityManager.getId(comment), nullValue());
        repository.save(comment);
        assertThat(entityManager.getId(comment), notNullValue());
    }

    @Test
    void verifyCommentIsSameEntity() {
        Comment comment = Comment
                .builder()
                .text("Thanks a lot")
                .item(item1)
                .author(user1)
                .build();

        assertThat(entityManager.getId(comment), nullValue());
        Comment commentFromRepository = repository.save(comment);
        assertThat(entityManager.getId(comment), notNullValue());
        assertThat(comment.getText(), sameInstance(commentFromRepository.getText()));
        assertThat(comment.getItem(), sameInstance(commentFromRepository.getItem()));
        assertThat(comment.getAuthor(), sameInstance(commentFromRepository.getAuthor()));
        assertThat(comment.getCreated(), sameInstance(commentFromRepository.getCreated()));
    }

    @Test
    void verifyRepositorySaveSameQuantityOfEntities() {
        Integer expectedSize = 2;

        Comment comment1 = Comment
                .builder()
                .text("Thanks a lot")
                .item(item1)
                .author(user1)
                .build();

        Comment comment2 = Comment
                .builder()
                .text("Thank you very much")
                .item(item2)
                .author(user2)
                .build();

        assertThat(repository.findAll(), empty());
        assertThat(entityManager.getId(comment1), nullValue());
        assertThat(entityManager.getId(comment2), nullValue());
        repository.save(comment1);
        repository.save(comment2);
        assertThat(entityManager.getId(comment1), notNullValue());
        assertThat(entityManager.getId(comment2), notNullValue());
        assertThat(repository.findAll().size(), equalTo(expectedSize));
    }

    @Test
    void verifyFindAllByItemId() {
        Integer expectedSize = 2;
        Integer itemId = item1.getId();

        Comment comment1 = Comment
                .builder()
                .text("Thanks a lot")
                .item(item1)
                .author(user1)
                .build();

        Comment comment2 = Comment
                .builder()
                .text("Thank you very much")
                .item(item2)
                .author(user2)
                .build();

        Comment comment3 = Comment
                .builder()
                .text("Thank you very much")
                .item(item1)
                .author(user2)
                .build();

        assertThat(repository.findAll(), empty());
        assertThat(entityManager.getId(comment1), nullValue());
        assertThat(entityManager.getId(comment2), nullValue());
        assertThat(entityManager.getId(comment3), nullValue());
        repository.save(comment1);
        repository.save(comment2);
        repository.save(comment3);
        assertThat(entityManager.getId(comment1), notNullValue());
        assertThat(entityManager.getId(comment2), notNullValue());
        assertThat(entityManager.getId(comment3), notNullValue());
        List<CommentInfo> itemsFromRepository = repository.findAllByItemId(itemId);
        assertThat(itemsFromRepository.size(), equalTo(expectedSize));
        assertThat(comment1.getText(), equalTo(itemsFromRepository.get(0).getText()));
        assertThat(comment2.getText(), equalTo(itemsFromRepository.get(1).getText()));
        assertThat(comment1.getAuthor().getName(), equalTo(itemsFromRepository.get(0).getAuthorName()));
        assertThat(comment2.getAuthor().getName(), equalTo(itemsFromRepository.get(1).getAuthorName()));
    }

    private void resetIdColumns() {
        entityManager.getEntityManager().createNativeQuery("ALTER TABLE comments ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.getEntityManager().createNativeQuery("ALTER TABLE items ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.getEntityManager().createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }
}
