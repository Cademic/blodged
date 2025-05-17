package com.gcu.model.db;

import java.sql.Timestamp;

public class UserModel extends DBModel {
    private String password;
    private String username;
    private String email;
    private String bio;
    private Timestamp createdDate;
    private boolean isPrivate;
    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }
    public Timestamp getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }
    public boolean isPrivate() {
        return isPrivate;
    }
    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
    public UserModel(String password, String username, String email, long entityId, String bio, Timestamp createdDate, boolean isPrivate) {
        super(entityId);
        this.password = password;
        this.username = username;
        this.email = email;
        this.bio=bio;
        this.createdDate=createdDate;
        this.isPrivate=isPrivate;
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
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
