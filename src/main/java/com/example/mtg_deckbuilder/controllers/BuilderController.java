package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.api.BuilderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
        var userName = user.getUsername();
        var view = builderService.getBuilderView(deckId);

        model.addAttribute("builderView", view);
        model.addAttribute("manaCurveLabels", List.of("0","1","2","3","4","5","6","7+"));
        model.addAttribute("manaCurveData", view.manaCurveData());
        model.addAttribute("userName", userName);

        return "builder";
    }

    @GetMapping(value = "/builder/deck/{deckId}/deck-entry/{deckCardEntryId}/hover", headers = "HX-Request=true")
    public String deckEntryHover(
            @AuthenticationPrincipal CustomUserDetails user,
            Model model,
            @PathVariable("deckId") String deckIdStr,
            @PathVariable("deckCardEntryId") String deckCardEntryIdStr) {
        var hoverOpt = builderService.getDeckEntryHover(
                user, UUID.fromString(deckIdStr), UUID.fromString(deckCardEntryIdStr));
        model.addAttribute("hover", hoverOpt.orElse(null));
        return "fragments/builder-card-hover :: hoverPanel";
    }
}
