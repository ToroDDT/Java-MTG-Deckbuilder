package com.example.mtg_deckbuilder.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@Controller // Changed from @RestController
public class AuthController {

    @GetMapping("/login")
    public String getLoginPage() {
        // Spring will now look for src/main/resources/templates/login.html
        return "login";
    }
}