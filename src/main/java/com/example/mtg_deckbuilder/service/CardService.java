package com.example.mtg_deckbuilder.service;

import com.example.mtg_deckbuilder.model.CardType;
import com.example.mtg_deckbuilder.repository.CardRepository;
import com.example.mtg_deckbuilder.utils.ColorIdentityParser;
import com.example.mtg_deckbuilder.utils.Inequality;
import com.example.mtg_deckbuilder.utils.Parser;
import com.example.mtg_deckbuilder.utils.InequalityParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final Parser<Inequality> queryParser = new InequalityParser();

    @Autowired
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public void findByTypeLine(StringBuilder sql, Map<String, Object> params, String typeLine) {
        CardType type = CardType.fromString(typeLine);
        // If the input was valid, use the Enum's internal string
        if (type != null) {
            sql.append(" AND type_line = :type");
            params.put("type", type.getType());
        }
    }
    public void findByCmc(StringBuilder sql, Map<String, Object> params, String cmcInput) {
        Inequality expression = queryParser.parse(cmcInput);
        if (expression == null) return;
        String query = " AND cmc " + expression.operator() + " :cmc";
        params.put("cmc", expression.value());
        sql.append(query);
    }
    public void findByEdhrecRank(StringBuilder sql, Map<String, Object> params,String edhrecRank) {
        Inequality expression = queryParser.parse(edhrecRank);
        if (expression == null) return;
        String query = " AND edhrec_rank " + expression.operator() + " :edhrec_rank";
        sql.append(query);
        params.put("edhrec_rank", expression.value());
    }
    public void findByManaCost(StringBuilder sql,  Map<String, Object> params, String manaInput) {
        ColorIdentityParser colorIdentityParser = new ColorIdentityParser();
        List<String> symbols = colorIdentityParser.parseManaSymbols(manaInput);

        if (!symbols.isEmpty()) {
            for (int i = 0; i < symbols.size(); i++) {
                String paramName = "mana" + i;
                // Use LIKE to find the symbol anywhere in the mana_cost column
                sql.append(" AND mana_cost LIKE CONCAT('%', :").append(paramName).append(", '%')");
                params.put(paramName, symbols.get(i));
            }
        }
    }
    public void executeComplexQuery(String cmcInput, String manaInput, String typeLineInput, String edhrecInput) {
        StringBuilder sql = new StringBuilder("SELECT * FROM card WHERE 1=1");
        Map<String, Object> params = new HashMap<>();
        findByCmc(sql, params, cmcInput);
        findByEdhrecRank(sql, params, edhrecInput);
        findByTypeLine(sql, params, typeLineInput);
    }
}
