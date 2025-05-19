package com.blodged.data.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
/**
 * Follows model entity to interract with the database
 */
@Table("follows")
public class FollowsEntity {
    
    @Id
    private long id;
    @Column("user_following")
    private long userFollowing;
    @Column("user_followed")
    private long userFollowed;
/**
 * getter for userFollowing
 * @return the user that is doing the following
 */
    public long getUserFollowing() {
        return userFollowing;
    }
/**
 * getter for userFollowed
 * @return the user that is being followed
 */
    public long getUserFollowed() {
        return userFollowed;
    }

    public FollowsEntity(long userFollowing, long userFollowed) {
        this.userFollowing=userFollowing;
        this.userFollowed = userFollowed;
    }

}
