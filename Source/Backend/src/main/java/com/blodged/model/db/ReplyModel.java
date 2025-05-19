package com.blodged.model.db;
/**
 * Model object for a reply
 */
public class ReplyModel extends DBModel {

    private UserModel user;
    private String content;
    /**
     * getter for user
     * @return an object of type UserModel that is the user that the reply belongs to
     */
    public UserModel getUser() {
        return user;
    }
    /**
     * setter for user
     * @param user the new user that the reply belongs to
     */
    public void setUser(UserModel user) {
        this.user = user;
    }
    /**
     * getter for content
     * @return returns the content of the reply
     */
    public String getContent() {
        return content;
    }
    /**
     * setter for content
     * @param content the new content for the reply
     */
    public void setContent(String content) {
        this.content = content;
    }
    public ReplyModel(UserModel user, String content, long entityId) {
        super(entityId);
        this.user = user;
        this.content = content;
    }


    
}
