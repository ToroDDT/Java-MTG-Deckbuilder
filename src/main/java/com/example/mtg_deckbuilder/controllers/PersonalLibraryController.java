package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.advice.Sanitize;
import com.example.mtg_deckbuilder.exceptions.CardScanFailedException;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.api.CardScannerClient;
import com.example.mtg_deckbuilder.service.api.DeckService;
import com.example.mtg_deckbuilder.service.api.CardService;
import com.example.mtg_deckbuilder.service.impl.PersonalLibraryServiceImpl;
import com.example.mtg_deckbuilder.service.api.PersonalLibraryService;
import com.example.mtg_deckbuilder.views.CardBrowserViewModelImpl;
import com.example.mtg_deckbuilder.views.CardTagsViewModel;
import com.example.mtg_deckbuilder.views.LibraryViewModelImpl;
import com.example.mtg_deckbuilder.views.PersonalLibraryStats;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Controller
public class PersonalLibraryController {

    private final PersonalLibraryService personalLibraryService;
    private final DeckService deckService;
    private final CardScannerClient cardScannerClient;

    PersonalLibraryController(PersonalLibraryServiceImpl personalLibraryService,
                              DeckService deckService,
                              CardScannerClient cardScannerClient) {
        this.personalLibraryService = personalLibraryService;
        this.deckService = deckService;
        this.cardScannerClient = cardScannerClient;
    }

    @GetMapping("/personal-library")
    public String getPersonalLibrary(@AuthenticationPrincipal CustomUserDetails user,
                                     HttpServletResponse response, Model model) {

        model.addAttribute("personalLibrary", new CardBrowserViewModelImpl());
        model.addAttribute("ownedCard", new OwnedCard());
        model.addAttribute("filters", new LibraryFilters());
        model.addAttribute("libraryView",
                user != null
                        ? personalLibraryService.buildPersonalLibraryViewModel(user)
                        : emptyLibraryView());


        response.setHeader("Cache-Control", "max-age=" + TimeUnit.DAYS.toDays(30));
        response.setHeader("Content-Type", "text/html; charset=UTF-8");

        return "personal-library";
    }

    private static LibraryViewModelImpl emptyLibraryView() {
        return LibraryViewModelImpl.builder()
                .cards(List.of())
                .deckNames(List.of())
                .totalCards(0)
                .totalValue(0.0)
                .avgPrice(0.0)
                .colorIdentityAmounts(Map.of())
                .build();
    }

    @GetMapping("/personal-library/cards")
    public String getPersonalCards(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        LibraryViewModelImpl libraryView = personalLibraryService.buildPersonalLibraryViewModel(user);

        model.addAttribute("libraryView", libraryView);

        return "fragments/personal-cards :: personal-cards";
    }

    @GetMapping(path = "/personal-library/search", headers = "hx-request=true")
    public  String getCardsMatchingFilter(@ModelAttribute("personalLibraryFilters") @Sanitize LibraryFilters personalLibraryFilters, Model model, @AuthenticationPrincipal CustomUserDetails user){

        LibraryViewModelImpl libraryView = personalLibraryService.buildPersonalLibraryViewModel(user, personalLibraryFilters);

        model.addAttribute("libraryView", libraryView);

        return "fragments/personal-cards :: personal-cards";
    }

    @GetMapping("/personal-library/info")
    public String getPersonalCardsInfo(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        PersonalLibraryStats libraryView = personalLibraryService.getStatsOfPersonalLibrary(user);

        model.addAttribute("personalLibrary", libraryView);

        return "fragments/collection-info :: stickyStatsBar";
    }

    @GetMapping(path = "/personal-library/card-query", headers = "hx-request=true")
    public String getCardsForAdd(@RequestParam(name = "query", required = false) String query, Model model) {
        String trimmedQuery = query == null ? "" : query.trim();
        model.addAttribute("query", trimmedQuery);
        model.addAttribute("cards", personalLibraryService.getCardQuery(trimmedQuery));
        return "card-query :: card-results";
    }


    @PostMapping("/personal-library/add")
    public String addCardToPersonalLibrary(@ModelAttribute("ownedCard") OwnedCard ownedCard, HttpServletResponse response,
                                           @AuthenticationPrincipal CustomUserDetails user,
                                           @RequestHeader(value = "HX-Request", required = false) String hxRequest,
                                           Model model) {
        personalLibraryService.addCard(ownedCard, user);

        if (hxRequest != null) {
            return buildCardQueryResponse(response, model, ownedCard.getName() + " added to your library.");
        }

        return "redirect:/personal-library";
    }

    @PostMapping("/personal-library/scan")
    public String scanCardToPersonalLibrary(@RequestPart("file") MultipartFile file,
                                            HttpServletResponse response,
                                            @AuthenticationPrincipal CustomUserDetails user,
                                            @RequestHeader(value = "HX-Request", required = false) String hxRequest,
                                            Model model) throws IOException {
        if (file.isEmpty()) {
            return buildCardQueryResponse(response, model, "Select an image to scan.");
        }

        final String scannedName;
        try {
            scannedName = cardScannerClient.scanCard(
                    file.getBytes(),
                    file.getOriginalFilename(),
                    file.getContentType());
        } catch (CardScanFailedException ex) {
            return buildCardQueryResponse(response, model, ex.getMessage());
        }

        OwnedCard ownedCard = new OwnedCard();
        ownedCard.setName(scannedName);
        personalLibraryService.addCard(ownedCard, user);

        if (hxRequest != null) {
            return buildCardQueryResponse(response, model, scannedName + " added to your library.");
        }

        return "redirect:/personal-library";
    }

    @GetMapping(value = "/update-tags", headers = "hx-request=true")
    public String addTag(
            @RequestParam String tag,
            @RequestParam String personalCardId,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model) {

        List<String> tags = personalLibraryService.updateCardTags(tag, personalCardId, user);
        model.addAttribute("card", new CardTagsViewModel(personalCardId, tags));
        return "fragments/tags :: tags";
    }

    @PostMapping(value = "/remove-tag", headers = "hx-request=true")
    public String removeTag(
            @RequestParam String tag,
            @RequestParam String personalCardId,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model) {

        List<String> tags = personalLibraryService.removeCardTag(tag, personalCardId, user);
        model.addAttribute("card", new CardTagsViewModel(personalCardId, tags));
        return "fragments/tags :: tags";
    }

    @GetMapping(value = "/card/location", headers = "hx-request=true")
    @ResponseBody
    public String changeCardLocation(
            @RequestParam(required = false) String deck,
            @RequestParam String cardId,
            @RequestParam String personalCardId,
            @AuthenticationPrincipal CustomUserDetails user) {
        UUID resolvedCardId = UUID.fromString(HtmlUtils.htmlEscape(cardId));
        UUID resolvedPersonalCardId = UUID.fromString(HtmlUtils.htmlEscape(personalCardId));
        if (deck == null || deck.isBlank()) {
            deckService.removePersonalLibraryCardFromDeck(user, resolvedPersonalCardId);
            return "None";
        }
        return deckService.addCard(user, HtmlUtils.htmlEscape(deck.trim()), resolvedCardId, resolvedPersonalCardId);
    }

    @PostMapping("/delete-card")
    @ResponseBody
    public String deleteCard(@RequestParam UUID personalCardId,
                             HttpServletResponse response,
                             @AuthenticationPrincipal CustomUserDetails user) {

        personalLibraryService.delete(user, personalCardId);
        response.addHeader("HX-Trigger", "refreshStats");
        return "";
    }

    private String buildCardQueryResponse(HttpServletResponse response, Model model, String message) {
        response.addHeader("HX-Trigger", "refreshLibrary");
        model.addAttribute("query", "");
        model.addAttribute("cards", java.util.List.of());
        model.addAttribute("message", message);
        return "card-query :: card-results";
    }
}
