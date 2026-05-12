package com.example.mtg_deckbuilder.repository.impl;

import com.example.mtg_deckbuilder.model.cards.ScryfallCardObject;
import com.example.mtg_deckbuilder.repository.api.CardRepository;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.jooq.generated.Tables.CARDS;

@Repository
public class CardRepositoryImpl implements CardRepository {

    private final DSLContext dslContext;

    public CardRepositoryImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public Optional<ScryfallCardObject> findById(UUID id) {
        return dslContext.selectFrom(CARDS)
                .where(CARDS.ID.eq(id))
                .fetchOptional(ScryfallCardObject::mapFromRecord);
    }

    @Override
    public Optional<ScryfallCardObject> findByName(String name) {
        return dslContext.selectFrom(CARDS)
                .where(CARDS.NAME.eq(name))
                .fetchOptional(ScryfallCardObject::mapFromRecord);
    }

    public List<ScryfallCardObject> findByCardsBySubstring(String name) {
        return dslContext.selectFrom(CARDS)
                .where(CARDS.NAME.containsIgnoreCase(name))
                .fetch(ScryfallCardObject::mapFromRecord);
    }

    public List<String> findLegalCommanderCards() {
        return dslContext.select(CARDS.NAME)
                .from(CARDS)
                .where(CARDS.TYPE_LINE.containsIgnoreCase("Legendary"))
                .and(CARDS.TYPE_LINE.containsIgnoreCase("Creature"))
                .fetch(CARDS.NAME);
    }

    public List<ScryfallCardObject> getCards(String sortingOrder, UUID lastId) {
        var pageSize = 12;

        // Handle dynamic sorting/direction safely
        var sortField = "ASC".equalsIgnoreCase(sortingOrder) ? CARDS.ID.asc() : CARDS.ID.desc();
        var condition = "ASC".equalsIgnoreCase(sortingOrder) ? CARDS.ID.gt(lastId) : CARDS.ID.lt(lastId);

        return dslContext.selectFrom(CARDS)
                .where(condition)
                .orderBy(sortField)
                .limit(pageSize)
                .fetch(ScryfallCardObject::mapFromRecord);
    }
}