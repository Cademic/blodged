package com.gcu.data.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.gcu.data.entities.FollowsEntity;

/**
 * Database interaction object for the follows table
 */
public interface FollowsRepo extends CrudRepository<FollowsEntity, Long> {

    /**
     * check if the former user follows the latter user
     * @param userFollowing the user who is following
     * @param userFollowed the user who is followed
     * @return the user who is followed if successful, null if not
     */
    @Query("SELECT * FROM follows WHERE user_following = :userFollowing AND user_followed = :userFollowed")
    public FollowsEntity doesUserFollow(long userFollowing, long userFollowed);

    /**
     * unfollow a user
     * @param userFollowing the user who is unfollowing
     * @param userFollowed the user who is unfollowed
     */
    @Modifying
    @Query("DELETE FROM follows WHERE user_following = :userFollowing AND user_followed = :userFollowed")
    public void unfollow(long userFollowing, long userFollowed);

    /**
     * get all users who a specified user is following
     * @param userId the id of the user to check
     * @return the follows entities by which either user can be accessed
     */
    @Query("SELECT * FROM follows WHERE user_following = :userId")
    public List<FollowsEntity> userIsFollowing(long userId);

    /**
     * get all users who a specified user is followed by
     * @param userId the id of the user to check
     * @return the follows entities by which either user can be accessed
     */
    @Query("SELECT * FROM follows WHERE user_followed = :userId")
    public List<FollowsEntity> userIsFollowedBy(long userId);

    /**
     * clear all following/followed by relationships of a user
     * typically only used in deletions to negate foreign key constraints
     * @param userId the user to delete from
     */
    @Modifying
    @Query("DELETE FROM follows WHERE user_following = :userId OR user_followed = :userId")
    public void clearUser(long userId);

}
