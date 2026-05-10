package com.example.mtg_deckbuilder.advice;

import com.example.mtg_deckbuilder.model.LibraryFilters;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Aspect
@Component
public class SanitizationAspect {

    @Before("execution(* com.example.mtg_deckbuilder.controllers..*(.., @Sanitize (*), ..))")
    public void sanitizeFilters(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof LibraryFilters filters) {
                performSanitization(filters);
            }
        }
    }

    private void performSanitization(LibraryFilters filters) {
        // Handle Strings: Convert null to ""
        if (filters.getCardName() == null) filters.setCardName("");
        if (filters.getCardType() == null) filters.setCardType("All");
        if (filters.getTagSearch() == null) filters.setTagSearch("");

        // Handle List: Ensure it's not null (important for MTG color checkboxes)
        if (filters.getSelectedColors() == null) {
            filters.setSelectedColors(new ArrayList<>());
        }

        // Handle Default Numeric Ranges for MTG CMC
        if (filters.getMinCMC() == null) filters.setMinCMC(0);
        if (filters.getMaxCMC() == null) filters.setMaxCMC(16);
    }
}
