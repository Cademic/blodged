package com.gcu.model.request;


import jakarta.validation.constraints.Size;

public class UpdateUserModel {

    private long userId;
    private String keyToUpdate;

    @Size(min=1, max=200, message = "Cannot be longer than 200 characters!")
    private String valueToUpdate;
    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
    public String getKeyToUpdate() {
        return keyToUpdate;
    }
    public void setKeyToUpdate(String keyToUpdate) {
        this.keyToUpdate = keyToUpdate;
    }
    public String getValueToUpdate() {
        return valueToUpdate;
    }
    public void setValueToUpdate(String valueToUpdate) {
        this.valueToUpdate = valueToUpdate;
    }
    public UpdateUserModel(String password, long userId, String keyToUpdate, String valueToUpdate) {
        this.userId = userId;
        this.keyToUpdate = keyToUpdate;
        this.valueToUpdate = valueToUpdate;
    }

    
    
}
