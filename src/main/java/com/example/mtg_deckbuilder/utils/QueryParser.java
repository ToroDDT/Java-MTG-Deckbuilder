package com.example.mtg_deckbuilder.utils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryParser implements InequalityMatcher {

    public Inequality parseInequality(String input) {
        if (input == null || input.isBlank()) return null;

        Pattern pattern = Pattern.compile("^(>=|<=|>|<|=)?(\\d+(\\.\\d+)?)");
        Matcher matcher = pattern.matcher(input.replaceAll("\\s+", ""));

        if (matcher.find()) {
            String op = matcher.group(1) != null ? matcher.group(1) : "=";
            Double val = Double.parseDouble(matcher.group(2));
            return new Inequality(op, val);
        }
        return null;
    }
}
