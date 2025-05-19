package com.blodged.data.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.blodged.data.entities.LikeEntity;

/**
 * Database interaction object for the likes table
 */
public interface LikesRepository extends CrudRepository<LikeEntity, Long> {

    /**
     * check if a specified user has liked a specified post
     * @param userId the id of the user to check
     * @param postId the id of the post to check
     * @return the LikeEntity of the like if successful, null if not
     */
    @Query("SELECT * FROM likes WHERE user_id = :userId AND post_id = :postId")
    public LikeEntity userHasLiked(long userId, long postId);

    /**
     * get all likes for a specified post
     * @param postId the post to check
     * @return a list of like entities for the aforementioned post
     */
    @Query("SELECT * FROM likes WHERE post_id = :postId")
    public List<LikeEntity> likesForPost(long postId);

    /**
     * get all likes for a specified user
     * @param userId the user to check
     * @return a list of like entities for the aforementioned user
     */
    @Query("SELECT * FROM likes WHERE user_id = :userId")
    public List<LikeEntity> likesForUser(long userId);

    /**
     * get a string list representation of the usernames of every user who liked a post.
     * **For API use only**
     * @param postId the post to check
     * @return a list of the usernames who liked the post
     */
    @Query("SELECT u.username as username FROM likes LEFT JOIN (select id, username FROM users GROUP BY id) u ON u.id = likes.user_id WHERE likes.post_id = :postId;")
    List<String> usersWhoLikePost(long postId);
    
}
