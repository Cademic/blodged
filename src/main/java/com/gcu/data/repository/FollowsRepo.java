package com.gcu.data.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.gcu.data.entities.FollowsEntity;

public interface FollowsRepo extends CrudRepository<FollowsEntity, Long> {

    @Query("SELECT * FROM follows WHERE user_following = :userFollowing AND user_followed = :userFollowed")
    public FollowsEntity doesUserFollow(long userFollowing, long userFollowed);

    @Modifying
    @Query("DELETE FROM follows WHERE user_following = :userFollowing AND user_followed = :userFollowed")
    public void unfollow(long userFollowing, long userFollowed);

    @Query("SELECT * FROM follows WHERE user_following = :userId")
    public List<FollowsEntity> userIsFollowing(long userId);

    @Query("SELECT * FROM follows WHERE user_followed = :userId")
    public List<FollowsEntity> userIsFollowedBy(long userId);

    @Modifying
    @Query("DELETE FROM follows WHERE user_following = :userId OR user_followed = :userId")
    public void clearUser(long userId);

}
