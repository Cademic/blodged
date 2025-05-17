package com.gcu.data.repository;

import java.util.List;

import com.gcu.data.entities.metadata.PostMetadataEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.gcu.data.entities.PostEntity;
import org.springframework.data.repository.query.Param;

public interface PostRepo extends CrudRepository<PostEntity, Long> {

    @Query(value = "SELECT * FROM posts WHERE post_user_id = :userId")
    public List<PostEntity> findByUser(long userId);

    @Modifying
    @Query(value = "DELETE FROM posts WHERE id = :id")
    public void deleteId(long id);

    @Modifying
    @Query(value = "UPDATE posts SET post_content = :content WHERE id = :postId")
    public void updateContent(long postId, String content);

    @Query(value = "SELECT posts.id, posts.post_content, au.username as authorUsername, COALESCE(lc, 0) as likeCount FROM posts LEFT JOIN (SELECT id, username FROM users GROUP BY id) au ON au.id = posts.post_user_id LEFT JOIN (SELECT post_id, count(*) lc FROM likes GROUP BY post_id) ltc ON ltc.post_id = posts.id WHERE posts.id = :id;")
    PostMetadataEntity getPostMetaById(long id);

    @Query(value = "SELECT posts.id, posts.post_content, au.username as authorUsername, COALESCE(lc, 0) as likeCount FROM posts LEFT JOIN (SELECT id, username FROM users GROUP BY id) au ON au.id = posts.post_user_id LEFT JOIN (SELECT post_id, count(*) lc FROM likes GROUP BY post_id) ltc ON ltc.post_id = posts.id;")
    List<PostMetadataEntity> getAllPostMeta();

    @Query(value = "SELECT posts.id, posts.post_content, au.username as authorUsername, COALESCE(lc, 0) as likeCount FROM posts LEFT JOIN (SELECT id, username FROM users GROUP BY id) au ON au.id = posts.post_user_id LEFT JOIN (SELECT post_id, count(*) lc FROM likes GROUP BY post_id) ltc ON ltc.post_id = posts.id WHERE au.username = :username;")
    List<PostMetadataEntity> getPostMetaByUsername(String username);

}
