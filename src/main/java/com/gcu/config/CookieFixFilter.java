package com.gcu.config;

import com.gcu.business.AuthService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
@Order(1)
public class CookieFixFilter implements Filter {

    private @Autowired AuthService authService;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        if (!authService.hasLoggedIn && !authService.hasClearedSession) {
            SecurityContextHolder.clearContext();
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            authService.hasClearedSession = true;
            res.sendRedirect("/accounts/login");
        } else {
            chain.doFilter(request, response);
        }
    }
}
