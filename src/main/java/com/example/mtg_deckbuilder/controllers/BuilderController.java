package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.api.BuilderService;
import com.example.mtg_deckbuilder.service.api.DeckService;
import com.example.mtg_deckbuilder.service.api.PersonalLibraryService;
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

import java.util.List;
import java.util.UUID;

@Controller
public class BuilderController {

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
        model.addAttribute("colorProductionLabels", List.of("Red", "White", "Green", "Black", "Blue", "Colorless"));
        model.addAttribute("userName", userName);
        model.addAttribute("ownedCard", new OwnedCard());

        return "builder/main";
    }

    @GetMapping("/builder/type-layout/{id}")
    public String getCardTypeLayout(
            Model model,
            @PathVariable("id") String deckId
    ) {

        var view = builderService.getBuilderView(deckId);

        model.addAttribute("builderView", view);

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
