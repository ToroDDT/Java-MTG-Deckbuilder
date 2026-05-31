package com.example.mtg_deckbuilder.cache;

import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.repository.api.PersonalLibraryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import java.util.List;
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

    @Cacheable("userPaginated")
    public List<OwnedCard> getAllCardsPaginated(UUID user) {
        return repository.findCardsPaginated(user);
    }

    @Cacheable(value = "userCards", key = "{#user, #libraryFilters}")
    public List<OwnedCard> getAllCards(UUID user, LibraryFilters libraryFilters) {
        return repository.findCards(user, libraryFilters);
    }

    @Caching(evict = {
            @CacheEvict(value = "user", key = "#userId"),
            @CacheEvict(value = "userPaginated", key = "#userId"),
            @CacheEvict(value = "userCards", allEntries = true)
    })
    public void evictForUser(UUID userId) {
        // Eviction is applied by Spring Cache AOP.
    }
}
