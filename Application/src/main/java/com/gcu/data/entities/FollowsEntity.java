package com.gcu.data.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("follows")
public class FollowsEntity {
    
    @Id
    private long id;
    @Column("user_following")
    private long userFollowing;
    @Column("user_followed")
    private long userFollowed;

    public long getUserFollowing() {
        return userFollowing;
    }

    public long getUserFollowed() {
        return userFollowed;
    }

    public FollowsEntity(long userFollowing, long userFollowed) {
        this.userFollowing=userFollowing;
        this.userFollowed = userFollowed;
    }

}
