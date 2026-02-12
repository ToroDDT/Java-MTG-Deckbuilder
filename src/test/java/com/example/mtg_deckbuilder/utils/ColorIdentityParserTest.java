package com.example.mtg_deckbuilder.utils;

import com.example.mtg_deckbuilder.model.ColorIdentity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class ColorIdentityParserTest {

    @ParameterizedTest
    @ValueSource(strings = {"White", "BLUE", "wHiTe"})
    public void testReturnsValidColorIdentity(String input) {
        Parser<ColorIdentity> colorIdentityParser = new ColorIdentityParser();
        assertEquals("white", colorIdentityParser.parse(input).name());
        colorIdentityParser.parse(input);
    }
}
