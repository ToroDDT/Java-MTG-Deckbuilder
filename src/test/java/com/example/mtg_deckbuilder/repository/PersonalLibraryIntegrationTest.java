package com.example.mtg_deckbuilder.repository;

import com.example.mtg_deckbuilder.exceptions.CardDoesNotExistException;
import com.example.mtg_deckbuilder.repository.api.PersonalLibraryRepository;
import com.example.mtg_deckbuilder.repository.impl.PersonalLibraryRepositoryImpl;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.example.mtg_deckbuilder.service.impl.PersonalLibraryServiceImplTest.testUser;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class PersonalLibraryIntegrationTest {

    @Autowired
    private PersonalLibraryRepository repository;
    @Autowired
    private PersonalLibraryRepositoryImpl personalLibraryRepositoryImpl;
    @Autowired
    private JdbcClient testJdbcClient;

    @Test
    void deleteCard_WhenCardIdDoesNotExistInDB_ThrowsException() {
        CustomUserDetails user = testUser();
        UUID fakeCardId = UUID.randomUUID();

        assertThrows(CardDoesNotExistException.class, () -> {
            repository.deleteCard(user, fakeCardId);
        });
    }

  @Test
void getUpdatedCardTags_WhenCardHasTags_ReturnsFlattenedList() {
    // Arrange
    CustomUserDetails user = testUser();
    UUID personalCardId = UUID.randomUUID();

    // 1. Corrected user insert: Pass the clean sub-fields, not the entire 'user' object!
    testJdbcClient.sql("""
        INSERT INTO users (id, username, email ) 
        VALUES (:id, :userName, :email)
    """)
            .param("id", user.getId())                 // Correct: Pass the User's ID
            .param("userName", user.getUsername())     // Correct: Pass the actual username string
            .param("email", "testuser@example.com")    // Correct: Pass an email string
            .update();

    // 2. Insert into the personal collection library
    testJdbcClient.sql("""
        INSERT INTO personal_collection_library (id, user_id, card_id, image, tags) 
        VALUES (:id, :userId, :cardId, :image, ARRAY['Commander', 'Foil', 'Mythic'])
    """)
            .param("id", personalCardId)
            .param("userId", user.getId())
            .param("cardId", UUID.randomUUID())        // A separate random UUID for the MTG card template
            .param("image", "")
            .update();

    // Act
    List<String> actualTags = repository.findCards(user.getId()).getLast().getTags();

    // Assert
    assertThat(actualTags)
            .hasSize(3)
            .containsExactlyInAnyOrder("Commander", "Foil", "Mythic");

    }
}

