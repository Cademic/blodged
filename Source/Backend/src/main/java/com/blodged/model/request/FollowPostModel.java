package com.blodged.model.request;
/**
 * A model class that thymeleaf uses with forms to store data in. 
 * An instance of this class is sent to the POST controller with the data from the html form. 
 * This model is used to get the id of the user one wants to follow.
 */
public class FollowPostModel {

    private long userToFollow;

    /**
     * getter for userToFollow
     * @return returns the id of the user that needs to be followed
     */
    public long getUserToFollow() {
        return userToFollow;
    }

    /**
     * setter for userToFollow
     * @param userToFollow the id of the user you want to follow
     */
    public void setUserToFollow(long userToFollow) {
        this.userToFollow = userToFollow;
    }
}
