package com.blodged.data.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blodged.data.entities.FollowsEntity;
import com.blodged.data.repository.FollowsRepo;
import com.blodged.model.db.UserModel;
/**
 * Business logic class that handles follows
 */
@Service
public class FollowsBusinessService {
    
    private @Autowired FollowsRepo service;
    private @Autowired UserBusinessService userService;
/**
 * Checks if a user is following another user
 * @param userFollowing check if this user is following the other user
 * @param userFollowed  check if the other user is following this user
 * @return true or false 
 */
    public boolean doesUserFollow(UserModel userFollowing, UserModel userFollowed) {
        return doesUserFollow(userFollowing.getId(), userFollowed.getId());
    }
    /**
 * Checks if a user is following another user based on user id
 * @param userFollowing check if this user is following the other user
 * @param userFollowed  check if the other user is following this user
 * @return true or false 
 */
    public boolean doesUserFollow(long userFollowing, long userFollowed) {
        FollowsEntity follow = service.doesUserFollow(userFollowing, userFollowed);
        return follow != null;
    }
/**
 * Method to follow another user based on userid
 * @param userFollowing the user that needs to follow
 * @param userFollowed the user that is being followed
 */
    public void follow(long userFollowing, long userFollowed) {
        FollowsEntity fe = new FollowsEntity(userFollowing, userFollowed);
        service.save(fe);
    }
    /**
 * Method to follow another user 
 * @param userFollowing the user that needs to follow
 * @param userFollowed the user that is being followed
 */
    public void follow(UserModel userFollowing, UserModel userFollowed) {
        follow(userFollowing.getId(), userFollowed.getId());
    }
    /**
     * Method to unfollow another user based on user id
     * @param userFollowing the user that needs to unfollow
     * @param userFollowed the user that is being unfollowed
     */
    public void unfollow(long userFollowing, long userFollowed) {
        FollowsEntity fe = service.doesUserFollow(userFollowing, userFollowed);
        if (fe != null) {
            service.unfollow(userFollowing, userFollowed);
        }
    }
    /**
     * method to get the amount of users a specific user is following
     * @param userId id of user to check
     * @return the count of users that is following
     */
    public int countFollowing(long userId) {
        return service.userIsFollowing(userId).size();
    }
    /**
     * Method to get the amount of followers a specific user has
     * @param userId the is of the user to check
     * @return the count of followers
     */
    public int countFollowers(long userId) {
        return service.userIsFollowedBy(userId).size();
    }
    /**
     * Returns a list of UserModel objects that the user is following
     * @param userId the id of the user
     * @return list of UserModel
     */
    public List<UserModel> userFollows(long userId) {
        List<FollowsEntity> follows = service.userIsFollowing(userId);
        List<UserModel> users = new java.util.ArrayList<>();
        for (FollowsEntity f : follows) {
            UserModel u = userService.getFromId(f.getUserFollowed());
            if (u != null) users.add(u);
        }
        return users;
    }
    /**
     * Method to unfollow all users
     * @param userId id of the user to remove all followers for
     */
    public void unfollowAll(long userId) {
        service.clearUser(userId);
    }
}
