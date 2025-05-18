package com.gcu.model.db.metadata;

import java.sql.Timestamp;
/**
 * Model object that contains all data for the user... why isnt this merged with the UserModel?
 * 
 */
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
    /**
     * getter for id
     * @return returns the id
     */
    public long getId() {
        return id;
    }
    /**
     * setter for id
     * @param id the new id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * getter for username
     * @return returns the username
     */
    public String getUsername() {
        return username;
    }
    /**
     * setter for username
     * @param username the new username
     */
    public void setUsername(String username) {
        this.username = username;
    }
    /**
     * getter for bio
     * @return returns the bio
     */
    public String getBio() {
        return bio;
    }
    /**
     * setter for bio
     * @param bio the new bio 
     */
    public void setBio(String bio) {
        this.bio = bio;
    }
/**
 * getter for createdDate
 * @return object of type Timestamp that contains the created date
 */
    public Timestamp getCreatedDate() {
        return createdDate;
    }
/**
 * setter for createdDate
 * @param createdDate object of type Timestamp that is the new created date
 */
    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }
/**
 * getter for followedByCount
 * @return returns the number of users that is following this user
 */
    public int getFollowedByCount() {
        return followedByCount;
    }
/**
 * setter for followedByCount
 * @param followedByCount the number of users that is following this user
 */
    public void setFollowedByCount(int followedByCount) {
        this.followedByCount = followedByCount;
    }
/**
 * getter for followingCount
 * @return returns the number of users that this user is following
 */
    public int getFollowingCount() {
        return followingCount;
    }
/**
 * setter for followingCount
 * @param followingCount the number of users that this user is following
 */
    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }
/**
 * getter for likesGiven
 * @return returns the number of likes this user has given out
 */
    public int getLikesGiven() {
        return likesGiven;
    }
/**
 * setter for likesGiven
 * @param likesGiven the new number of likes that this user has given out
 */
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
