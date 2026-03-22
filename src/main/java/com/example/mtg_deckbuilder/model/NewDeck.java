package com.example.mtg_deckbuilder.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor // Optional: helpful for testing
public class NewDeck {
    private UUID id;
    @NotBlank
    private String name;
    private UUID userId;
    private String format;
    private String commander = "None";
    private String visibility = "public";
    private String folder = "home";
    private String description = "";
    private String colorIdentity;
    private Integer bracket;
    private LocalDate lastUpdate;
    private String url;
    private String image;
}