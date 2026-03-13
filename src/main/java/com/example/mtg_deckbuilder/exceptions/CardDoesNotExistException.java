package com.example.mtg_deckbuilder.exceptions;

import java.util.UUID;

public class CardDoesNotExistException extends RuntimeException{
    public CardDoesNotExistException() {
        super("Deck does not exist.");
    }

    public CardDoesNotExistException(String name) {
        super("Card with id \"" + name + "\" does not exist.");
    }
}
