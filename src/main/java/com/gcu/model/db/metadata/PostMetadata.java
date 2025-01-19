package com.gcu.model.db.metadata;

public class PostMetadata {

    private long id;
    private String authorUsername;
    private String postContent;
    private long[] replyIds;
    private int likeCount;

    public PostMetadata(long id, String authorUsername, String postContent, long[] replyIds, int likeCount) {
        this.id = id;
        this.authorUsername = authorUsername;
        this.postContent = postContent;
        this.replyIds = replyIds;
        this.likeCount = likeCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public long[] getReplyIds() {
        return replyIds;
    }

    public void setReplyIds(long[] replyIds) {
        this.replyIds = replyIds;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
