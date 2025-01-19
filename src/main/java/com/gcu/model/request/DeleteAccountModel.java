package com.gcu.model.request;

public class DeleteAccountModel {
    
    private long userId;
    private boolean deletePosts;
    public DeleteAccountModel(long userId, boolean deletePosts) {
        this.userId = userId;
        this.deletePosts = deletePosts;
    }
    public boolean isDeletePosts() {
        return deletePosts;
    }
    public void setDeletePosts(boolean deletePosts) {
        this.deletePosts = deletePosts;
    }
    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
}
