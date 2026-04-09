package com.example.mtg_deckbuilder.service;

import com.example.mtg_deckbuilder.dto.Combos;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommanderSpellBookService{

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String BASE_URL = "https://backend.commanderspellbook.com/find-my-combos/";
    private static final String SEARCH_COMBO_URL= "https://backend.commanderspellbook.com/variants/?format=json&q=card%3D%22Thassa%27s+Oracle%22";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final DefaultPersonalLibraryService personalLibraryService;

    @Autowired
    public CommanderSpellBookService(DefaultPersonalLibraryService personalLibraryService ) {
        this.personalLibraryService = personalLibraryService;
    }

    public Combos getComboResults(CustomUserDetails userId) throws Exception {
       var cards = personalLibraryService.getCardsFromPersonalLibrary(userId.getId());
       return searchCombos(cards);
    }


public static Combos searchCombos(List<OwnedCard> cards) throws Exception {
    List<Map<String, Object>> mainBoard = cards.stream()
            .map(card -> {
                Map<String, Object> entry = new HashMap<>();
                entry.put("card", card.getCard().getName());
                entry.put("quantity", 1);
                return entry;
            })
            .collect(Collectors.toList());

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("main", mainBoard);
    requestBody.put("commanders", List.of());

    String body = objectMapper.writeValueAsString(requestBody);

    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() != 200) {
        throw new RuntimeException("API error: HTTP " + response.statusCode());
    }

    return objectMapper.readValue(response.body(), Combos.class);
}
    public static String findMyCombosForCards(List<String> mainBoard, List<String> commanders) throws Exception {
        StringBuilder query = new StringBuilder("format=json");

        for (String card : mainBoard) {
            query.append("&main_board%5B%5D=")
                    .append(URLEncoder.encode(card, StandardCharsets.UTF_8));
        }

        for (String card : commanders) {
            query.append("&commanders%5B%5D=")
                    .append(URLEncoder.encode(card, StandardCharsets.UTF_8));
        }

        URI uri = URI.create(BASE_URL + "?" + query);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("API error: HTTP " + response.statusCode());
        }

        return response.body();
    }

}
