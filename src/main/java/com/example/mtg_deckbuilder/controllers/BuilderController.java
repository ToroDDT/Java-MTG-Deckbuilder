package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.api.BuilderService;
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

    public BuilderController(BuilderService builderService) {
        this.builderService = builderService;
    }

    @GetMapping("/builder/deck/{id}")
    public String getDeck(@AuthenticationPrincipal CustomUserDetails user, Model model, @PathVariable("id") String deckId) {
        var view = builderService.getMainView(deckId, user);

        model.addAttribute("view", view);
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

        var layout = builderService.getDeckLayoutView(deckId, viewStyle, groupBy, sortBy, extrasParams);

        model.addAttribute("builderView", layout.builderView());
        model.addAttribute("deckViewStyle", layout.deckViewStyle());
        model.addAttribute("deckExtras", layout.deckExtras());
        model.addAttribute("deckSections", layout.deckSections());
        model.addAttribute("deckListCondensed", layout.deckListCondensed());
        model.addAttribute("deckVisualSplit", layout.deckVisualSplit());
        model.addAttribute("deckSpoilerReveal", layout.deckSpoilerReveal());

        boolean visualStacksLike =
                "visual-stacks".equals(layout.deckViewStyle()) || "visual-split".equals(layout.deckViewStyle());
        if ("visual-grid".equals(layout.deckViewStyle()) || "visual-spoiler".equals(layout.deckViewStyle())) {
            return "builder/layouts/visual-grid :: type-layout";
        }
        if (visualStacksLike) {
            return "builder/layouts/visual-stack :: type-layout";
        }
        return "builder/layouts/type :: type-layout";
    }

    @GetMapping(value = "/builder/deck/{deckId}/card-query", headers = "HX-Request=true")
    public String builderCardQuery(
            @PathVariable("deckId") String deckId,
            @RequestParam(name = "query", required = false) String query,
            Model model) {

        var queryView = builderService.getCardQueryView(query);
        model.addAttribute("query", queryView.query());
        model.addAttribute("cards", queryView.cards());
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
        var libraryView = builderService.getOwnedLibraryView(deckId, user);
        model.addAttribute("libraryView", libraryView.libraryView());
        model.addAttribute("builderDeckId", libraryView.builderDeckId());
        model.addAttribute("builderDeckName", libraryView.builderDeckName());
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
        builderService.removeDeckEntry(user, deckId, deckCardEntryId);
        response.addHeader("HX-Trigger", "refreshStats");
        return "";
    }

    @PostMapping("/personal-library/add-to-deck")
    @ResponseBody
    public void addToDeck(
            @RequestParam("deckId") String deckId,
            @RequestParam("name") String cardName,
            @AuthenticationPrincipal CustomUserDetails user,
            HttpServletResponse response) {

        builderService.addCardToDeck(user, deckId, cardName);

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
