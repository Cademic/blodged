package com.gcu.data.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public ReplyEntity(long userId, long postId, String content) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
    }


    

}
