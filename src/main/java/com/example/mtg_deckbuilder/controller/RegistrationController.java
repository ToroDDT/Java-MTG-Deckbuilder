package com.example.mtg_deckbuilder.controller;
import com.example.mtg_deckbuilder.dto.UserRegistrationDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {

    // 1. Display the Registration Form
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // This allows Thymeleaf to bind form fields to this object
        model.addAttribute("user", new UserRegistrationDto());
        return "register"; // matches the name of your HTML file (register.html)
    }

    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto,
                                      RedirectAttributes redirectAttributes) {

        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match!");
            return "redirect:/register";
        }

        // TODO: Save user to database using a Service (BCrypt password encoding recommended)
        System.out.println("Registering user: " + registrationDto.getUsername());

        redirectAttributes.addFlashAttribute("message", "Account created successfully!");
        return "redirect:/login";
    }
}