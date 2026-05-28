package com.example.mtg_deckbuilder.dto.card;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record CardFaces(
    String name,
    String artist,
    List<String> colors,
    String object,
    @JsonProperty("artist_id") UUID artistId,
    @JsonProperty("mana_cost") String manaCost,
    @JsonProperty("type_line") String typeLine,
    @JsonProperty("image_uris") ImageUris imageUris,
    @JsonProperty("oracle_text") String oracleText,
    @JsonProperty("illustration_id") UUID illustrationId
) {

    /** Best art URL from this face's {@link #imageUris}, same priority as {@link Card#artworkUrl()}. */
    public String artworkUrl() {
        return imageUris != null ? imageUris.firstNonBlankArtUrl() : null;
    }
}