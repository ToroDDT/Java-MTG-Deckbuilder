package com.example.mtg_deckbuilder.model;

import java.util.Map;

public record CardSearchParameters(
     String name,
     String cmc,
     String colors,
     String type
) {}