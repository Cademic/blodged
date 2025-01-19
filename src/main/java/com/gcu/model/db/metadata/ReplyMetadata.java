package com.gcu.model.db.metadata;

public class ReplyMetadata {

    private long id;
    private String replyAuthorUsername;
    private long postId;
    private String replyContent;

    public ReplyMetadata(long id, String replyAuthorUsername, long postId, String replyContent) {
        this.id = id;
        this.replyAuthorUsername = replyAuthorUsername;
        this.postId = postId;
        this.replyContent = replyContent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReplyAuthorUsername() {
        return replyAuthorUsername;
    }

    public void setReplyAuthorUsername(String replyAuthorUsername) {
        this.replyAuthorUsername = replyAuthorUsername;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }
}

