package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
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
import org.springframework.web.util.HtmlUtils;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Controller
public class PersonalLibraryController {

    private final PersonalLibraryService personalLibraryService;
    private final DeckService deckService;

    PersonalLibraryController(PersonalLibraryServiceImpl personalLibraryService,  DeckService deckService ) {
        this.personalLibraryService = personalLibraryService;
        this.deckService = deckService;
    }

    @GetMapping("/personal-library")
    public String getPersonalLibrary(HttpServletResponse response, Model model) {

        model.addAttribute("personalLibrary", new CardBrowserViewModelImpl());
        model.addAttribute("ownedCard", new OwnedCard());
        model.addAttribute("filters", new LibraryFilters());


        response.setHeader("Cache-Control", "max-age=" + TimeUnit.DAYS.toDays(30));
        response.setHeader("Content-Type", "text/html; charset=UTF-8");

        return "personal-library";
    }
    @GetMapping("/personal-library/cards")
    public String getPersonalCards(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        LibraryViewModelImpl libraryView = personalLibraryService.buildPersonalLibraryViewModel(user);

        model.addAttribute("libraryView", libraryView);
        return "fragments/personal-cards :: personal-cards";
    }

    @GetMapping(path = "/personal-library/search", headers = "hx-request=true")
    public  String getCardsMatchingFilter(@ModelAttribute("personalLibraryFilters") LibraryFilters personalLibraryFilters, Model model, @AuthenticationPrincipal CustomUserDetails user){
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
    public String addCardToPersonalLibrary(@ModelAttribute("ownedCard") OwnedCard ownedCard,
                                           @AuthenticationPrincipal CustomUserDetails user,
                                           @RequestHeader(value = "HX-Request", required = false) String hxRequest,
                                           Model model) {

        personalLibraryService.addCard(ownedCard, user.getId());

        if (hxRequest != null) {
            model.addAttribute("query", "");
            model.addAttribute("cards", java.util.List.of());
            model.addAttribute("message", ownedCard.getName() + " added to your library.");
            return "card-query :: card-results";
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

    @GetMapping(value = "/card/location", headers = "hx-request=true")
    @ResponseBody
    public String changeCardLocation(@RequestParam String deck, @RequestParam String cardId, @RequestParam String personalCardId,  @AuthenticationPrincipal CustomUserDetails user) {
        return deckService.addCard(user, HtmlUtils.htmlEscape(deck),UUID.fromString( HtmlUtils.htmlEscape(cardId)), UUID.fromString(HtmlUtils.htmlEscape(personalCardId)));
    }

    @PostMapping("/delete-card")
    @ResponseBody
    public String deleteCard(@RequestParam String personalCardId,
                             HttpServletResponse response,
                             @AuthenticationPrincipal CustomUserDetails user) {


        response.addHeader("HX-Trigger", "refreshLibrary");

        return "";
    }
}
