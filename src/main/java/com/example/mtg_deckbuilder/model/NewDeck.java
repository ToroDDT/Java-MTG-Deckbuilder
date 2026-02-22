package com.example.mtg_deckbuilder.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor // Optional: helpful for testing
public class NewDeck {
    private UUID id;
    @NotBlank
    private String name;
    @NotBlank
    private String format; // must have format specified
    private String commander = "None";
    private String visibility = "public"; // must be public or private
    private String folder = "home"; // default folder is home
    private String description = "";
    private List<String> colorIdentity;
    private Integer bracket;
    private LocalDate lastUpdate;
}