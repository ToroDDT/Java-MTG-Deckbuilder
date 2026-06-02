package com.example.mtg_deckbuilder.dto.card;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CardFaceTest {

    @Test
    void hasSwitchableFacesWhenTwoFacesHaveArt() {
        Card card = Card.builder()
                .name("Delver of Secrets")
                .cardFaces(List.of(
                        face("Delver of Secrets", "https://example.com/front.png"),
                        face("Insectile Aberration", "https://example.com/back.png")
                ))
                .build();

        assertTrue(card.hasSwitchableFaces());
        assertEquals(
                List.of("https://example.com/front.png", "https://example.com/back.png"),
                card.faceArtworkUrls()
        );
        assertEquals(
                List.of("Delver of Secrets", "Insectile Aberration"),
                card.faceNames()
        );
    }

    @Test
    void hasSwitchableFacesFalseForSingleFace() {
        Card card = Card.builder()
                .name("Lightning Bolt")
                .cardFaces(List.of(face("Lightning Bolt", "https://example.com/bolt.png")))
                .build();

        assertFalse(card.hasSwitchableFaces());
    }

    private static CardFaces face(String name, String url) {
        ImageUris uris = new ImageUris();
        uris.setNormal(url);
        return CardFaces.builder()
                .name(name)
                .imageUris(uris)
                .build();
    }
}
