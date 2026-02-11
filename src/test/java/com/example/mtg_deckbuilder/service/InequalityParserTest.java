package com.example.mtg_deckbuilder.service;

import com.example.mtg_deckbuilder.exceptions.InvalidInequalityInput;
import com.example.mtg_deckbuilder.utils.Inequality;
import com.example.mtg_deckbuilder.utils.Parser;
import com.example.mtg_deckbuilder.utils.InequalityParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringBootTest
public class InequalityParserTest {

    @ParameterizedTest
    @ValueSource(strings = {"> 4", "4",})
    public void ShouldReturnValidInequality(String input) {
        Parser<Inequality> queryParser = new InequalityParser();
        assertInstanceOf(Inequality.class, queryParser.parse(input));
        assertInstanceOf(Inequality.class, queryParser.parse(input));
  }
  @ParameterizedTest
  @ValueSource(strings = {"", " ", "ABCD", "> ABCD", ">", "> null"})
  public void shouldThrowInvalidInequalityInputException(String input) {
      Parser<Inequality> queryParser = new InequalityParser();
      assertThrows(InvalidInequalityInput.class, () -> queryParser.parse(input));
      assertThrows(InvalidInequalityInput.class, () -> queryParser.parse(input));
      assertThrows(InvalidInequalityInput.class, () -> queryParser.parse(input));
      assertThrows(InvalidInequalityInput.class, () -> queryParser.parse(input));
      assertThrows(InvalidInequalityInput.class, () -> queryParser.parse(input));
      assertThrows(InvalidInequalityInput.class, () -> queryParser.parse(input));
  }

  @Test
  public void shouldThrowNullPointerException() {
    Parser<Inequality> queryParser = new InequalityParser();
      assertThrows(NullPointerException.class, () -> queryParser.parse(null));
  }
}
