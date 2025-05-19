package com.blodged.model.request;


import jakarta.validation.constraints.Size;

/**
 * A model class that thymeleaf uses with forms to store data in. 
 * An instance of this class is sent to the POST controller with the data from the html form.  
 * This Model is used for updating users.
 */
public class UpdateUserModel {

    private long userId;
    private String keyToUpdate;

    @Size(min=1, max=200, message = "Cannot be longer than 200 characters!")
    private String valueToUpdate;
    
    public long getUserId() {
        return userId;
    }
    /**
     * setter for userId
     * @param userId the user ID to set
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }
    /**
     * getter for keyToUpdate
     * @return the key that needs to be updated
     */
    public String getKeyToUpdate() {
        return keyToUpdate;
    }
    /**
     * setter for keyToUpdate
     * @param keyToUpdate the key that you want to set
     */
    public void setKeyToUpdate(String keyToUpdate) {
        this.keyToUpdate = keyToUpdate;
    }
    /**
     * getter for valueToUpdate
     * @return returns that value that needs to be updated
     */
    public String getValueToUpdate() {
        return valueToUpdate;
    }
    /**
     * setter for valueToUpdate
     * @param valueToUpdate the value that you want to set
     */
    public void setValueToUpdate(String valueToUpdate) {
        this.valueToUpdate = valueToUpdate;
    }
    /**
     * Non-default constructor for UpdateUserModel
     * @param password password for user
     * @param userId id of the user
     * @param keyToUpdate the key that you want to update
     * @param valueToUpdate the value taht you want to update
     */
    public UpdateUserModel(String password, long userId, String keyToUpdate, String valueToUpdate) {
        this.userId = userId;
        this.keyToUpdate = keyToUpdate;
        this.valueToUpdate = valueToUpdate;
    }

    
    
}
