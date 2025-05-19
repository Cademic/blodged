package com.blodged.data.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
/**
 * Reply model entity to interract with the database
 */
@Table("replies")
public class ReplyEntity {
    
    @Id
    private long id;

    @Column("post_id")
    private long postId;

    @Column("user_id")
    private long userId;

    @Column("content")
    private String content;
/**
 * getter for id
 * @return the id of the reply
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
 * getter for postId
 * @return the id of the parent post
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
 * getter for userId
 * @return the id of the user
 */
    public long getUserId() {
        return userId;
    }
/**
 * setter for userId
 * @param userId the new id of the user
 */
    public void setUserId(long userId) {
        this.userId = userId;
    }
/**
 * getter for content
 * @return the content of the reply
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


    public ReplyEntity(long userId, long postId, String content) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
    }


    

}
