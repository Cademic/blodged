package com.gcu.data.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("likes")
public class LikeEntity {

    @Id
    private long id;
    @Column("user_id")
    private long userId;
    @Column("post_id")
    private long postId;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public long getPostId() {
        return postId;
    }
    public void setPostId(long postId) {
        this.postId = postId;
    }
    public LikeEntity(long userId, long postId) {
        this.userId = userId;
        this.postId = postId;
    }
}
