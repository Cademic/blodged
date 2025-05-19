package com.blodged.data.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
/**
 * Likes model entity to interract with the db
 */
@Table("likes")
public class LikeEntity {

    @Id
    private long id;
    @Column("user_id")
    private long userId;
    @Column("post_id")
    private long postId;
/**
 * getter for id
 * @return the id of the like entry
 */
    public long getId() {
        return id;
    }
    /**
     * setter for id
     * @param id the new id of the like entry
     */
    public void setId(long id) {
        this.id = id;
    }
    /**
     * getter for userId
     * @return the id of the user who liked
     */
    public long getUserId() {
        return userId;
    }
    /**
     * setter for userId
     * @param userId the new id of the user who liked
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
    /**
     * getter for postId
     * @return the id of the post that was liked
     */
    public long getPostId() {
        return postId;
    }
    /**
     * setter for postId
     * @param postId the new id of the post that was liked
     */
    public void setPostId(long postId) {
        this.postId = postId;
    }
    public LikeEntity(long userId, long postId) {
        this.userId = userId;
        this.postId = postId;
    }
}
