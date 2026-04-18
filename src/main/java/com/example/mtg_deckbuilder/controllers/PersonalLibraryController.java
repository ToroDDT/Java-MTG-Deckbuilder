package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.api.DeckService;
import com.example.mtg_deckbuilder.service.impl.ComboServiceImpl;
import com.example.mtg_deckbuilder.service.impl.PersonalLibraryServiceImpl;
import com.example.mtg_deckbuilder.service.api.PersonalLibraryService;
import com.example.mtg_deckbuilder.views.CardBrowserViewModel;
import com.example.mtg_deckbuilder.views.LibraryViewModel;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Controller
public class PersonalLibraryController {

    private final PersonalLibraryService personalLibraryService;
    private final ComboServiceImpl comboServiceImpl;
    private final DeckService deckService;
    private final DataSourceTransactionManager dataSourceTransactionManager;

    PersonalLibraryController(PersonalLibraryServiceImpl personalLibraryService, ComboServiceImpl comboServiceImpl, DeckService deckService, DataSourceTransactionManager dataSourceTransactionManager) {
        this.personalLibraryService = personalLibraryService;
        this.comboServiceImpl = comboServiceImpl;
        this.deckService = deckService;
        this.dataSourceTransactionManager = dataSourceTransactionManager;
    }

    @GetMapping("/personal-library")
    public String getPersonalLibrary(HttpServletResponse response, Model model) {
        CardBrowserViewModel cardBrowserViewModel = new CardBrowserViewModel();

        model.addAttribute("personalLibrary", cardBrowserViewModel);
        model.addAttribute("ownedCard", new OwnedCard());
        model.addAttribute("filters", new LibraryFilters());

        response.setHeader("Cache-Control", "max-age=" + TimeUnit.DAYS.toDays(30));
        response.setHeader("Content-Type", "text/html; charset=UTF-8");

        return "personal-library";
    }
    @GetMapping("/personal-library/cards")
    public String getPersonalCards(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        LibraryViewModel libraryView = personalLibraryService.buildPersonalLibraryViewModel(user);

        model.addAttribute("cards", libraryView.getCards());
        model.addAttribute("ownedCard", new OwnedCard());
        model.addAttribute("personalLibraryFilters", new LibraryFilters());
        model.addAttribute("libraryView", libraryView);

        return "fragments/personal-cards :: personal-cards";
    }
    @GetMapping("/personal-library/info")
    public String getPersonalCardsInfo(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        LibraryViewModel libraryView = personalLibraryService.buildPersonalLibraryViewModel(user);

        model.addAttribute("personalLibrary", libraryView);

        return "fragments/collection-info :: stickyStatsBar";
    }


    @PostMapping("/personal-library/add")
    public String addCardToPersonalLibrary(@ModelAttribute("ownedCard") OwnedCard ownedCard, @AuthenticationPrincipal CustomUserDetails user) {
        personalLibraryService.addCard(ownedCard, user.getId());
        return "redirect:/personal-library";
    }

    @GetMapping(path = "/personal-library/search", headers = "hx-request=true")
    public  String getCardsMatchingFilter(@ModelAttribute("personalLibraryFilters") LibraryFilters personalLibraryFilters, Model model, @AuthenticationPrincipal CustomUserDetails user){
        LibraryViewModel libraryView = personalLibraryService.buildPersonalLibraryViewModel(user, personalLibraryFilters);

        model.addAttribute("cards", libraryView);
        model.addAttribute("ownedCard", new OwnedCard());
        model.addAttribute("personalLibraryFilters", new LibraryFilters());
        model.addAttribute("libraryView", libraryView);
        return "fragments/personal-cards :: personal-cards";
    }

    @GetMapping(value = "/card/location", headers = "hx-request=true")
    @ResponseBody
    public String changeCardLocation(@RequestParam String deck, @RequestParam String cardId, @RequestParam String personalCardId,  @AuthenticationPrincipal CustomUserDetails user) {
        System.out.println(deckService.addCard(user, HtmlUtils.htmlEscape(deck),UUID.fromString( HtmlUtils.htmlEscape(cardId)), UUID.fromString(HtmlUtils.htmlEscape(personalCardId))));
       return deckService.addCard(user, HtmlUtils.htmlEscape(deck),UUID.fromString( HtmlUtils.htmlEscape(cardId)), UUID.fromString(HtmlUtils.htmlEscape(personalCardId)));
    }
}