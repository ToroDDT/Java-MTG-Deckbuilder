package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.dto.card.Card;
import com.example.mtg_deckbuilder.repository.impl.CardRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.annotation.Cacheable;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepositoryImpl scryfallRepository;

    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    void findLegalCommandersReturnsRepositoryResult() {
        List<String> commanders = List.of("Atraxa, Praetors' Voice", "Krenko, Mob Boss");

        when(scryfallRepository.findLegalCommanderCards()).thenReturn(commanders);

        List<String> result = cardService.findLegalCommanders();

        assertSame(commanders, result);
        verify(scryfallRepository).findLegalCommanderCards();
    }

    @Test
    void findLegalCommandersReturnsEmptyListWhenRepositoryFindsNone() {
        List<String> commanders = List.of();

        when(scryfallRepository.findLegalCommanderCards()).thenReturn(commanders);

        List<String> result = cardService.findLegalCommanders();

        assertSame(commanders, result);
        verify(scryfallRepository).findLegalCommanderCards();
    }

    @Test
    void findLegalCommandersPropagatesRepositoryException() {
        when(scryfallRepository.findLegalCommanderCards())
                .thenThrow(new RuntimeException("Database unavailable"));

        assertThrows(RuntimeException.class, () -> cardService.findLegalCommanders());

        verify(scryfallRepository).findLegalCommanderCards();
    }

    @Test
    void findLegalCommandersIsCacheable() throws NoSuchMethodException {
        Method method = CardServiceImpl.class.getMethod("findLegalCommanders");

        Cacheable cacheable = method.getAnnotation(Cacheable.class);

        assertArrayEquals(new String[]{"commanders"}, cacheable.value());
    }

    @Test
    void findByNameReturnsCardWhenRepositoryFindsMatch() {
        Card card = Card.builder().build();
        card.setName("Sol Ring");

        Optional<Card> expected = Optional.of(card);

        when(scryfallRepository.findByName("Sol Ring")).thenReturn(expected);

        Optional<Card> result = cardService.findByName("Sol Ring");

        assertSame(expected, result);
        verify(scryfallRepository).findByName("Sol Ring");
    }

    @Test
    void findByNameReturnsEmptyOptionalWhenRepositoryFindsNoMatch() {
        when(scryfallRepository.findByName("Missing Card")).thenReturn(Optional.empty());

        Optional<Card> result = cardService.findByName("Missing Card");

        assertTrue(result.isEmpty());
        verify(scryfallRepository).findByName("Missing Card");
    }

    @Test
    void findByNameThrowsWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> cardService.findByName(null));

        verifyNoInteractions(scryfallRepository);
    }

    @Test
    void findByNamePassesNullNameToRepository() {
        when(scryfallRepository.findByName(null)).thenReturn(Optional.empty());

        Optional<Card> result = cardService.findByName(null);

        assertTrue(result.isEmpty());
        verify(scryfallRepository).findByName(null);
    }

    @Test
    void findByNamePropagatesRepositoryException() {
        when(scryfallRepository.findByName("Black Lotus"))
                .thenThrow(new RuntimeException("Query failed"));

        assertThrows(RuntimeException.class, () -> cardService.findByName("Black Lotus"));

        verify(scryfallRepository).findByName("Black Lotus");
    }

    @Test
    void findByNameContainingReturnsRepositoryResult() {
        Card first = Card.builder().build();
        first.setName("Sol Ring");

        Card second = Card.builder().build();
        second.setName("Sol Talisman");

        List<Card> expected = List.of(first, second);

        when(scryfallRepository.findByCardsBySubstring("Sol")).thenReturn(expected);

        List<Card> result = cardService.findByNameContaining("Sol");

        assertSame(expected, result);
        verify(scryfallRepository).findByCardsBySubstring("Sol");
    }

    @Test
    void findByNameContainingReturnsEmptyListWhenNoCardsMatch() {
        List<Card> expected = List.of();

        when(scryfallRepository.findByCardsBySubstring("DefinitelyMissing")).thenReturn(expected);

        List<Card> result = cardService.findByNameContaining("DefinitelyMissing");

        assertSame(expected, result);
        verify(scryfallRepository).findByCardsBySubstring("DefinitelyMissing");
    }

    @Test
    void findByNameContainingPassesBlankSearchStringToRepository() {
        List<Card> expected = List.of();

        when(scryfallRepository.findByCardsBySubstring("")).thenReturn(expected);

        List<Card> result = cardService.findByNameContaining("");

        assertSame(expected, result);
        verify(scryfallRepository).findByCardsBySubstring("");
    }



    @Test
    void findByNameContainingThrowsWhenSearchTextIsNull() {
        assertThrows(IllegalArgumentException.class, () -> cardService.findByNameContaining(null));

        verifyNoInteractions(scryfallRepository);
    }

    @Test
    void findByNameContainingPassesNullSearchStringToRepository() {
        List<Card> expected = List.of();

        when(scryfallRepository.findByCardsBySubstring(null)).thenReturn(expected);

        List<Card> result = cardService.findByNameContaining(null);

        assertSame(expected, result);
        verify(scryfallRepository).findByCardsBySubstring(null);
    }

    @Test
    void findByNameContainingPropagatesRepositoryException() {
        when(scryfallRepository.findByCardsBySubstring("Sol"))
                .thenThrow(new RuntimeException("Substring query failed"));

        assertThrows(RuntimeException.class, () -> cardService.findByNameContaining("Sol"));

        verify(scryfallRepository).findByCardsBySubstring("Sol");
    }
}