package com.gcu.model.request;

/**
 * the model for checking if a user can authenticate
 */
public class AuthModel {
    private long userId;
    private String password;

    /**
     * get the user ID of the user trying to authenticate
     * @return database ID of the user
     */
    public long getUserId() {
        return userId;
    }

    /**
     * set the user id
     * @param userId new user id
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }
    /**
     * get the password of the user trying to authenticate
     * @return un-encoded password of the user
     */
    public String getPassword() {
        return password;
    }
    /**
     * set the password
     * @param password new password
     */
    public void setPassword(String password) {
        this.password = password;
    }
    public AuthModel(long userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    
}
