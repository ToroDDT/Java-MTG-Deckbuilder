package com.example.mtg_deckbuilder.repository;

import com.example.mtg_deckbuilder.exceptions.CardDoesNotExistException;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.repository.api.PersonalLibraryRepository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.api.PersonalLibraryService;
import com.example.mtg_deckbuilder.views.api.PersonalLibraryStats;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.example.mtg_deckbuilder.service.impl.PersonalLibraryServiceImplTest.testUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PersonalLibraryIntegrationTest {

    @Autowired
    private PersonalLibraryService service; // Use the interface type directly
    @Autowired
    private PersonalLibraryRepository repository; // Use the interface type directly

    @Autowired
    private JdbcClient testJdbcClient;

    @BeforeEach
    public void setUp() {
        CustomUserDetails user = testUser();
        UUID cardId = UUID.randomUUID();
        String image = "https://example.com/image.jpg";
        assert user.getPassword() != null;
        testJdbcClient.sql("INSERT  INTO  users (id, username, email, password) VALUES (?,?,?,?)")
                        .params(user.getId(), user.getUsername(),"deltoro1999@icloud.com", user.getPassword())
                .update();

        testJdbcClient.sql("INSERT INTO cards (id, name, type_line) VALUES (?, ?, ?)")
                .params(cardId, "Black Lotus", "Artifact")
                .update();

        testJdbcClient.sql("INSERT INTO personal_collection_library (id, user_id, card_id, image ) VALUES (?, ?, ?, ?)")
                .params(UUID.randomUUID(), user.getId(), cardId, image)
                .update();
    }

    @Nested
    class libraryInfoTests{
        @Test
        public void testReturnsNoNullValues() {
            CustomUserDetails user = testUser();
            PersonalLibraryStats libraryInfo = service.getLibraryInfo(user);

            assertNotNull(libraryInfo);
            assertNotNull(libraryInfo.totalCards());
            assertNotNull(libraryInfo.totalValue());
            assertNotNull(libraryInfo.colorIdentityAmounts());
        }
    }
}

