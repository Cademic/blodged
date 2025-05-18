package com.gcu.model.db;

import java.sql.Timestamp;
/**
 * Model object for a user
 */
public class UserModel extends DBModel {
    private String password;
    private String username;
    private String email;
    private String bio;
    private Timestamp createdDate;
    private boolean isPrivate;
    private String gravatarEmail = null;

    /**
     * getter for bio
     * @return the contents of the user's bio
     */
    public String getBio() {
        return bio;
    }
    /**
     * setter for bio
     * @param bio the new contents of the bio.
     */
    public void setBio(String bio) {
        this.bio = bio;
    }
    /**
     * getter for createdDate
     * @return returns a Timestamp object which contains the date at which this user was created
     */
    public Timestamp getCreatedDate() {
        return createdDate;
    }
    /**
     * setter for createdDate
     * @param createdDate object of type Timestamp of the new date
     */
    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }
    /**
     * getter for isPrivate
     * @return a boolean of whether the user is a private user or not
     */
    public boolean isPrivate() {
        return isPrivate;
    }
    /**
     * setter for isPrivate
     * @param isPrivate the new value for whether a user is private or not
     */
    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
    public UserModel(String password, String username, String email, long entityId, String bio, Timestamp createdDate, boolean isPrivate, String gravatarUrl) {
        super(entityId);
        this.password = password;
        this.username = username;
        this.email = email;
        this.bio=bio;
        this.createdDate=createdDate;
        this.isPrivate=isPrivate;
        this.gravatarEmail =gravatarUrl;
    }
    public UserModel() {
    	super(-1);
    	this.password = null;
    	this.username = "invalid";
    	this.email = "invalid";
    	this.bio = "invalid";
    	this.createdDate = new Timestamp(0);
    	this.isPrivate = true;
    	
    }
    /**
     * getter for password
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    /**
     * setter for password
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * getter for username
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    /**
     * setter for username
     * @param username the new username
     */
    public void setUsername(String username) {
        this.username = username;
    }
    /**
     * getter for email
     * @return the email
     */
    public String getEmail() {
        return email;
    }
    /**
     * setter for email
     * @param email the new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * get the gravatar email of the user
     * @return email address
     */
    public String getGravatarEmail() {
        return gravatarEmail;
    }
    /**
     * set the gravatar email
     * @param gravatarEmail the email address
     */
    public void setGravatarEmail(String gravatarEmail) {
        this.gravatarEmail = gravatarEmail;
    }

}
