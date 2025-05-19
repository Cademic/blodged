package com.blodged.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.blodged.business.AuthService;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * middleware which clears cookies in the case of the app restarting and the user still being on the page
 */
@Component
@Order(1)
public class CookieFixFilter implements Filter {

    private @Autowired AuthService authService;

    /**
     * middleware which clears cookies in the case of the app restarting and the user still being on the page
     * @param request  The request to process
     * @param response The response associated with the request
     * @param chain    Provides access to the next filter in the chain for this filter to pass the request and response
     *                     to for further processing
     * @throws IOException
     * @throws ServletException
     */
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
