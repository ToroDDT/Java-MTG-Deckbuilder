package com.example.mtg_deckbuilder.repository;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class CardLibrary {

    private final CardNameRepository repository;
    @Getter
    private Map<String, String> cardLibraryMap;
    @Getter
    private List<String> cardLibrary;

    @Autowired
    public CardLibrary(CardNameRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() throws IOException {
        this.cardLibrary = repository.readFile();
        this.cardLibraryMap = repository
                .convertStringArrayToMap(this.cardLibrary);
    }
}