package com.example.mtg_deckbuilder.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRegistrationFormException extends RuntimeException{
    public InvalidRegistrationFormException(String message) {
        super(message);
    }
}


