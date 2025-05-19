package com.blodged.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.blodged.business.AuthService;
import com.blodged.data.service.UserBusinessService;
import com.blodged.model.db.UserModel;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiTokenAuthFilter extends OncePerRequestFilter {

    @Autowired
    private UserBusinessService userService;
    
    @Autowired
    private AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Skip filter for auth endpoints
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/") || !path.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Check for token in cookies
        String token = null;
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        
        // If no token in cookies, check for token in Authorization header
        if (token == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }
        
        // Validate token and set up authentication
        if (token != null && token.startsWith("mock-token-")) {
            try {
                // Extract user ID from token (our token format is "mock-token-{userId}")
                String userId = token.substring("mock-token-".length());
                Long id = Long.parseLong(userId);
                
                // Get user and set up authentication
                UserModel user = userService.getFromId(id);
                if (user != null) {
                    UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    // Also update the AuthService
                    authService.completeLogin(userDetails);
                }
            } catch (Exception e) {
                // Invalid token format or user not found
                System.err.println("Token validation failed: " + e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
} 