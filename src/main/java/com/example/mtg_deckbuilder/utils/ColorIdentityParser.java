package com.example.mtg_deckbuilder.utils;

import com.example.mtg_deckbuilder.exceptions.InvalidColorIdentityException;
import com.example.mtg_deckbuilder.model.ColorIdentity;
import jakarta.validation.constraints.NotNull;

public class ColorIdentityParser implements Parser<ColorIdentity> {

    @Override
    public ColorIdentity parse(@NotNull String input) throws InvalidColorIdentityException {
        ColorIdentity mtgColorIdentity = null;
        if (input.isBlank()) {
            throw new InvalidColorIdentityException(
                    String.format("'%s' is not a valid MTG color. Please use full names (e.g., White, Blue, Black, Red, Green) or standard symbols (W, U, B, R, G).", input), input
            );
        }

        for(ColorIdentity colorIdentity: ColorIdentity.values())
            if (colorIdentity.name().equalsIgnoreCase(input)) {
                mtgColorIdentity = colorIdentity;
            }
        return mtgColorIdentity;
    }
}
