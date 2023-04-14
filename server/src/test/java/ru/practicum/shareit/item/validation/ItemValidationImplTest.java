package ru.practicum.shareit.item.validation;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.ItemNotFoundException;
import ru.practicum.exceptions.WrongOwnerOfItemException;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.item.validation.ItemValidation;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
public class ItemValidationImplTest {
    private final ItemValidation validation;
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private Item item;

    @BeforeEach
    void setUp() {
        User userToSave = makeUser("John", "email@mail.com");
        User user = userRepository.save(userToSave);

        Item itemToSave = makeItem("Item1", "For something", true, user);
        item = repository.save(itemToSave);
    }

    @Test
    void verifyCheckOwnerOfItem() {
        assertThat(repository.findAll(), notNullValue());

        User someUser = makeUser("Jimmy", "mymail@email.com");
        someUser.setId(100);

        final WrongOwnerOfItemException exception = assertThrows(
                WrongOwnerOfItemException.class, () -> validation.checkOwnerOfItem(someUser.getId(), item.getId()));

        assertThat("The wrong owner of ru.practicum.item", is(exception.getMessage()));
    }

    @Test
    void verifyIfItemExistException() {
        assertThat(repository.findAll(), notNullValue());

        Item item = makeItem("Some ru.practicum.item", "For somebody", true, User.builder().build());
        item.setId(100);

        final ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class, () -> validation.checkIfItemExist(item.getId()));

        assertThat("The ru.practicum.item with the id doesn't exists", is(exception.getMessage()));
    }

    private Item makeItem(String name, String description, Boolean available, User owner) {
        Item.ItemBuilder builder = Item.builder();

        builder.name(name);
        builder.description(description);
        builder.available(available);
        builder.owner(owner);

        return builder.build();
    }

    private User makeUser(String name, String email) {
        User.UserBuilder builder = User.builder();

        builder.name(name);
        builder.email(email);

        return builder.build();
    }
}
