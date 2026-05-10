package com.example.mtg_deckbuilder.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CardDoesNotExistException extends RuntimeException{

    public CardDoesNotExistException() {
        super("Card does not exist.");
    }

    public CardDoesNotExistException(String message) {
        super(message);
    }
}
