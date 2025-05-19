package com.blodged.data.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Post model entity to interract with the database
 */
@Table("POSTS")
public class PostEntity {
    
    @Id
    long id = 0;

    @Column("post_content")
    String postContent;

    @Column("post_user_id")
    long postUserId;
/**
 * getter for id
 * @return the id of the post
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
 * getter for postContent
 * @return the content of the post
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
 * getter for postUserId
 * @return the id of the user that made the post
 */
    public long getPostUserId() {
        return postUserId;
    }
/**
 * setter for postUserId
 * @param postUserId the new id of the user that made the post
 */
    public void setPostUserId(long postUserId) {
        this.postUserId = postUserId;
    }

    public PostEntity(String postContent, long postUserId) {
        this.postContent = postContent;
        this.postUserId = postUserId;
    }

}
