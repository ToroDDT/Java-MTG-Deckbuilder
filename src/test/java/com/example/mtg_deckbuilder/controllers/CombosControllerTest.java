package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.config.SecurityConfig;
import com.example.mtg_deckbuilder.dto.combo.CardCombos;
import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.impl.ComboServiceImpl;
import com.example.mtg_deckbuilder.views.ComboDetailViewModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(CombosController.class)
@Import(SecurityConfig.class)
class CombosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ComboServiceImpl comboService;

    @Test
    void combosListBindsSearchFiltersAndReturnsFragment() throws Exception {
        when(comboService.getLocations(any(CustomUserDetails.class)))
                .thenReturn(List.of("library", "Artifacts Deck"));
        when(comboService.getCombos(any(CustomUserDetails.class), any(LibraryFilters.class)))
                .thenReturn(CardCombos.builder()
                        .cardCombinations(List.of())
                        .description(List.of())
                        .images(List.of())
                        .results(List.of())
                        .build());

        mockMvc.perform(get("/personal-library/combos-list")
                        .with(authenticationToken())
                        .header("HX-Request", "true")
                        .param("cardName", "Goblin")
                        .param("oracleTextSearch", "damage")
                        .param("location", "library")
                        .param("minPrice", "1.50")
                        .param("maxPrice", "9.99"))
                .andExpect(status().isOk())
                .andExpect(view().name("combos :: combos-section"))
                .andExpect(model().attributeExists("cardCombos"))
                .andExpect(model().attributeExists("locationOptions"))
                .andExpect(model().attribute("filters", org.hamcrest.Matchers.hasProperty("cardName", org.hamcrest.Matchers.equalTo("Goblin"))))
                .andExpect(model().attribute("filters", org.hamcrest.Matchers.hasProperty("oracleTextSearch", org.hamcrest.Matchers.equalTo("damage"))))
                .andExpect(model().attribute("filters", org.hamcrest.Matchers.hasProperty("location", org.hamcrest.Matchers.equalTo("library"))))
                .andExpect(model().attribute("filters", org.hamcrest.Matchers.hasProperty("minPrice", org.hamcrest.Matchers.equalTo(1.50))))
                .andExpect(model().attribute("filters", org.hamcrest.Matchers.hasProperty("maxPrice", org.hamcrest.Matchers.equalTo(9.99))));

        verify(comboService).getCombos(any(CustomUserDetails.class), any(LibraryFilters.class));
    }

    @Test
    void comboDetailReturnsDedicatedPage() throws Exception {
        when(comboService.getComboDetail(any(CustomUserDetails.class), any(), any(), any()))
                .thenReturn(Optional.of(new ComboDetailViewModel(
                        "Bruce Banner | Legolas's Quick Reflexes",
                        "library",
                        "GUR",
                        List.of("G", "U", "R"),
                        false,
                        List.of("Bruce Banner", "Legolas's Quick Reflexes"),
                        List.of("front-1.jpg", "front-2.jpg"),
                        List.of("hero-1.jpg", "hero-2.jpg"),
                        List.of("Legolas's Quick Reflexes in hand."),
                        "The Incredible Hulk is indestructible.",
                        "{G} available.",
                        List.of("Cast Legolas's Quick Reflexes."),
                        "The Incredible Hulk cannot have vigilance.",
                        List.of("Infinite combat phases."),
                        "$87.87",
                        "$37.99",
                        List.of(new ComboDetailViewModel.LegalityRow("Commander", true))
                )));

        mockMvc.perform(get("/personal-library/combos/detail")
                        .with(authenticationToken())
                        .param("location", "library")
                        .param("cards", "Bruce Banner||Legolas's Quick Reflexes")
                        .param("description", "Infinite combat phases."))
                .andExpect(status().isOk())
                .andExpect(view().name("combo-detail"))
                .andExpect(model().attributeExists("comboDetail"));
    }

    private static org.springframework.test.web.servlet.request.RequestPostProcessor authenticationToken() {
        CustomUserDetails user = new CustomUserDetails(
                "tester",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                UUID.randomUUID()
        );
        return authentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
    }
}
