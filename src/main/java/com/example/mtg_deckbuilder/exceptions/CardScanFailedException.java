package com.example.mtg_deckbuilder.exceptions;

public class CardScanFailedException extends RuntimeException {
    public CardScanFailedException(String message) {
        super(message);
    }

    public CardScanFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
