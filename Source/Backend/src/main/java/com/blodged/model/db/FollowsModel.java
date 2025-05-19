package com.blodged.model.db;
/**
 * A model object for holding the follow counts for each user
 */
public class FollowsModel extends DBModel {

    private int userFollowing;
    private int userFollows;
    /**
     * getter for userFollowing
     * @return the number of users the user is following
     */
    public int getUserFollowing() {
        return userFollowing;
    }
    /**
     * setter for userFollowing
     * @param userFollowing the new number of users that this user is following
     */
    public void setUserFollowing(int userFollowing) {
        this.userFollowing = userFollowing;
    }
    /**
     * getter for userFollows
     * @return the number of users that are following the user
     */
    public int getUserFollows() {
        return userFollows;
    }
    /**
     * setter for userFollows
     * @param userFollows the new number of users that are following the user
     */
    public void setUserFollows(int userFollows) {
        this.userFollows = userFollows;
    }
    public FollowsModel(int id, int userFollowing, int userFollows) {
        super(id);
        this.userFollowing = userFollowing;
        this.userFollows = userFollows;
    }
}
