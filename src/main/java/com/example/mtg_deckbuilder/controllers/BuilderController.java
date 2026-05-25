package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.api.BuilderService;
import com.example.mtg_deckbuilder.service.api.DeckService;
import com.example.mtg_deckbuilder.service.api.PersonalLibraryService;
import com.example.mtg_deckbuilder.views.BuilderDeckSection;
import com.example.mtg_deckbuilder.views.DeckLayoutExtrasFlags;
import com.example.mtg_deckbuilder.views.LibraryViewModelImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller
public class BuilderController {

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

    private final BuilderService builderService;
    private final DeckService deckService;
    private final PersonalLibraryService personalLibraryService;

    public BuilderController(
            BuilderService builderService,
            DeckService deckService,
            PersonalLibraryService personalLibraryService) {
        this.builderService = builderService;
        this.deckService = deckService;
        this.personalLibraryService = personalLibraryService;
    }

    @GetMapping("/builder/deck/{id}")
    public String getDeck(@AuthenticationPrincipal CustomUserDetails user, Model model, @PathVariable("id") String deckId) {
        var userName = user.getUsername();
        var view = builderService.getBuilderView(deckId);

        model.addAttribute("builderView", view);
        model.addAttribute("manaCurveLabels", List.of("0", "1", "2", "3", "4", "5", "6", "7+"));
        model.addAttribute("manaCurveData", view.manaCurveData());
        model.addAttribute("colorProductionData", view.colorProduction());
        model.addAttribute("colorProductionTotal",
                view.colorProduction().stream().mapToLong(Long::longValue).sum());
        model.addAttribute("colorProductionLabels", List.of("Red", "White", "Green", "Black", "Blue", "Colorless"));
        model.addAttribute("userName", userName);
        model.addAttribute("ownedCard", new OwnedCard());

        return "builder/main";
    }

    @GetMapping("/builder/type-layout/{id}")
    public String getCardTypeLayout(
            Model model,
            @PathVariable("id") String deckId,
            @RequestParam(name = "viewStyle", required = false, defaultValue = "text") String viewStyle,
            @RequestParam(name = "groupBy", required = false, defaultValue = "type") String groupBy,
            @RequestParam(name = "sortBy", required = false, defaultValue = "name") String sortBy,
            @RequestParam(name = "extras", required = false) List<String> extrasParams
    ) {

        var view = builderService.getBuilderView(deckId);

        model.addAttribute("builderView", view);

        var normalizedStyle =
                ALLOWED_VIEW_STYLES.contains(viewStyle) ? viewStyle : "text";
        model.addAttribute("deckViewStyle", normalizedStyle);

        String normalizedGroup = ALLOWED_GROUP_BY.contains(groupBy) ? groupBy : "type";
        String normalizedSort = ALLOWED_SORT_BY.contains(sortBy) ? sortBy : "name";
        HashSet<String> extras = new HashSet<>();
        if (extrasParams != null) {
            for (String chunk : extrasParams) {
                if (chunk != null && ALLOWED_EXTRAS.contains(chunk)) {
                    extras.add(chunk);
                }
            }
        }
        var deckExtras = new DeckLayoutExtrasFlags(
                extras.contains("mana-cost"),
                extras.contains("price"),
                extras.contains("set-symbol"));
        model.addAttribute("deckExtras", deckExtras);

        List<BuilderDeckSection> sections =
                builderService.buildDeckSections(deckId, normalizedGroup, normalizedSort);
        model.addAttribute("deckSections", sections);
        model.addAttribute("deckListCondensed", "condensed".equals(normalizedStyle));
        model.addAttribute("deckVisualSplit", "visual-split".equals(normalizedStyle));
        model.addAttribute("deckSpoilerReveal", "visual-spoiler".equals(normalizedStyle));

        boolean visualStacksLike =
                "visual-stacks".equals(normalizedStyle) || "visual-split".equals(normalizedStyle);
        if ("visual-grid".equals(normalizedStyle) || "visual-spoiler".equals(normalizedStyle)) {
            return "builder/layouts/visual-grid :: type-layout";
        }
        if (visualStacksLike) {
            return "builder/layouts/visual-stack :: type-layout";
        }
        return "builder/layouts/type :: type-layout";
    }

    @GetMapping(value = "/builder/deck/{deckId}/card-query", headers = "HX-Request=true")
    public String builderCardQuery(
            @PathVariable("deckId") String deckId, // <-- Added this annotation
            @RequestParam(name = "query", required = false) String query,
            Model model) {

        String trimmedQuery = query == null ? "" : query.trim();
        model.addAttribute("query", trimmedQuery);
        model.addAttribute("cards", personalLibraryService.getCardQuery(trimmedQuery));
        model.addAttribute("addCardFormId", "builderAddCardForm");
        model.addAttribute("cardQueryTargetId", "builder-card-query-results");
        model.addAttribute("cardQueryInputId", "builder-card-name");
        model.addAttribute("deckId", deckId);

        return "builder/results :: card-results";
    }
    @GetMapping(value = "/builder/deck/{deckId}/owned-library", headers = "HX-Request=true")
    public String builderOwnedLibrary(
            @PathVariable("deckId") String deckId,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model) {
        LibraryViewModelImpl libraryView = personalLibraryService.buildPersonalLibraryViewModel(user);
        model.addAttribute("libraryView", libraryView);
        model.addAttribute("builderDeckId", deckId);
        model.addAttribute("builderDeckName", builderService.getBuilderView(deckId).deckName());
        return "builder/cards :: builder-owned-cards";
    }

    @GetMapping(value = "/builder/deck/{deckId}/deck-entry/{deckCardEntryId}/hover", headers = "HX-Request=true")
    public String deckEntryHover(
            @AuthenticationPrincipal CustomUserDetails user,
            Model model,
            @PathVariable("deckId") UUID deckIdStr,
            @PathVariable("deckCardEntryId") UUID deckCardEntryIdStr) {
        var hoverOpt = builderService.getDeckEntryHover(
                user, deckIdStr, deckCardEntryIdStr);
        model.addAttribute("hover", hoverOpt.orElse(null));
        return "builder/card-hover :: hoverPanel";
    }

    @GetMapping(value = "/builder/deck/{deckId}/deck-entry/{deckCardEntryId}/actions", headers = "HX-Request=true")
    public String deckEntryActions(
            Model model,
            @PathVariable("deckId") String deckId,
            @PathVariable("deckCardEntryId") String deckCardEntryId) {
        model.addAttribute("deckId", deckId);
        model.addAttribute("deckEntryId", deckCardEntryId);
        return "builder/card-actions :: actionsPanel";
    }

    @PostMapping(value = "/builder/deck/{deckId}/deck-entry/{deckCardEntryId}/delete", headers = "HX-Request=true")
    @ResponseBody
    public String deleteDeckEntry(
            @AuthenticationPrincipal CustomUserDetails user,
            HttpServletResponse response,
            @PathVariable UUID deckId,
            @PathVariable UUID deckCardEntryId) {
        deckService.removeDeckEntry(user, deckId, deckCardEntryId);
        response.addHeader("HX-Trigger", "refreshStats");
        return "";
    }
    @PostMapping("/personal-library/add-to-deck")
    @ResponseBody
    public void addToDeck(
            @RequestParam("deckId") String deckId,
            @RequestParam("name") String cardName,
            @AuthenticationPrincipal CustomUserDetails user,
            HttpServletResponse response) { // <-- Inject the response object

        deckService.addCard(user, deckId, cardName);

        response.setHeader("HX-Trigger", "cardsUpdated");
    }

    @GetMapping("/builder/randomize-cards")
    public String getRandomizedCards(
            Model model,
            @RequestParam("deckId") UUID deckId) {
        model.addAttribute("deckId", deckId);
        model.addAttribute("cardImages", builderService.getRandomizedCards(deckId));
        return "builder/randomized-cards :: randomized-hand";
    }
}
