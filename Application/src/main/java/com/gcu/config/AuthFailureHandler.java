package com.gcu.config;

import com.gcu.data.service.UserBusinessService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;
import java.util.Objects;

public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final UserBusinessService service;

    public AuthFailureHandler(UserBusinessService service) {
        this.service=service;
    }
    @Override // this should be rate limited
    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException ex) throws IOException {
        String error = ex.getMessage();
        if (Objects.equals(ex.getMessage(), "Bad credentials")) {
            String email = req.getParameter("email");
            if (service.emailExists(email)) {
                error = "Incorrect password!";
            } else {
                error = "Email doesn't exist!";
            }
        }
        res.sendRedirect("/accounts/login?error=" + error);
    }
}
