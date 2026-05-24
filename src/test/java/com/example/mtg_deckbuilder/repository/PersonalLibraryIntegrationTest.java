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


}

