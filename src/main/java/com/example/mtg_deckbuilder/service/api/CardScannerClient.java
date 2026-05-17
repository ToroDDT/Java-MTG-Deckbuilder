package com.example.mtg_deckbuilder.service.api;

public interface CardScannerClient {
    String scanCard(byte[] imageBytes, String filename, String contentType);
}
