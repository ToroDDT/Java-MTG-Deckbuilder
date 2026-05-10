package com.example.mtg_deckbuilder.annotation;

import com.example.mtg_deckbuilder.security.CustomUserDetails;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;

@Aspect
@Component
public class SecurityAspect {

    @Around("@annotation(com.example.mtg_deckbuilder.annotation.RequiresAuth)")
    public Object checkUser(ProceedingJoinPoint joinPoint) throws Throwable {
        // Look through the method arguments for the CustomUserDetails
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg instanceof CustomUserDetails user) {
                if (user.getId() == null) {
                    throw new InsufficientAuthenticationException("You must be logged in to do this.");
                }
                return joinPoint.proceed(); // User is fine, continue to the method
            }
        }

        throw new InsufficientAuthenticationException("Security context missing.");
    }
}
