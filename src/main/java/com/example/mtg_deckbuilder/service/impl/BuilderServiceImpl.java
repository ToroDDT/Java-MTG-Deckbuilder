package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.dto.card.Card;
import com.example.mtg_deckbuilder.model.ColorIdentity;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.repository.api.BuilderRepository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.api.BuilderService;
import com.example.mtg_deckbuilder.service.api.CardService;
import com.example.mtg_deckbuilder.service.api.DeckService;
import com.example.mtg_deckbuilder.service.api.PersonalLibraryService;
import com.example.mtg_deckbuilder.utils.DeckOptimizerV2;
import com.example.mtg_deckbuilder.views.BuilderCardHoverView;
import com.example.mtg_deckbuilder.views.BuilderCardQueryView;
import com.example.mtg_deckbuilder.views.BuilderDeckCardRecord;
import com.example.mtg_deckbuilder.views.BuilderDeckLayoutView;
import com.example.mtg_deckbuilder.views.BuilderDeckSection;
import com.example.mtg_deckbuilder.views.BuilderMainView;
import com.example.mtg_deckbuilder.views.BuilderOwnedLibraryView;
import com.example.mtg_deckbuilder.views.BuilderViewModel;
import com.example.mtg_deckbuilder.views.DeckLayoutExtrasFlags;
import lombok.Builder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class BuilderServiceImpl implements BuilderService {

    private static final Set<String> ALLOWED_VIEW_STYLES = Set.of(
            "text",
            "condensed",
            "visual-grid",
            "visual-stacks",
            "visual-split",
            "visual-spoiler"
    );

    private static final Set<String> ALLOWED_GROUP_BY = Set.of(
            "type",
            "subtype",
            "type-tag",
            "rarity",
            "color",
            "color-identity",
            "mana-value",
            "set",
            "artist",
            "none");

    private static final Set<String> ALLOWED_SORT_BY =
            Set.of("name", "mana-value", "price", "rarity");

    private static final Set<String> ALLOWED_EXTRAS = Set.of("mana-cost", "price", "set-symbol");

    private final BuilderRepository builderRepository;
    private final DeckService deckService;
    private final CardService cardService;
    private final PersonalLibraryService personalLibraryService;

    public BuilderServiceImpl(BuilderRepository builderRepository,
                              DeckService deckService,
                              CardService cardService,
                              PersonalLibraryService personalLibraryService) {
        this.builderRepository = builderRepository;
        this.deckService = deckService;
        this.cardService = cardService;
        this.personalLibraryService = personalLibraryService;
    }

    @Override
    public BuilderMainView getMainView(String deckId, CustomUserDetails user) {
        return BuilderMainView.from(getBuilderView(deckId), user);
    }

    @Override
    public BuilderDeckLayoutView getDeckLayoutView(String deckId,
                                                   String viewStyle,
                                                   String groupBy,
                                                   String sortBy,
                                                   List<String> extrasParams) {
        String normalizedStyle = ALLOWED_VIEW_STYLES.contains(viewStyle) ? viewStyle : "text";
        String normalizedGroup = ALLOWED_GROUP_BY.contains(groupBy) ? groupBy : "type";
        String normalizedSort = ALLOWED_SORT_BY.contains(sortBy) ? sortBy : "name";

        Set<String> extras = new HashSet<>();
        if (extrasParams != null) {
            for (String chunk : extrasParams) {
                if (chunk != null && ALLOWED_EXTRAS.contains(chunk)) {
                    extras.add(chunk);
                }
            }
        }

        return BuilderDeckLayoutView.of(
                getBuilderView(deckId),
                normalizedStyle,
                DeckLayoutExtrasFlags.from(extras),
                buildDeckSections(deckId, normalizedGroup, normalizedSort));
   }

    @Override
    public List<BuilderDeckSection> buildDeckSections(String deckId, String groupBy, String sortBy) {
        List<BuilderDeckCardRecord> cards = builderRepository.getAllCardsForUser(deckId);
        return BuilderDeckLayoutComposer.build(groupBy, sortBy, cards);
    }

    @Override
    public BuilderCardQueryView getCardQueryView(String query) {
        String normalizedQuery = BuilderCardQueryView.normalize(query);
        return BuilderCardQueryView.of(normalizedQuery, personalLibraryService.getCardQuery(normalizedQuery));
    }

    @Override
    public BuilderOwnedLibraryView getOwnedLibraryView(String deckId, CustomUserDetails user) {
        return BuilderOwnedLibraryView.of(
                personalLibraryService.buildPersonalLibraryViewModel(user),
                deckId,
                getBuilderView(deckId).deckName());
    }

    @Override
    public Optional<BuilderCardHoverView> getDeckEntryHover(CustomUserDetails user, UUID deckId, UUID deckCardEntryId) {
        return builderRepository.findDeckEntryHover(user.getId(), deckId, deckCardEntryId);
    }


    @Override
    public List<OwnedCard> getCardsFromDeck(UUID deckId) {
        return builderRepository.getAllCardsFromDeck(deckId);
    }

    @Override
    public List<String> getRandomizedCards(UUID deckId) {
        var cards = builderRepository.getAllCardsFromDeck(deckId);
        Collections.shuffle(cards);
        // <img src> must be a plain https URL, not raw image_uris JSON (browser treats "{" as relative path).
        return cards.stream()
                .map(OwnedCard::getCard)
                .map(Card::artworkUrl)
                .filter(Objects::nonNull)
                .filter(img -> !img.isBlank())
                .limit(7)
                .toList();
    }

    @Override
    public void removeDeckEntry(CustomUserDetails user, UUID deckId, UUID deckCardEntryId) {
        deckService.removeDeckEntry(user, deckId, deckCardEntryId);
    }

    @Override
    public void addCardToDeck(CustomUserDetails user, String deckId, String cardName) {
        deckService.addCard(user, deckId, cardName);
    }

    @Override
    public BuilderViewModel getBuilderView(String deckId ) {

        var cards = builderRepository.getAllCardsForUser(deckId);

        if (cards.isEmpty()) {
            return BuilderViewModel.empty(deckId);
        }

        var deckName = cards.getLast().deckName();
        var deckImage = cards.getLast().deckImage();
        var colorProduction = getColorProduction(cards.stream()
                .filter(card -> containsType(card, "Land"))
                .toList());
        List<Long> counts = List.of(
                colorProduction.red(),
                colorProduction.white(),
                colorProduction.green(),
                colorProduction.black(),
                colorProduction.blue(),
                colorProduction.colorless()
        );

        var creatures = cards.stream()
                .filter(card -> containsType(card, "Creature"))
                .toList();
        var instants = cards.stream()
                .filter(card -> containsType(card, "Instant"))
                .toList();
        var sorceries = cards.stream()
                .filter(card -> containsType(card, "Sorcery"))
                .toList();
        var enchantments = cards.stream()
                .filter(card -> containsType(card, "Enchantment"))
                .toList();
        var lands = cards.stream()
                .filter(card -> containsType(card, "Land"))
                .toList();
        var artifacts = cards.stream()
                .filter(card -> containsType(card, "Artifact"))
                .toList();
        var total = cards.stream()
                .map(BuilderDeckCardRecord::priceUsd)
                .filter(Objects::nonNull)
                .mapToDouble(Double::parseDouble)
                .sum();
        Map<Integer, Long> manaCurve = cards.stream()
                .filter(card -> card.typeLine() == null || !card.typeLine().contains("Land"))
                .map(BuilderDeckCardRecord::cmc)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .collect(Collectors.groupingBy(
                        cmc -> (int) Double.parseDouble(cmc),
                        Collectors.counting()
                ));
        List<Long> manaCurveData = Stream.concat(
                IntStream.rangeClosed(0, manaCurve.size())
                        .mapToObj(i -> manaCurve.getOrDefault(i, 0L)),
                Stream.of(manaCurve.entrySet().stream()
                        .filter(e -> e.getKey() >= manaCurve.size())
                        .mapToLong(Map.Entry::getValue)
                        .sum())
        ).collect(Collectors.toList());

        var commander = cardService.findByName(cards.getLast().commander());
        List<String> colors = commander.map(ColorIdentity::getColors).orElse(List.of());


        return BuilderViewModel.of(
                deckId,
                deckName,
                deckImage,
                total,
                creatures,
                manaCurveData,
                instants,
                enchantments,
                artifacts,
                lands,
                sorceries,
                counts,
                colors);
    }

    @Builder
    public record ColorProduction(long red, long white, long green, long black, long blue, long colorless) {

        public ColorProduction combine(ColorProduction other) {
            return ColorProduction.builder()
                    .red(this.red + other.red())
                    .white(this.white + other.white())
                    .green(this.green + other.green())
                    .black(this.black + other.black())
                    .blue(this.blue + other.blue())
                    .colorless(this.colorless + other.colorless())
                    .build();
        }

        public static ColorProduction fromIdentity(String identity) {
            if (identity == null || identity.isBlank()) {
                return ColorProduction.builder().colorless(1).build();
            }
            return ColorProduction.builder()
                    .red(identity.contains("R") ? 1 : 0)
                    .white(identity.contains("W") ? 1 : 0)
                    .green(identity.contains("G") ? 1 : 0)
                    .black(identity.contains("B") ? 1 : 0)
                    .blue(identity.contains("U") ? 1 : 0)
                    .colorless(0)
                    .build();
        }
    }

    private ColorProduction getColorProduction(List<BuilderDeckCardRecord> cards) {
        return cards.stream()
                .map(card -> ColorProduction.fromIdentity(card.producedMana()))
                .reduce(ColorProduction.builder().build(), ColorProduction::combine);
    }

    @Override
    public String optimizeDecksAgainstOpponent() {
        Map<String, Integer> oppDeck = new HashMap<>();

        oppDeck.put("1 CMC", 10);
        oppDeck.put("2 CMC", 15);
        oppDeck.put("3 CMC", 12);
        oppDeck.put("4 CMC", 8);
        oppDeck.put("5 CMC", 5);
        oppDeck.put("6 CMC", 2);
        oppDeck.put("Rock", 10);
        oppDeck.put("Draw", 0);
        oppDeck.put("Land", 36);
        oppDeck.put("Sol Ring", 1);
        DeckOptimizerV2 optimizer = new DeckOptimizerV2.Builder()
                .initial1Cmc(10)
                .initial2Cmc(15)
                .initial3Cmc(12)
                .initial4Cmc(8)
                .initial5Cmc(5)
                .initial6Cmc(2)
                .initialRock(10)
                .initialDraw(0)
                .initialLand(36)
                .commanderCost(4)
                .manaRockCost(2)
                .beatOtherMode(false)
                .oppCommander(5)
                .oppDecklist(oppDeck)
                .build();

        optimizer.run();
        return "";
    }

    @Override
    public String optimizeDeck() {
        return "";
    }

    private boolean containsType(BuilderDeckCardRecord card, String type) {
        if (card == null || card.typeLine() == null) {
            return false;
        }
        return card.typeLine().contains(type);
    }
}
