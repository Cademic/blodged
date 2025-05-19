package com.blodged.controller.REST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blodged.business.AuthService;
import com.blodged.data.service.UserBusinessService;
import com.blodged.model.db.UserModel;
import com.blodged.model.request.LoginModel;
import com.blodged.model.request.RegisterModel;
import com.blodged.model.response.AuthResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserBusinessService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(summary = "Login a user", responses = {
        @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginModel loginModel) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginModel.getEmail(), loginModel.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserModel user = userService.getByEmail(loginModel.getEmail());
            
            AuthResponse response = new AuthResponse(
                user.getId(), 
                user.getEmail(), 
                user.getUsername(),
                user.getRole() // Use the role from the user model
            );
            
            // Return a JSON object with 'user' and 'token' fields to match the frontend expectations
            return ResponseEntity.ok(java.util.Map.of(
                "user", response,
                "token", "mock-token-" + user.getId() // In a real app, generate a proper JWT token here
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
    
    @Operation(summary = "Register a new user", responses = {
        @ApiResponse(responseCode = "200", description = "Registration successful", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Registration failed")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterModel registerModel) {
        // Check if user already exists
        if (userService.getByEmail(registerModel.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Email already in use");
        }
        
        try {
            // Create the user with encoded password
            UserModel user = userService.create(
                registerModel.getUsername(), 
                passwordEncoder.encode(registerModel.getPassword()), 
                registerModel.getEmail()
            );
            
            // Auto-login the user after registration
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(registerModel.getEmail(), registerModel.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            AuthResponse response = new AuthResponse(
                user.getId(), 
                user.getEmail(), 
                user.getUsername(),
                user.getRole() // Use the role from the user model
            );
            
            // Return a JSON object with 'user' and 'token' fields
            return ResponseEntity.ok(java.util.Map.of(
                "user", response,
                "token", "mock-token-" + user.getId() // Use the actual user ID just like in login
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }
} 