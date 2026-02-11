package com.example.mtg_deckbuilder.utils;
import com.example.mtg_deckbuilder.exceptions.InvalidInequalityInput;
import jakarta.validation.constraints.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InequalityParser implements Parser<Inequality> {

    @Override
    public Inequality parse(@NotNull String input)  throws InvalidInequalityInput {
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
}
