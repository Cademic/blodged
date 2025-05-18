package com.gcu.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
/**
 * A model class that thymeleaf uses with forms to store data in. 
 * An instance of this class is sent to the POST controller with the data from the html form. 
 * This model is used to get the data for logging in from the html form
 */
public class LoginModel {

    @NotNull(message="Email is required")
    @Size(min=1,max=64,message="Email cannot be longer than 64")
    private String email;
    @NotNull(message="Password is required")
    @Size(min=1,max=64,message = "Password cannot be longer than 64")
    private String password;

    /**
     * getter for email
     * @return returns the email of the user
     */
    public String getEmail() {
        return email;
    }
    /**
     * setter for email
     * @param email the email that you want to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
/**
 * getter for password
 * @return returns the password of the user
 */
    public String getPassword() {
        return password;
    }
    /**
     * setter for password
     * @param password the password that you want to set
     */
    public void setPassword(String password) {
        this.password=password;
    }

}
