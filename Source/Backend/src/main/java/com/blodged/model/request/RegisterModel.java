package com.blodged.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
/**
 * A model class that thymeleaf uses with forms to store data in. 
 * An instance of this class is sent to the POST controller with the data from the html form. 
 * This model is used to get the data for a new user from the html form
 */
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

    /**
     * getter for username
     * @return the username of the new user
     */
    public @NotNull(message = "username is required") @Size(min = 1, max = 64, message = "Username must be between 1 and 64 characters") String getUsername() {
        return username;
    }
    /**
     * setter for username
     * @param username sets the username of the new user
     */
    public void setUsername(@NotNull(message = "username is required") @Size(min = 1, max = 64, message = "Username must be between 1 and 64 characters") String username) {
        this.username = username;
    }
    /**
     * getter for password
     * @return returns the password of the new user
     */

    public @NotNull(message = "Password is required") @Size(min = 1, max = 64, message = "Password must be between 1 and 64 characters") String getPassword() {
        return password;
    }

    /**
     * setter for password
     * @param password sets the password of the new user
     */
    public void setPassword(@NotNull(message = "Password is required") @Size(min = 1, max = 64, message = "Password must be between 1 and 64 characters") String password) {
        this.password = password;
    }
    /**
     * getter for email
     * @return returns the email of the new user
     */
    public @NotNull(message = "Email is required") @Size(min = 1, max = 64, message = "Email must be between 1 and 64 characters") String getEmail() {
        return email;
    }

    /**
     * setter for email
     * @param email sets the email of the new user
     */
    public void setEmail(@NotNull(message = "Email is required") @Size(min = 1, max = 64, message = "Email must be between 1 and 64 characters") String email) {
        this.email = email;
    }
}
