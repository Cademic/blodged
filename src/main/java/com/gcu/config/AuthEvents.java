package com.gcu.config;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.gcu.business.AuthService;

@Component
public class AuthEvents {

    private @Autowired AuthService authService;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent success) {
        UserDetails u = (UserDetails) success.getAuthentication().getPrincipal();
        authService.completeLogin(u);
    }

}