package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.dto.CardCombos;
import com.example.mtg_deckbuilder.dto.ComboVariant;
import com.example.mtg_deckbuilder.dto.Combos;
import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.api.ComboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ComboServiceImpl implements ComboService {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String BASE_URL = "https://backend.commanderspellbook.com/find-my-combos/";
    private static final String SEARCH_COMBO_URL= "https://backend.commanderspellbook.com/variants/?format=json&q=card%3D%22Thassa%27s+Oracle%22";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final PersonalLibraryServiceImpl personalLibraryService;
    private static final Set<String> EXCLUDED_CARD_NAMES = Set.of(
            "Moritte of the Frost",
            "Stella Lee, Wild Card"
    );

    @Autowired
    public ComboServiceImpl(PersonalLibraryServiceImpl personalLibraryService ) {
        this.personalLibraryService = personalLibraryService;
    }

    @Override
    public CardCombos findCombos(CustomUserDetails userId, LibraryFilters libraryFilters) throws Exception {
        var cards = personalLibraryService.getCards(userId.getId());

        if (libraryFilters.getCardName() == null) {
            var searchedCombos = searchCombos(cards);
            return buildIncludedCombos(searchedCombos);
        }
        else {
            var searchedCombos = searchCombos(libraryFilters.getCardName());
            return buildAlmostIncludedCombos(searchedCombos);
        }
    }

    private CardCombos buildIncludedCombos(Combos searchedCombos) {
        List<ComboVariant> filteredVariants = searchedCombos
                .getResults()
                .getIncluded()
                .stream()
                .filter(comboVariant -> comboVariant
                        .getUses()
                        .stream()
                        .filter(cardUse -> cardUse.getCard() != null)
                        .noneMatch(cardUse -> EXCLUDED_CARD_NAMES.contains(cardUse.getCard().getName()))
                )
                .toList();

        return CardCombos.builder()
                .cardCombinations(filteredVariants
                        .stream()
                        .map(comboVariant -> comboVariant
                                .getUses()
                                .stream()
                                .filter(cardUse -> cardUse.getCard() != null)
                                .map(cardUse -> cardUse.getCard().getName())
                                .toList())
                        .toList())
                .description(filteredVariants
                        .stream()
                        .map(ComboVariant::getDescription)
                        .toList())
                .images(filteredVariants
                        .stream()
                        .map(comboVariant -> comboVariant
                                .getUses()
                                .stream()
                                .filter(cardUse -> cardUse.getCard() != null)
                                .map(cardUse -> cardUse.getCard().getImageUriFrontNormal())
                                .toList())
                        .toList())
                .build();
    }
    private CardCombos buildAlmostIncludedCombos(Combos searchedCombos) {
        List<ComboVariant> variants = searchedCombos.getResults().getAlmostIncludedByAddingColors()
                .stream()
                .filter(comboVariant -> comboVariant
                        .getUses()
                        .stream()
                        .filter(cardUse -> cardUse.getCard() != null)
                        .noneMatch(cardUse -> EXCLUDED_CARD_NAMES.contains(cardUse.getCard().getName()))
                )
                .toList();

        return CardCombos.builder()
                .cardCombinations(variants
                        .stream()
                        .map(comboVariant -> comboVariant
                                .getUses()
                                .stream()
                                .filter(cardUse -> cardUse.getCard() != null)
                                .map(cardUse -> cardUse.getCard().getName())
                                .toList()
                        )
                        .toList())
                .description(variants
                        .stream()
                        .map(ComboVariant::getDescription)
                        .toList())
                .images(variants
                        .stream()
                        .map(comboVariant -> comboVariant
                                .getUses()
                                .stream()
                                .filter(cardUse -> cardUse.getCard() != null)
                                .map(cardUse -> cardUse.getCard().getImageUriFrontNormal())
                                .toList())
                        .toList())
                .build();
    }

    public static Combos searchCombos(List<OwnedCard> cards) throws Exception {
        List<Map<String, Object>> mainBoard = cards.stream()
                .map(card -> {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("card", card.getCard().getName());
                    entry.put("quantity", 1);
                    return entry;
                })
                .collect(Collectors.toList());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("main", mainBoard);
        requestBody.put("commanders", List.of());

        String body = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("API error: HTTP " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), Combos.class);
    }
    public static Combos searchCombos(String card) throws Exception {
        List<Map<String, Object>> mainBoard = List.of(Map.of("card", card, "quantity", 1));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("main", mainBoard);
        requestBody.put("commanders", List.of());

        String body = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("API error: HTTP " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), Combos.class);
    }
}

