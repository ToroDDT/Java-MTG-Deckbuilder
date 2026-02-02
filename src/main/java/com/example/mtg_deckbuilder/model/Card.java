package com.example.mtg_deckbuilder.model;

import java.time.LocalDate;
import java.util.UUID;

public record Card(
        UUID id,
        UUID oracle_id,
        Integer[] multiverse_ids,
        Integer mtgo_id,
        Integer tcgplayer_id,
        Integer cardmarket_id,

        String name,
        String lang,
        LocalDate released_at,
        String uri,
        String scryfall_uri,
        String layout,

        String[] games,
        String[] artist_ids,
        boolean highres_image,
        String image_status,
        String mana_cost,
        Double cmc,
        String type_line,
        String oracle_text,

        String[] colors,
        String[] color_identity,
        String[] keywords,

        boolean reserved,
        boolean game_changer,
        boolean foil,
        String[] finishes,
        boolean nonfoil,
        boolean oversized,
        boolean promo,
        boolean reprint,
        boolean variation,

        UUID set_id,
        String set_code,
        String set_name,
        String set_type,
        String set_uri,
        String set_search_uri,
        String scryfall_set_uri,
        String rulings_uri,
        String prints_search_uri,
        String collector_number,
        boolean digital,
        String rarity,

        UUID card_back_id,
        String artist,
        UUID illustration_id,
        String border_color,
        String frame,
        String security_stamp,
        boolean full_art,
        boolean textless,
        boolean booster,
        boolean story_spotlight,

        Integer edhrec_rank,
        Integer penny_rank
) {}