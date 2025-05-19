package com.blodged.model.request;
/**
 * A model class that thymeleaf uses with forms to store data in. 
 * An instance of this class is sent to the POST controller with the data from the html form. 
 * This model is used to get the information for deleting an account from the html form
 */
public class DeleteAccountModel {
    
    private long userId;
    private boolean deletePosts;
    public DeleteAccountModel(long userId, boolean deletePosts) {
        this.userId = userId;
        this.deletePosts = deletePosts;
    }
    /**
     * getter for isDeletePosts
     * @return a boolean of whether to delete the posts or not
     */
    public boolean isDeletePosts() {
        return deletePosts;
    }
    /**
     * setter for isDeletePosts
     * @param deletePosts boolean of whether you want to delete the posts or not
     */
    public void setDeletePosts(boolean deletePosts) {
        this.deletePosts = deletePosts;
    }
    /**
     * getter for userId
     * @return the user id of the user that is being deleted
     */
    public long getUserId() {
        return userId;
    }
    /**
     * setter for userId
     * @param userId sets the userId of the user that is being deleted
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }
}
