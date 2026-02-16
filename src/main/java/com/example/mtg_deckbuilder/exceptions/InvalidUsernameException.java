package com.example.mtg_deckbuilder.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidUsernameException extends  RuntimeException{
    public InvalidUsernameException(String message) {
        super(message);
    }
}

