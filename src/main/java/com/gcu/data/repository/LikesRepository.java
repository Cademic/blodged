package com.gcu.data.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.gcu.data.entities.LikeEntity;

public interface LikesRepository extends CrudRepository<LikeEntity, Long> {

    @Query("SELECT * FROM likes WHERE user_id = :userId AND post_id = :postId")
    public LikeEntity userHasLiked(long userId, long postId);

    @Query("SELECT * FROM likes WHERE post_id = :postId")
    public List<LikeEntity> likesForPost(long postId);

    @Query("SELECT * FROM likes WHERE user_id = :userId")
    public List<LikeEntity> likesForUser(long userId);

    @Query("SELECT u.username as username FROM likes LEFT JOIN (select id, username FROM users GROUP BY id) u ON u.id = likes.user_id WHERE likes.post_id = :postId;")
    List<String> usersWhoLikePost(long postId);
    
}
