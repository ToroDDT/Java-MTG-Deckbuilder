package com.example.mtg_deckbuilder.cache;

import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.repository.api.PersonalLibraryRepository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class PersonalLibraryCache {
    private final PersonalLibraryRepository repository;

    public PersonalLibraryCache(PersonalLibraryRepository repository) {
        this.repository = repository;
    }

    @Cacheable("user")
    public List<OwnedCard> getAllCards(UUID user) {
        return repository.findCards(user);
    }

    @Cacheable("userComboCards")
    public List<OwnedCard> getAllCardsForCombos(UUID user) {
        return repository.findCardsForCombos(user);
    }

    @Cacheable("userPaginated")
    public List<OwnedCard> getAllCardsPaginated(UUID user) {
        return repository.findCardsPaginated(user);
    }

    @Cacheable(value = "userCards", key = "{#user, #libraryFilters}")
    public List<OwnedCard> getAllCards(UUID user, LibraryFilters libraryFilters) {
        return repository.findCards(user, libraryFilters);
    }

    @Cacheable(value = "userCardExists", key = "{#userId, #cardId}")
    public Boolean findCard(UUID userId, String cardId) {
        return repository.findCardExists(userId, cardId);
    }

    @Cacheable(value = "userLibraryInfo", key = "#user.id")
    public List<OwnedCard> getInfo(CustomUserDetails user) {
        return repository.getInfo(user);
    }

    @Cacheable(value = "userCardLocations", key = "{#user.id, #cardIds}")
    public Map<UUID, List<String>> findLocations(CustomUserDetails user, List<UUID> cardIds) {
        return repository.findLocations(user, cardIds);
    }

    @Caching(evict = {
            @CacheEvict(value = "user", key = "#userId"),
            @CacheEvict(value = "userComboCards", key = "#userId"),
            @CacheEvict(value = "userPaginated", key = "#userId"),
            @CacheEvict(value = "userCards", allEntries = true),
            @CacheEvict(value = "userCardExists", allEntries = true),
            @CacheEvict(value = "userLibraryInfo", key = "#userId"),
            @CacheEvict(value = "userCardLocations", allEntries = true)
    })
    public void evictForUser(UUID userId) {
        // Eviction is applied by Spring Cache AOP.
    }
}
