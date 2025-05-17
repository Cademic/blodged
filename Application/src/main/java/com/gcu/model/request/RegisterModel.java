package com.gcu.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterModel {
    @NotNull(message = "username is required")
    @Size(min=1,max=64,message = "Username must be between 1 and 64 characters")
    private String username;
    @NotNull(message = "Password is required")
    @Size(min=1,max=64,message = "Password must be between 1 and 64 characters")
    private String password;
    @NotNull(message = "Email is required")
    @Size(min=1,max=64,message = "Email must be between 1 and 64 characters")
    private String email;


    public @NotNull(message = "username is required") @Size(min = 1, max = 64, message = "Username must be between 1 and 64 characters") String getUsername() {
        return username;
    }

    public void setUsername(@NotNull(message = "username is required") @Size(min = 1, max = 64, message = "Username must be between 1 and 64 characters") String username) {
        this.username = username;
    }

    public @NotNull(message = "Password is required") @Size(min = 1, max = 64, message = "Password must be between 1 and 64 characters") String getPassword() {
        return password;
    }

    public void setPassword(@NotNull(message = "Password is required") @Size(min = 1, max = 64, message = "Password must be between 1 and 64 characters") String password) {
        this.password = password;
    }
    public @NotNull(message = "Email is required") @Size(min = 1, max = 64, message = "Email must be between 1 and 64 characters") String getEmail() {
        return email;
    }

    public void setEmail(@NotNull(message = "Email is required") @Size(min = 1, max = 64, message = "Email must be between 1 and 64 characters") String email) {
        this.email = email;
    }
}
