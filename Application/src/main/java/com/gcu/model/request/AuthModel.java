package com.gcu.model.request;

public class AuthModel {
    private long userId;
    private String password;
    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public AuthModel(long userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    
}
