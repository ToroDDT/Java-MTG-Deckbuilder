package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.api.BuilderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BuilderController {

    private final BuilderService builderService;

    public BuilderController(BuilderService builderService) {
        this.builderService = builderService;
    }

    @GetMapping("/builder")
    public String builder(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        var deckId = "23c76af6-46d6-4cc0-907c-5cccdffa362d";
        model.addAttribute("builderView", builderService.getBuilderView(deckId));
        return "builder";
    }
}
