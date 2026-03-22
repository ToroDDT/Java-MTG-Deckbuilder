package com.example.mtg_deckbuilder.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PersonalLibrary {
    @GetMapping("/personal-library")
    public String personalLibrary(){
        return "personal-library";
    }
}
