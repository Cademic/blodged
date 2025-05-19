package com.blodged.config;

import com.blodged.data.service.UserBusinessService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;
import java.util.Objects;

/**
 * listens to the login page to track status events
 */
public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final UserBusinessService service;

    public AuthFailureHandler(UserBusinessService service) {
        this.service=service;
    }

    /**
     * listens to authentication failures in order to give error messages when incorrect info is entered
     * @param req the request during which the authentication attempt occurred.
     * @param res the response.
     * @param ex the exception which was thrown to reject the authentication
     * request.
     * @throws IOException
     */
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
