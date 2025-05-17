package com.gcu.model.db;

public class FollowsModel extends DBModel {

    private int userFollowing;
    private int userFollows;
    public int getUserFollowing() {
        return userFollowing;
    }
    public void setUserFollowing(int userFollowing) {
        this.userFollowing = userFollowing;
    }
    public int getUserFollows() {
        return userFollows;
    }
    public void setUserFollows(int userFollows) {
        this.userFollows = userFollows;
    }
    public FollowsModel(int id, int userFollowing, int userFollows) {
        super(id);
        this.userFollowing = userFollowing;
        this.userFollows = userFollows;
    }
}
