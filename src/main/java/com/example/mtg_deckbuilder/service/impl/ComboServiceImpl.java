package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.dto.card.Card;
import com.example.mtg_deckbuilder.dto.combo.*;
import com.example.mtg_deckbuilder.model.ComboDetailsRequest;
import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.model.Deck;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.model.SortOptions;
import com.example.mtg_deckbuilder.repository.api.ComboRepository;
import com.example.mtg_deckbuilder.repository.impl.ComboRepositoryImpl;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.api.BuilderService;
import com.example.mtg_deckbuilder.service.api.ComboService;
import com.example.mtg_deckbuilder.service.api.DeckService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import com.example.mtg_deckbuilder.views.api.ComboDetailViewModel;
import com.example.mtg_deckbuilder.views.impl.ComboDetailViewModelImpl;

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
    public List<String> getLocations(CustomUserDetails user) {
        return comboRespository.getLocations(user);
    }
    @Override
    public Optional<ComboDetailViewModel> getComboDetail(CustomUserDetails user, ComboDetailsRequest request) throws Exception {
        var combos = comboRespository.getCombos(user);

        List<OwnedCard> cards = getCardsBasedOnLocation(user, request.getLocation());
        if (cards.isEmpty()) {
            return Optional.empty();
        }

        List<ComboVariant> variants = searchCombos(cards)
                .getResults()
                .getIncluded()
                .stream()
                .filter(comboVariant -> comboVariant.getUses() != null)
                .filter(comboVariant -> comboVariant
                        .getUses()
                        .stream()
                        .filter(cardUse -> cardUse.getCard() != null)
                        .noneMatch(cardUse -> EXCLUDED_CARD_NAMES.contains(cardUse.getCard().getName())))
                .toList();

        return variants.stream()
                .filter(variant -> matchesVariantSelection(variant, request.getSelectedCardNames(), request.getDescription()))
                .findFirst()
                .map(variant -> toDetailViewModel(variant, request.getLocation()));
    }

    @Override
    public CardCombos getCombos(CustomUserDetails user, LibraryFilters filters) {
        CardCombos combos = comboRespository.getCombos(user);
        if (filters == null) {
            return combos;
        }

        boolean hasFilterCriteria = filters.hasSearchFilter();
        boolean hasSort = filters.getSortBy() != null
                && filters.getSortBy() != SortOptions.RECENT;
        if (!hasFilterCriteria && !hasSort) {
            return combos;
        }
Map<String, Card> library = personalLibraryService
        .getCards(user.getId())
        .stream()
        .filter(ownedCard -> ownedCard.getCard() != null && ownedCard.getCard().getName() != null)
        .collect(Collectors.toMap(
                OwnedCard::getName,
                OwnedCard::getCard,
                (first, ignored) -> first
        ));

List<List<String>> listOfCombos = combos.getItems().stream()
        .filter(item -> {
            List<String> cardNames = item.getCardCombination() == null ? List.of() : item.getCardCombination();

            // Resolve the actual Card objects using your library map
            List<Card> resolvedCards = cardNames.stream()
                    .map(library::get)
                    .filter(Objects::nonNull)
                    .toList();

            return matchesComboFilters(filters, item, resolvedCards);
        })
        .map(ComboItem::getCardCombination)
        .toList();
    }


    private static boolean matchesComboFilters(
            LibraryFilters filters,
            ComboItem item,
            List<Card> comboCards
    ) {
        List<String> names = item.getCardCombination() == null ? List.of() : item.getCardCombination();
        String description = item.getDescription() == null ? "" : item.getDescription();
        String location = item.getLocation() == null ? "" : item.getLocation();
        if (!matchesTextSearch(names, description, filters)) {
            return false;
        }

        if (!matchesCardType(comboCards, filters.getCardType())) {
            return false;
        }

        if (!matchesSelectedColors(comboCards, filters.getSelectedColors())) {
            return false;
        }

        if (!matchesLocation(location, filters.getLocation())) {
            return false;
        }

        if (!matchesCmc(comboCards, filters)) {
            return false;
        }

        return matchesPrice(comboCards, filters);
    }

    private static boolean matchesLocation(String comboLocation, String requestedLocation) {
        String normalizedRequestedLocation = normalize(requestedLocation);
        if (normalizedRequestedLocation.isEmpty() || "all".equals(normalizedRequestedLocation)) {
            return true;
        }

        return normalize(comboLocation).equals(normalizedRequestedLocation);
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

    private static boolean matchesVariantSelection(
            ComboVariant variant,
            List<String> selectedCardNames,
            String description
    ) {
        List<String> variantCardNames = variant.getUses() == null ? List.of() : variant.getUses().stream()
                .filter(cardUse -> cardUse.getCard() != null)
                .map(cardUse -> normalize(cardUse.getCard().getName()))
                .toList();

        List<String> normalizedSelectedNames = selectedCardNames.stream()
                .map(ComboServiceImpl::normalize)
                .toList();

        return variantCardNames.equals(normalizedSelectedNames)
                && normalize(variant.getDescription()).equals(normalize(description));
    }

    private static boolean matchesCardType(
            List<Card> comboCards,
            String cardType
    ) {
        if (cardType == null || cardType.isBlank() || "ALL".equalsIgnoreCase(cardType)) {
            return true;
        }

        String normalizedCardType = normalize(cardType);
        return comboCards.stream()
                .map(Card::getTypeLine)
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

        boolean matchesMin = comboCards.stream()
                .map(com.example.mtg_deckbuilder.dto.card.Card::getCmc)
                .filter(Objects::nonNull)
                .anyMatch(cmc -> !hasMin || cmc >= minCmc);

        if (!matchesMin) {
            return false;
        }

        return !hasMax || totalCmc(comboCards) <= maxCmc;
    }

    private static boolean matchesPrice(
            List<com.example.mtg_deckbuilder.dto.card.Card> comboCards,
            LibraryFilters filters
    ) {
        Double minPrice = filters.getMinPrice();
        Double maxPrice = filters.getMaxPrice();
        boolean hasMin = minPrice != null && minPrice > 0;
        boolean hasMax = maxPrice != null && maxPrice > 0;

        if (!hasMin && !hasMax) {
            return true;
        }

        return comboCards.stream()
                .map(ComboServiceImpl::usdPrice)
                .filter(Objects::nonNull)
                .anyMatch(price -> (!hasMin || price >= minPrice) && (!hasMax || price <= maxPrice));
    }

    private static Double usdPrice(com.example.mtg_deckbuilder.dto.card.Card card) {
        if (card == null || card.getPrices() == null) {
            return null;
        }
        return card.getPrices().getUsd();
    }

    private List<OwnedCard> getCardsBasedOnLocation(CustomUserDetails user, String location) {
        if (normalize(location).isEmpty() || "library".equals(normalize(location))) {
            return personalLibraryService.getCards(user.getId());
        }

        return deckService.getDeckIds(user).stream()
                .filter(deck -> normalize(deck.name()).equals(normalize(location)))
                .findFirst()
                .map(deck -> builderService.getCardsFromDeck(deck.id()))
                .orElse(List.of());
    }

    private static ComboDetailViewModel toDetailViewModel(ComboVariant variant, String location) {
        List<CardUse> uses = variant.getUses() == null ? List.of() : variant.getUses();
        List<String> cardNames = uses.stream()
                .filter(cardUse -> cardUse.getCard() != null)
                .map(cardUse -> cardUse.getCard().getName())
                .toList();
        List<String> cardImageUrls = uses.stream()
                .filter(cardUse -> cardUse.getCard() != null)
                .map(cardUse -> Optional.ofNullable(cardUse.getCard().getImageUriFrontLarge())
                        .orElse(cardUse.getCard().getImageUriFrontNormal()))
                .filter(Objects::nonNull)
                .toList();
        List<String> heroImageUrls = uses.stream()
                .filter(cardUse -> cardUse.getCard() != null)
                .map(cardUse -> Optional.ofNullable(cardUse.getCard().getImageUriFrontArtCrop())
                        .orElse(cardUse.getCard().getImageUriFrontNormal()))
                .filter(Objects::nonNull)
                .limit(2)
                .toList();

        String title = variant.getName() != null && !variant.getName().isBlank()
                ? variant.getName()
                : String.join(" | ", cardNames);

        List<String> initialStateLines = new ArrayList<>();
        uses.forEach(cardUse -> initialStateLines.addAll(cardUseStateLines(cardUse)));
        List<TemplateRequirement> requirements = variant.getRequires() == null ? List.of() : variant.getRequires();
        requirements.forEach(requirement -> initialStateLines.addAll(templateStateLines(requirement)));

        List<String> stepLines = splitIntoSentences(variant.getDescription());
        List<String> resultLines = variant.getProduces() == null ? List.of() : variant.getProduces().stream()
                .filter(featureProduced -> featureProduced.getFeature() != null && featureProduced.getFeature().getName() != null)
                .map(featureProduced -> formatProducedFeature(featureProduced.getQuantity(), featureProduced.getFeature().getName()))
                .toList();

        return new ComboDetailViewModelImpl(
                title,
                location,
                Optional.ofNullable(variant.getIdentity()).orElse(""),
                identityColors(variant.getIdentity()),
                variant.isSpoiler(),
                cardNames,
                cardImageUrls,
                heroImageUrls,
                initialStateLines,
                defaultText(variant.getNotablePrerequisites()),
                defaultText(variant.getManaNeeded()),
                stepLines,
                defaultText(variant.getNotes()),
                resultLines,
                variant.getPrices() == null ? null : variant.getPrices().getTcgplayer(),
                variant.getPrices() == null ? null : variant.getPrices().getCardkingdom(),
                legalityRows(variant)
        );
    }

    private static String resultSummary(ComboVariant variant) {
        if (variant.getProduces() == null || variant.getProduces().isEmpty()) {
            return "";
        }

        return variant.getProduces().stream()
                .filter(featureProduced -> featureProduced.getFeature() != null && featureProduced.getFeature().getName() != null)
                .map(featureProduced -> formatProducedFeature(featureProduced.getQuantity(), featureProduced.getFeature().getName()))
                .collect(Collectors.joining(" "));
    }

    private static List<String> identityColors(String identity) {
        String normalized = defaultText(identity);
        if (normalized.isEmpty()) {
            return List.of();
        }

        return normalized.chars()
                .mapToObj(character -> String.valueOf((char) character))
                .toList();
    }

    private static List<String> cardUseStateLines(CardUse cardUse) {
        if (cardUse.getCard() == null || cardUse.getCard().getName() == null) {
            return List.of();
        }

        List<String> lines = new ArrayList<>();
        List<String> zones = cardUse.getZoneLocations() == null || cardUse.getZoneLocations().isEmpty()
                ? List.of("battlefield")
                : cardUse.getZoneLocations();

        for (String zone : zones) {
            StringBuilder line = new StringBuilder(cardUse.getCard().getName())
                    .append(" in ")
                    .append(humanizeZone(zone));

            String state = switch (normalize(zone)) {
                case "battlefield" -> cardUse.getBattlefieldCardState();
                case "graveyard" -> cardUse.getGraveyardCardState();
                case "library" -> cardUse.getLibraryCardState();
                case "exile" -> cardUse.getExileCardState();
                default -> null;
            };

            if (cardUse.isMustBeCommander()) {
                line.append(" as your commander");
            }

            if (state != null && !state.isBlank()) {
                line.append(" (").append(state.trim()).append(")");
            }

            line.append(".");
            lines.add(line.toString());
        }

        return lines;
    }

    private static List<String> templateStateLines(TemplateRequirement requirement) {
        if (requirement.getTemplate() == null || requirement.getTemplate().getName() == null) {
            return List.of();
        }

        List<String> zones = requirement.getZoneLocations() == null || requirement.getZoneLocations().isEmpty()
                ? List.of("battlefield")
                : requirement.getZoneLocations();

        return zones.stream()
                .map(zone -> requirement.getQuantity() + " " + requirement.getTemplate().getName() + " in " + humanizeZone(zone) + ".")
                .toList();
    }

    private static String humanizeZone(String zone) {
        String normalizedZone = normalize(zone);
        return switch (normalizedZone) {
            case "battlefield" -> "the battlefield";
            case "graveyard" -> "the graveyard";
            case "library" -> "the library";
            case "exile" -> "exile";
            case "hand" -> "hand";
            case "commandzone", "command zone" -> "the command zone";
            default -> zone == null ? "the battlefield" : zone;
        };
    }

    private static List<String> splitIntoSentences(String text) {
        String normalized = defaultText(text);
        if (normalized.isBlank()) {
            return List.of();
        }

        return Arrays.stream(normalized.split("\\.\\s+"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(line -> line.endsWith(".") ? line : line + ".")
                .toList();
    }

    private static String formatProducedFeature(int quantity, String featureName) {
        return quantity > 1 ? quantity + "x " + featureName : featureName;
    }

    private static String defaultText(String value) {
        return value == null ? "" : value.trim();
    }

    private static List<ComboDetailViewModelImpl.LegalityRowImpl> legalityRows(ComboVariant variant) {
        if (variant.getLegalities() == null) {
            return List.of();
        }

        return List.of(
                new ComboDetailViewModelImpl.LegalityRowImpl("Commander", variant.getLegalities().isCommander()),
                new ComboDetailViewModelImpl.LegalityRowImpl("Pauper Commander", variant.getLegalities().isPauperCommander()),
                new ComboDetailViewModelImpl.LegalityRowImpl("Pauper Commander in 99", variant.getLegalities().isPauperCommanderMain()),
                new ComboDetailViewModelImpl.LegalityRowImpl("Oathbreaker", variant.getLegalities().isOathbreaker()),
                new ComboDetailViewModelImpl.LegalityRowImpl("PreDH", variant.getLegalities().isPredh())
        );
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
                .results(filteredVariants
                        .stream()
                        .map(ComboServiceImpl::resultSummary)
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
                .variants(filteredVariants)
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
                .results(filteredVariants
                        .stream()
                        .map(ComboServiceImpl::resultSummary)
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
                .variants(filteredVariants)
                .build();
    }

    public static Combos searchCombos(List<OwnedCard> cards) throws Exception {
        return getCombos(cards);
    }

    static Combos getCombos(List<OwnedCard> cards) throws java.io.IOException, InterruptedException {

        List<Map<String, Object>> mainBoard = cards.stream()
                .map(card -> {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("card", card.getCard().getName());
                    entry.put("quantity", 1);
                    return entry;
                })
                .collect(Collectors.toList());

        return getCombos(ComboServiceImpl.objectMapper, ComboServiceImpl.BASE_URL, ComboServiceImpl.client, mainBoard);
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
