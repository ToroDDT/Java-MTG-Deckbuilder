package com.example.mtg_deckbuilder.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Changed from @RestController
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/create-account")
    public String showCreateAccount() {
        return "register";
    }
}