package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.advice.Sanitize;
import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.model.SortOptions;
import com.example.mtg_deckbuilder.service.api.CardBrowserService;
import com.example.mtg_deckbuilder.views.impl.CardBrowserViewModelImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.concurrent.TimeUnit;

@Controller
public class CardBrowserController {

    private final CardBrowserService cardBrowserService;

    public CardBrowserController(CardBrowserService cardBrowserService) {
        this.cardBrowserService = cardBrowserService;
    }

    @GetMapping("/card-browser")
    public String getCardBrowser(HttpServletResponse response, Model model) {
        LibraryFilters filters = defaultFilters();

        model.addAttribute("cardBrowser", new CardBrowserViewModelImpl());
        model.addAttribute("filters", filters);
        model.addAttribute("catalogView", cardBrowserService.buildViewModel(filters));

        response.setHeader("Cache-Control", "max-age=" + TimeUnit.DAYS.toDays(30));
        response.setHeader("Content-Type", "text/html; charset=UTF-8");

        return "card-browser/main";
    }

    @GetMapping(path = "/card-browser/search", headers = "hx-request=true")
    public String searchCards(@ModelAttribute @Sanitize LibraryFilters filters, Model model) {
        applyFilterDefaults(filters);
        model.addAttribute("catalogView", cardBrowserService.buildViewModel(filters));
        return "card-browser/cards :: catalog-cards";
    }

    @GetMapping("/card-browser/info")
    public String getCardBrowserInfo(@ModelAttribute @Sanitize LibraryFilters filters, Model model) {
        applyFilterDefaults(filters);
        model.addAttribute("catalogView", cardBrowserService.buildViewModel(filters));
        return "card-browser/stats :: stickyStatsBar";
    }

    private static LibraryFilters defaultFilters() {
        LibraryFilters filters = new LibraryFilters();
        applyFilterDefaults(filters);
        return filters;
    }

    private static void applyFilterDefaults(LibraryFilters filters) {
        if (filters.getSortBy() == null) {
            filters.setSortBy(SortOptions.RECENT);
        }
        if (filters.getMinCMC() == null) {
            filters.setMinCMC(0);
        }
        if (filters.getMaxCMC() == null) {
            filters.setMaxCMC(16);
        }
        if (filters.getPage() == null) {
            filters.setPage(0);
        }
    }
}
