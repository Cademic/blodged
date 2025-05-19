package com.blodged.model.db.metadata;
/**
 * Model object for a post... why is this not merged with PostModel?
 */
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
/**
 * getter for id    
 * @return returns the id of the post
 */
    public long getId() {
        return id;
    }
/**
 * setter for id
 * @param id the new id of the post
 */
    public void setId(long id) {
        this.id = id;
    }
/**
 * getter for authorUsername
 * @return returns the username of the author
 */
    public String getAuthorUsername() {
        return authorUsername;
    }
/**
 * setter for authorUsername
 * @param authorUsername the username of the new author
 */
    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }
/**
 * getter for postContent
 * @return returns content of the post
 */
    public String getPostContent() {
        return postContent;
    }
/**
 * setter for postContent
 * @param postContent the new content of the post
 */
    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }
/**
 * getter for replyIds
 * @return returns an array of longs that contains the ids of the replies
 */
    public long[] getReplyIds() {
        return replyIds;
    }
/**
 * setter for replyIds
 * @param replyIds array of longs that contains the ids of the replies
 */
    public void setReplyIds(long[] replyIds) {
        this.replyIds = replyIds;
    }
/**
 * getter for likeCount
 * @return returns the amount of likes the post has
 */
    public int getLikeCount() {
        return likeCount;
    }
/**
 * setter for likeCount
 * @param likeCount the new amount of like the post has
 */
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
