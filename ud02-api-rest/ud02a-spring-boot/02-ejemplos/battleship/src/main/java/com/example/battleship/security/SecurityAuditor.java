package com.example.battleship.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.stereotype.Component;

@Component
public class SecurityAuditor {

    private static final Logger log = LoggerFactory.getLogger(SecurityAuditor.class);

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        log.info("LOGIN_OK: user={}", event.getAuthentication().getName());
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        log.warn("LOGIN_FAIL: user={}, reason={}",
                event.getAuthentication().getName(),
                event.getException().getMessage());
    }

    @EventListener
    public void onDenied(AuthorizationDeniedEvent<?> event) {
        log.warn("ACCESS_DENIED: user={}",
                event.getAuthentication().get().getName());
    }
}
