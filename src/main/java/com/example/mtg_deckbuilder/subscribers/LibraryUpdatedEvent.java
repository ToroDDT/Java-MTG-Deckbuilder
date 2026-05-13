package com.example.mtg_deckbuilder.subscribers;

import com.example.mtg_deckbuilder.security.CustomUserDetails;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LibraryUpdatedEvent extends ApplicationEvent {
    private final CustomUserDetails user;
    public LibraryUpdatedEvent(Object source, CustomUserDetails user) {
        super(source);
        this.user = user;
    }
}