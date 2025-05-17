package com.gcu.model.db.metadata;

import java.sql.Timestamp;

public class UserMetadata {

    public UserMetadata(long id, String username, String bio, Timestamp createdDate, int followedByCount, int followingCount, int likesGiven) {
        this.id = id;
        this.username = username;
        this.bio = bio;
        this.createdDate = createdDate;
        this.followedByCount = followedByCount;
        this.followingCount = followingCount;
        this.likesGiven = likesGiven;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public int getFollowedByCount() {
        return followedByCount;
    }

    public void setFollowedByCount(int followedByCount) {
        this.followedByCount = followedByCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public int getLikesGiven() {
        return likesGiven;
    }

    public void setLikesGiven(int likesGiven) {
        this.likesGiven = likesGiven;
    }

    private long id;
    private String username;
    private String bio;
    private Timestamp createdDate;
    private int followedByCount;
    private int followingCount;
    private int likesGiven;



}
