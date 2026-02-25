package com.example.mtg_deckbuilder.utils;
import com.example.mtg_deckbuilder.exceptions.InvalidColorIdentityException;
import com.example.mtg_deckbuilder.exceptions.InvalidInequalityInput;
import com.example.mtg_deckbuilder.model.ColorIdentity;
import jakarta.validation.constraints.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchParametersParser {

    public static Inequality parseInequality(@NotNull String input)  throws InvalidInequalityInput {
        if (input.isBlank()){
            throw new InvalidInequalityInput("Input must not be blank", input);
        }
        Pattern pattern = Pattern.compile("^(>=|<=|>|<|=)?(\\d+(\\.\\d+)?)$");
        Matcher matcher = pattern.matcher(input.replaceAll("\\s+", ""));

        if (!matcher.matches()) {
            throw new InvalidInequalityInput("Invalid Format: must be e.g (operator value) ", input);
        }

            String operation = matcher.group(1) != null ? matcher.group(1) : "=";
            Integer value = Integer.parseInt(matcher.group(2));
            return new Inequality(operation, value);
    }

    public static ColorIdentity parseColorIdentity(@NotNull String input) throws InvalidColorIdentityException {
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
