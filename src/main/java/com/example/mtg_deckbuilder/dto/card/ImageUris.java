package com.example.mtg_deckbuilder.dto.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ImageUris {
    private String small;
    private String normal;
    private String large;
    private String png;
    @JsonProperty("border_crop")
    private String borderCrop;
    @JsonProperty("art_crop")
    private String artCrop;

    public ImageUris() {
    }

    /** First non-blank URL Scryfall provides; avoids null {@code border_crop} on some layouts. */
    public String firstNonBlankArtUrl() {
        for (String candidate : List.of(borderCrop, normal, large, png, small, artCrop)) {
            if (candidate != null && !candidate.isBlank()) {
                return candidate.trim();
            }
        }
        return null;
    }

}

