package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.api.BuilderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class BuilderController {

    private final BuilderService builderService;

    public BuilderController(BuilderService builderService) {
        this.builderService = builderService;
    }

    @GetMapping("/builder")
    public String builder(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        var userName = user.getUsername();
        var deckId = "23c76af6-46d6-4cc0-907c-5cccdffa362d";

        var view = builderService.getBuilderView(deckId);
        model.addAttribute("builderView", view);
        model.addAttribute("manaCurveLabels", List.of("0","1","2","3","4","5","6","7+"));
        model.addAttribute("manaCurveData", view.manaCurveData());
        model.addAttribute("userName", userName);
        return "builder";
    }
}
