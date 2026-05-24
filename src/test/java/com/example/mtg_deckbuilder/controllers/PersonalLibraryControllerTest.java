package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.config.SecurityConfig;
import com.example.mtg_deckbuilder.model.ColorIdentity;
import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.api.CardScannerClient;
import com.example.mtg_deckbuilder.service.api.DeckService;
import com.example.mtg_deckbuilder.service.impl.PersonalLibraryServiceImpl;
import com.example.mtg_deckbuilder.views.LibraryViewModelImpl;
import com.example.mtg_deckbuilder.views.PersonalLibraryStats;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.mock.web.MockMultipartFile;

@WebMvcTest(PersonalLibraryController.class)
@Import(SecurityConfig.class)
class PersonalLibraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonalLibraryServiceImpl personalLibraryService;

    @MockitoBean
    private DeckService deckService;

    @MockitoBean
    private CardScannerClient cardScannerClient;

    @Test
    void getPersonalLibraryReturnsPageWithExpectedModel() throws Exception {
        when(personalLibraryService.buildPersonalLibraryViewModel(any(CustomUserDetails.class)))
                .thenReturn(LibraryViewModelImpl.builder()
                        .cards(List.of())
                        .deckNames(List.of())
                        .colorIdentityAmounts(Map.of())
                        .totalValue(0.0)
                        .totalCards(0)
                        .avgPrice(0.0)
                        .build());

        mockMvc.perform(get("/personal-library").with(authenticationToken()))
                .andExpect(status().isOk())
                .andExpect(view().name("library/main"))
                .andExpect(model().attributeExists("personalLibrary"))
                .andExpect(model().attributeExists("ownedCard"))
                .andExpect(model().attributeExists("filters"))
                .andExpect(model().attributeExists("libraryView"))
                .andExpect(header().string("Content-Type", "text/html;charset=UTF-8"));
    }

    @Test
    void getPersonalCardsReturnsFragment() throws Exception {
        when(personalLibraryService.buildPersonalLibraryViewModel(any(CustomUserDetails.class)))
                .thenReturn(LibraryViewModelImpl.builder()
                        .cards(List.of())
                        .deckNames(List.of("Azorius"))
                        .colorIdentityAmounts(Map.of())
                        .totalValue(0.0)
                        .totalCards(0)
                        .avgPrice(0.0)
                        .build());

        mockMvc.perform(get("/personal-library/cards").with(authenticationToken()))
                .andExpect(status().isOk())
                .andExpect(view().name("library/cards :: personal-cards"))
                .andExpect(model().attributeExists("libraryView"));
    }

    @Test
    void searchReturnsFragmentWithBoundFilters() throws Exception {
        when(personalLibraryService.buildPersonalLibraryViewModel(any(CustomUserDetails.class), any(LibraryFilters.class)))
                .thenReturn(LibraryViewModelImpl.builder()
                        .cards(List.of())
                        .deckNames(List.of())
                        .colorIdentityAmounts(Map.of())
                        .totalValue(0.0)
                        .totalCards(0)
                        .avgPrice(0.0)
                        .build());

        mockMvc.perform(get("/personal-library/search")
                        .with(authenticationToken())
                        .header("HX-Request", "true")
                        .param("cardName", "Sol")
                        .param("tagSearch", "ramp, recursion"))
                .andExpect(status().isOk())
                .andExpect(view().name("library/cards :: personal-cards"))
                .andExpect(model().attributeExists("libraryView"));
    }

    @Test
    void addCardWithHtmxReturnsQueryFragmentMessage() throws Exception {
        mockMvc.perform(post("/personal-library/add")
                        .with(authenticationToken())
                        .with(csrf())
                        .header("HX-Request", "true")
                        .param("name", "Sol Ring"))
                .andExpect(status().isOk())
                .andExpect(view().name("library/results :: card-results"))
                .andExpect(model().attribute("message", "Sol Ring added to your library."));

        verify(personalLibraryService).addCard(any(OwnedCard.class), any(CustomUserDetails.class));
    }

    @Test
    void scanCardWithHtmxReturnsQueryFragmentMessage() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "thassa.png", "image/png", "image".getBytes());
        when(cardScannerClient.scanCard(any(byte[].class), anyString(), anyString()))
                .thenReturn("Thassa, Deep-Dwelling");

        mockMvc.perform(multipart("/personal-library/scan")
                        .file(file)
                        .with(authenticationToken())
                        .with(csrf())
                        .header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("library/results :: card-results"))
                .andExpect(model().attribute("message", "Thassa, Deep-Dwelling added to your library."));

        verify(cardScannerClient).scanCard(any(byte[].class), eq("thassa.png"), eq("image/png"));
        verify(personalLibraryService).addCard(any(OwnedCard.class), any(CustomUserDetails.class));
    }

    @Test
    void updateTagsReturnsTagsFragment() throws Exception {
        UUID personalCardId = UUID.randomUUID();
        when(personalLibraryService.updateCardTags(eq("Ramp"), eq(personalCardId.toString()), any(CustomUserDetails.class)))
                .thenReturn(List.of("Ramp", "Staple"));

        mockMvc.perform(get("/update-tags")
                        .with(authenticationToken())
                        .header("HX-Request", "true")
                        .param("tag", "Ramp")
                        .param("personalCardId", personalCardId.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("library/tags :: tags"))
                .andExpect(model().attributeExists("card"));
    }

    @Test
    void removeTagReturnsTagsFragment() throws Exception {
        UUID personalCardId = UUID.randomUUID();
        when(personalLibraryService.removeCardTag(eq("Ramp"), eq(personalCardId.toString()), any(CustomUserDetails.class)))
                .thenReturn(List.of("Staple"));

        mockMvc.perform(post("/remove-tag")
                        .with(authenticationToken())
                        .with(csrf())
                        .header("HX-Request", "true")
                        .param("tag", "Ramp")
                        .param("personalCardId", personalCardId.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("library/tags :: tags"))
                .andExpect(model().attributeExists("card"));
    }

    @Test
    void cardLocationReturnsDeckNameBody() throws Exception {
        UUID cardId = UUID.randomUUID();
        UUID personalCardId = UUID.randomUUID();
        when(deckService.addCard(any(CustomUserDetails.class), eq("Artifacts"), eq(cardId), eq(personalCardId)))
                .thenReturn("Artifacts");

        mockMvc.perform(get("/card/location")
                        .with(authenticationToken())
                        .header("HX-Request", "true")
                        .param("deck", "Artifacts")
                        .param("cardId", cardId.toString())
                        .param("personalCardId", personalCardId.toString()))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string("Artifacts"));
    }

    @Test
    void cardLocationWithBlankDeckRemovesAssignment() throws Exception {
        UUID cardId = UUID.randomUUID();
        UUID personalCardId = UUID.randomUUID();

        mockMvc.perform(get("/card/location")
                        .with(authenticationToken())
                        .header("HX-Request", "true")
                        .param("deck", "")
                        .param("cardId", cardId.toString())
                        .param("personalCardId", personalCardId.toString()))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string("None"));

        verify(deckService).removePersonalLibraryCardFromDeck(any(CustomUserDetails.class), eq(personalCardId));
    }

    @Test
    void personalLibraryInfoReturnsStatsFragment() throws Exception {
        when(personalLibraryService.getLibraryInfo(any(CustomUserDetails.class)))
                .thenReturn(new PersonalLibraryStats(42.0, Map.of(ColorIdentity.COLORLESS, 2L), 2, 21.0));

        mockMvc.perform(get("/personal-library/info").with(authenticationToken()))
                .andExpect(status().isOk())
                .andExpect(view().name("library/stats :: stickyStatsBar"))
                .andExpect(model().attributeExists("personalLibrary"));
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
