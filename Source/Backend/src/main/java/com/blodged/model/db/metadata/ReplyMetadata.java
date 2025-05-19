package com.blodged.model.db.metadata;
/**
 * Model object for replies... why isnt this merged with ReplyModel?
 */
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
/**
 * getter for id
 * @return returns the id of the reply
 */
    public long getId() {
        return id;
    }
/**
 * setter for id
 * @param id the new id of the reply
 */
    public void setId(long id) {
        this.id = id;
    }
/**
 * getter for replyAuthorUsername
 * @return returns the username of the author of the reply
 */
    public String getReplyAuthorUsername() {
        return replyAuthorUsername;
    }
/**
 * setter for replyAuthorUsername
 * @param replyAuthorUsername the username of the new author
 */
    public void setReplyAuthorUsername(String replyAuthorUsername) {
        this.replyAuthorUsername = replyAuthorUsername;
    }
/**
 * getter for postId
 * @return returnst he id of the parent post
 */
    public long getPostId() {
        return postId;
    }
/**
 * setter for postId
 * @param postId the new id of the parent post
 */
    public void setPostId(long postId) {
        this.postId = postId;
    }
/**
 * getter for replyContent
 * @return
 */
    public String getReplyContent() {
        return replyContent;
    }
/**
 * setter for replyContent
 * @param replyContent the new content of the reply
 */
    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }
}

