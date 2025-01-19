package com.gcu.data.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table("POSTS")
public class PostEntity {
    
    @Id
    long id = 0;

    @Column("post_content")
    String postContent;

    @Column("post_user_id")
    long postUserId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public long getPostUserId() {
        return postUserId;
    }

    public void setPostUserId(long postUserId) {
        this.postUserId = postUserId;
    }

    public PostEntity(String postContent, long postUserId) {
        this.postContent = postContent;
        this.postUserId = postUserId;
    }

}
