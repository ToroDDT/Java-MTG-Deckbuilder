package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.dto.combo.CardCombos;
import com.example.mtg_deckbuilder.dto.combo.ComboVariant;
import com.example.mtg_deckbuilder.dto.combo.Combos;
import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.model.Deck;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.repository.api.ComboRepository;
import com.example.mtg_deckbuilder.repository.impl.ComboRepositoryImpl;
import com.example.mtg_deckbuilder.repository.impl.PersonalLibraryRepositoryImpl;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.api.BuilderService;
import com.example.mtg_deckbuilder.service.api.ComboService;
import com.example.mtg_deckbuilder.service.api.DeckService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class ComboServiceImpl implements ComboService {

    private final Executor apiExecutor = Executors.newFixedThreadPool(10); // Adjust based on API rate limits
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String BASE_URL = "https://backend.commanderspellbook.com/find-my-combos/";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final PersonalLibraryServiceImpl personalLibraryService;
    private final ComboRepository comboRespository;
    private static final Set<String> EXCLUDED_CARD_NAMES = Set.of(
            "Moritte of the Frost",
            "Stella Lee, Wild Card"
    );
    private final DeckService deckService;
    private final BuilderService builderService;

    @Autowired
    public ComboServiceImpl(PersonalLibraryServiceImpl personalLibraryService, ComboRepositoryImpl comboRespository, DeckService deckService, BuilderService builderService) {
        this.personalLibraryService = personalLibraryService;
        this.comboRespository = comboRespository;
        this.deckService = deckService;
        this.builderService = builderService;
    }

  // Define a dedicated thread pool for API calls (usually in a config class)

@Override
public void updateCombos(CustomUserDetails user) {
    var allCards = personalLibraryService.getCards(user.getId());
    List<Deck> decks= deckService.getDeckIds(user);
    System.out.println("Amount of Decks " + decks.size());

    // 1. Create the Library Task
    CompletableFuture<Void> libraryTask = CompletableFuture.runAsync(() -> {
        try {
            var searchedCombos = searchCombos(allCards);
            saveCombos(user, buildIncludedCombos(searchedCombos, "library"));
        } catch (Exception e) {
            System.out.println("Error processing library: " + e.getMessage());
        }
    }, apiExecutor);

    // 2. Create the Deck Tasks
    List<CompletableFuture<Void>> deckTasks = decks.stream()
        .map(deck -> CompletableFuture.runAsync(() -> {
            try {
                var cards = builderService.getCardsFromDeck(deck.id());
                var combos = searchCombos(cards);
                System.out.println("Deck name: " + deck.name());
                System.out.println("Deck combos: " + combos.getResults().getIncluded());
                var comboIncluded = buildIncludedCombos(combos, deck.name());
                saveCombos(user, comboIncluded);
            } catch (Exception e) {
                System.out.println("FAILED DECK: " + deck.name());
                e.printStackTrace();
        }
        }, apiExecutor))
        .toList();

    // 3. Combine everything into one list
    List<CompletableFuture<Void>> allTasks = new ArrayList<>(deckTasks);
    allTasks.add(libraryTask);

    // 4. Wait for EVERYTHING to finish
    CompletableFuture.allOf(allTasks.toArray(new CompletableFuture[0])).join();
}
    @Override
    public void saveCombos(CustomUserDetails user, CardCombos cardCombos) throws JsonProcessingException {
        comboRespository.saveCombos(user, cardCombos);
    }

    @Override
    public CardCombos getCombos(CustomUserDetails user) {
        return comboRespository.getCombos(user);
    }

    @Override
    public CardCombos getCombos(CustomUserDetails user, LibraryFilters filters) {
        CardCombos combos = comboRespository.getCombos(user);
        if (filters == null || !filters.hasSearchFilter()) {
            return combos;
        }

        Map<String, com.example.mtg_deckbuilder.dto.card.Card> cardsByName = personalLibraryService
                .getCards(user.getId())
                .stream()
                .filter(ownedCard -> ownedCard.getCard() != null && ownedCard.getCard().getName() != null)
                .collect(Collectors.toMap(
                        ownedCard -> normalize(ownedCard.getCard().getName()),
                        OwnedCard::getCard,
                        (first, ignored) -> first
                ));

        return filterCombos(combos, filters, cardsByName);
    }

    static CardCombos filterCombos(
            CardCombos combos,
            LibraryFilters filters,
            Map<String, com.example.mtg_deckbuilder.dto.card.Card> cardsByName
    ) {
        if (combos == null || combos.getCardCombinations() == null) {
            return emptyCombos();
        }

        List<List<String>> filteredCards = new ArrayList<>();
        List<String> filteredDescriptions = new ArrayList<>();
        List<List<String>> filteredImages = new ArrayList<>();

        List<String> descriptions = combos.getDescription() == null ? List.of() : combos.getDescription();
        List<List<String>> images = combos.getImages() == null ? List.of() : combos.getImages();

        for (int i = 0; i < combos.getCardCombinations().size(); i++) {
            List<String> cardNames = combos.getCardCombinations().get(i);
            String description = i < descriptions.size() ? descriptions.get(i) : "";
            List<String> comboImages = i < images.size() ? images.get(i) : List.of();

            if (matchesComboFilters(cardNames, description, filters, cardsByName)) {
                filteredCards.add(cardNames);
                filteredDescriptions.add(description);
                filteredImages.add(comboImages);
            }
        }

        return CardCombos.builder()
                .cardCombinations(filteredCards)
                .description(filteredDescriptions)
                .images(filteredImages)
                .location(combos.getLocation())
                .build();
    }

    private static boolean matchesComboFilters(
            List<String> cardNames,
            String description,
            LibraryFilters filters,
            Map<String, com.example.mtg_deckbuilder.dto.card.Card> cardsByName
    ) {
        List<String> safeCardNames = cardNames == null ? List.of() : cardNames;
        List<com.example.mtg_deckbuilder.dto.card.Card> comboCards = safeCardNames.stream()
                .map(name -> cardsByName.get(normalize(name)))
                .filter(Objects::nonNull)
                .toList();

        if (!matchesTextSearch(safeCardNames, description, filters)) {
            return false;
        }

        if (!matchesCardType(comboCards, filters.getCardType())) {
            return false;
        }

        if (!matchesSelectedColors(comboCards, filters.getSelectedColors())) {
            return false;
        }

        return matchesCmc(comboCards, filters);
    }

    private static boolean matchesTextSearch(List<String> cardNames, String description, LibraryFilters filters) {
        String normalizedDescription = normalize(description);
        String combinedCardNames = cardNames.stream()
                .map(ComboServiceImpl::normalize)
                .collect(Collectors.joining(" "));

        if (!matchesTokenizedSearch(combinedCardNames, normalizedDescription, filters.getCardName())) {
            return false;
        }

        return matchesTokenizedSearch(normalizedDescription, "", filters.getOracleTextSearch());
    }

    private static boolean matchesTokenizedSearch(String primaryText, String secondaryText, String rawQuery) {
        List<String> tokens = tokenize(rawQuery);
        if (tokens.isEmpty()) {
            return true;
        }

        return tokens.stream()
                .allMatch(token -> primaryText.contains(token) || secondaryText.contains(token));
    }

    private static List<String> tokenize(String value) {
        String normalized = normalize(value);
        if (normalized.isEmpty()) {
            return List.of();
        }

        return Arrays.stream(normalized.split("\\s+"))
                .filter(token -> !token.isBlank())
                .toList();
    }

    private static boolean matchesCardType(
            List<com.example.mtg_deckbuilder.dto.card.Card> comboCards,
            String cardType
    ) {
        if (cardType == null || cardType.isBlank() || "ALL".equalsIgnoreCase(cardType)) {
            return true;
        }

        String normalizedCardType = normalize(cardType);
        return comboCards.stream()
                .map(com.example.mtg_deckbuilder.dto.card.Card::getTypeLine)
                .filter(Objects::nonNull)
                .map(ComboServiceImpl::normalize)
                .anyMatch(typeLine -> typeLine.contains(normalizedCardType));
    }

    private static boolean matchesSelectedColors(
            List<com.example.mtg_deckbuilder.dto.card.Card> comboCards,
            List<String> selectedColors
    ) {
        if (selectedColors == null || selectedColors.isEmpty()) {
            return true;
        }

        Set<String> comboColors = comboCards.stream()
                .map(com.example.mtg_deckbuilder.dto.card.Card::getColorIdentity)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        Set<String> normalizedSelectedColors = selectedColors.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        return comboColors.containsAll(normalizedSelectedColors);
    }

    private static boolean matchesCmc(
            List<com.example.mtg_deckbuilder.dto.card.Card> comboCards,
            LibraryFilters filters
    ) {
        Integer minCmc = filters.getMinCMC();
        Integer maxCmc = filters.getMaxCMC();
        boolean hasMin = minCmc != null && minCmc > 0;
        boolean hasMax = maxCmc != null && maxCmc < 16;

        if (!hasMin && !hasMax) {
            return true;
        }

        return comboCards.stream()
                .map(com.example.mtg_deckbuilder.dto.card.Card::getCmc)
                .filter(Objects::nonNull)
                .anyMatch(cmc -> (!hasMin || cmc >= minCmc) && (!hasMax || cmc <= maxCmc));
    }

    private static CardCombos emptyCombos() {
        return CardCombos.builder()
                .cardCombinations(List.of())
                .description(List.of())
                .images(List.of())
                .build();
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private CardCombos buildIncludedCombos(Combos searchedCombos, String location) {
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

        return getCardCombos(filteredVariants, location);
    }


    static CardCombos getCardCombos(List<ComboVariant> filteredVariants, String location) {
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
                .location(location)
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
    static CardCombos getCardCombos(List<ComboVariant> filteredVariants) {
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

        return getCardCombos(variants);
    }

    public static Combos searchCombos(List<OwnedCard> cards) throws Exception {
        return getCombos(cards, objectMapper, BASE_URL, client);
    }

    static Combos getCombos(List<OwnedCard> cards, ObjectMapper objectMapper, String baseUrl, HttpClient client) throws java.io.IOException, InterruptedException {
        List<Map<String, Object>> mainBoard = cards.stream()
                .map(card -> {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("card", card.getCard().getName());
                    entry.put("quantity", 1);
                    return entry;
                })
                .collect(Collectors.toList());

        return getCombos(objectMapper, baseUrl, client, mainBoard);
    }

    static Combos getCombos(ObjectMapper objectMapper, String baseUrl, HttpClient client, List<Map<String, Object>> mainBoard) throws java.io.IOException, InterruptedException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("main", mainBoard);
        requestBody.put("commanders", List.of());

        String body = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
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

        return getCombos(objectMapper, BASE_URL, client, mainBoard);
    }
}
