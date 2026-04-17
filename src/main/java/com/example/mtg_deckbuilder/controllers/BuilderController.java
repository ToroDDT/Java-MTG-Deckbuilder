package com.example.mtg_deckbuilder.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BuilderController {
    @GetMapping("/builder")
    public String builder() {
        return "builder";
    }
}
