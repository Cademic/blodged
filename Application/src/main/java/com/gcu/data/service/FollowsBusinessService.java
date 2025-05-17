package com.gcu.data.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gcu.data.entities.FollowsEntity;
import com.gcu.data.repository.FollowsRepo;
import com.gcu.data.service.UserBusinessService;
import com.gcu.model.db.UserModel;

@Service
public class FollowsBusinessService {
    
    private @Autowired FollowsRepo service;
    private @Autowired UserBusinessService userService;

    public boolean doesUserFollow(UserModel userFollowing, UserModel userFollowed) {
        return doesUserFollow(userFollowing.getId(), userFollowed.getId());
    }
    public boolean doesUserFollow(long userFollowing, long userFollowed) {
        FollowsEntity follow = service.doesUserFollow(userFollowing, userFollowed);
        return follow != null;
    }

    public void follow(long userFollowing, long userFollowed) {
        FollowsEntity fe = new FollowsEntity(userFollowing, userFollowed);
        service.save(fe);
    }
    public void follow(UserModel userFollowing, UserModel userFollowed) {
        follow(userFollowing.getId(), userFollowed.getId());
    }
    public void unfollow(long userFollowing, long userFollowed) {
        FollowsEntity fe = service.doesUserFollow(userFollowing, userFollowed);
        if (fe != null) {
            service.unfollow(userFollowing, userFollowed);
        }
    }
    public int countFollowing(long userId) {
        return service.userIsFollowing(userId).size();
    }
    public int countFollowers(long userId) {
        return service.userIsFollowedBy(userId).size();
    }
    public List<UserModel> userFollows(long userId) {
        return service.userIsFollowing(userId).stream().map(fe -> fe.getUserFollowed()).map(u -> userService.getFromId(u)).collect(Collectors.toList());
    }
    public void unfollowAll(long userId) {
        service.clearUser(userId);
    }
}
