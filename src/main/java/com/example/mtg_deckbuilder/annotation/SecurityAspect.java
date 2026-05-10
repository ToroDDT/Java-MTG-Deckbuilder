package com.example.mtg_deckbuilder.annotation;

import com.example.mtg_deckbuilder.security.CustomUserDetails;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;


@Aspect
@Component
public class SecurityAspect {

    @Around("@annotation(com.example.mtg_deckbuilder.annotation.RequiresAuth)")
    public Object checkUser(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg instanceof CustomUserDetails user) {
                if (user.getId() == null) {
                    throw new AuthenticationException("You must be logged in to do this.");
                }
                return joinPoint.proceed();
            }
        }
        throw new AuthenticationException("Security context missing.");
    }
}
