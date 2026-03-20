package com.example.mtg_deckbuilder.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Controller
public class HomeController {
    @GetMapping("/")
    public String index(HttpServletResponse response) {
        response.setHeader("Cache-Control", "max-age=" + TimeUnit.DAYS.toDays(30));
        response.setHeader("Content-Type", "text/html; charset=UTF-8");
        return "index";
    }
}
