package com.example.mtg_deckbuilder.dto.card;
import java.util.List;
import java.util.UUID;

public record CardFaces(
    String name,
    String artist,
    List<String> colors,
    String object,
    UUID artistId,
    String manaCost,
    String typeLine,
    ImageUris imageUris,
    String oracleText,
    UUID illustrationId
) {}