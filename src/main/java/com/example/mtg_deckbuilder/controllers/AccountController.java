package com.example.mtg_deckbuilder.controllers;
import com.example.mtg_deckbuilder.dto.combo.UserRegistrationDto;
import com.example.mtg_deckbuilder.exceptions.UserAlreadyExistsException;
import com.example.mtg_deckbuilder.service.impl.RegistrationServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AccountController {

    private final RegistrationServiceImpl registrationService;

    @Autowired
    public AccountController(RegistrationServiceImpl registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/login")
    public String showLoginPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/decks";
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUserAccount(@Valid @ModelAttribute("user") UserRegistrationDto registrationDto, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            registrationService.registerUser(registrationDto);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Account created successfully!"
            );

            return "redirect:/login";

        } catch (UserAlreadyExistsException e) {

            result.rejectValue("email", "error.user", e.getMessage());

            return "auth/register";
        }
    }

}
