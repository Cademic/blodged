package com.gcu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.gcu.business.AuthService;

/**
 * event handler for authentication
 */
@Component
public class AuthEvents {

    private @Autowired AuthService authService;

    /**
     * listens to spring security logins and completes the login using our service once it finishes
     * @param success the event to track
     */
    @EventListener
    public void onSuccess(AuthenticationSuccessEvent success) {
        UserDetails u = (UserDetails) success.getAuthentication().getPrincipal();
        authService.completeLogin(u);
    }

}