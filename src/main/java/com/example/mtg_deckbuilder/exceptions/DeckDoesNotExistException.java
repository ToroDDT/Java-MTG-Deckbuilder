package com.example.mtg_deckbuilder.exceptions;

import java.util.UUID;

public class DeckDoesNotExistException extends RuntimeException {
    public DeckDoesNotExistException() {
        super("Deck does not exist.");
    }

    public DeckDoesNotExistException(String deckId) {
        super("Deck with id \"" + deckId + "\" does not exist.");
    }
}
