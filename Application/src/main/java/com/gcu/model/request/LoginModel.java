package com.gcu.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LoginModel {

    @NotNull(message="Email is required")
    @Size(min=1,max=64,message="Email cannot be longer than 64")
    private String email;
    @NotNull(message="Password is required")
    @Size(min=1,max=64,message = "Password cannot be longer than 64")
    private String password;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password=password;
    }

}
