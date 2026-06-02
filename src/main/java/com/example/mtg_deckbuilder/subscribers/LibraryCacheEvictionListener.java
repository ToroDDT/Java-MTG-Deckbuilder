package com.example.mtg_deckbuilder.subscribers;

import com.example.mtg_deckbuilder.cache.PersonalLibraryCache;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class LibraryCacheEvictionListener {
    private final PersonalLibraryCache personalLibraryCache;

    public LibraryCacheEvictionListener(PersonalLibraryCache personalLibraryCache) {
        this.personalLibraryCache = personalLibraryCache;
    }

    @EventListener
    public void onLibraryUpdated(LibraryUpdatedEvent event) {
        personalLibraryCache.evictForUser(event.getUser().getId());
    }
}
