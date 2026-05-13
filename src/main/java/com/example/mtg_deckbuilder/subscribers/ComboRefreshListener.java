package com.example.mtg_deckbuilder.subscribers;

import com.example.mtg_deckbuilder.service.impl.ComboServiceImpl;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ComboRefreshListener {
    private  final ComboServiceImpl comboService;

    public ComboRefreshListener(ComboServiceImpl comboService) {
        this.comboService = comboService;
    }

    @EventListener
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onLibraryUpdated(LibraryUpdatedEvent event) throws Exception {
        var combosList = comboService.findCombos(
            event.getUser()
        );
        comboService.saveCombos(event.getUser(), combosList);
        // cache it, push via websocket, etc.
    }
}