package com.example.mtg_deckbuilder.repository;

import com.example.mtg_deckbuilder.exceptions.CardDoesNotExistException;
import com.example.mtg_deckbuilder.exceptions.UserDoesNotExistsException;
import com.example.mtg_deckbuilder.repository.api.PersonalLibraryRepository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.example.mtg_deckbuilder.service.impl.PersonalLibraryServiceImplTest.testUser;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional // Rolls back the delete after the test runs
class PersonalLibraryIntegrationTest {

    @Autowired
    private PersonalLibraryRepository repository;

    @Test
    void deleteCard_WhenCardIdDoesNotExistInDB_ThrowsException() {
        CustomUserDetails user = testUser();
        UUID fakeCardId = UUID.randomUUID(); // Not in database

        assertThrows(CardDoesNotExistException.class, () -> {
            repository.deleteCard(user, fakeCardId);
        });
    }
    @Test
    void deleteCardWhenUserDoesNotExistThrowsException() {
        CustomUserDetails user = testUser();
        UUID fakeCardId = UUID.randomUUID();

        assertThrows(UserDoesNotExistsException.class, () -> {
            repository.deleteCard(user, fakeCardId);
        });
    }
}
