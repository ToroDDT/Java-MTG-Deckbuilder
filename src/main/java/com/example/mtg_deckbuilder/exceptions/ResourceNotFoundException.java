package com.example.mtg_deckbuilder.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Optional: Useful for cases where you don't need a custom message
    public ResourceNotFoundException() {
        super("The requested resource was not found.");
    }
}