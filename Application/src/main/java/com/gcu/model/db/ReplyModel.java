package com.gcu.model.db;

public class ReplyModel extends DBModel {

    private UserModel user;
    private String content;
    public UserModel getUser() {
        return user;
    }
    public void setUser(UserModel user) {
        this.user = user;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public ReplyModel(UserModel user, String content, long entityId) {
        super(entityId);
        this.user = user;
        this.content = content;
    }


    
}
