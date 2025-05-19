package com.blodged.data.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.blodged.data.entities.PostEntity;
import com.blodged.data.entities.metadata.PostMetadataEntity;

/**
 * Database interaction object for the posts table
 */
public interface PostRepo extends CrudRepository<PostEntity, Long> {
    /**
     * find all the posts of a specific user
     * @param userId the id of the user to check
     * @return a list of their posts
     */
    @Query(value = "SELECT * FROM posts WHERE post_user_id = :userId")
    public List<PostEntity> findByUser(long userId);

    /**
     * delete a post based on its ID
     * @param id the ID of the post to change
     */
    @Modifying
    @Query(value = "DELETE FROM posts WHERE id = :id")
    public void deleteId(long id);

    /**
     * update the content of a post
     * @param postId the ID of the post to update
     * @param content the new content of the post
     */
    @Modifying
    @Query(value = "UPDATE posts SET post_content = :content WHERE id = :postId")
    public void updateContent(long postId, String content);

    /**
     * Select and build an API representation of a post entity based on its ID
     * @param id the id of the post to find
     * @return the API representation of the post
     */
    @Query(value = "SELECT posts.id, posts.post_content, au.username as authorUsername, COALESCE(lc, 0) as likeCount FROM posts LEFT JOIN (SELECT id, username FROM users GROUP BY id) au ON au.id = posts.post_user_id LEFT JOIN (SELECT post_id, count(*) lc FROM likes GROUP BY post_id) ltc ON ltc.post_id = posts.id WHERE posts.id = :id;")
    PostMetadataEntity getPostMetaById(long id);

    /**
     * Select and build API representations of all post entities
     * @return API representations of all post entities
     */
    @Query(value = "SELECT posts.id, posts.post_content, au.username as authorUsername, COALESCE(lc, 0) as likeCount FROM posts LEFT JOIN (SELECT id, username FROM users GROUP BY id) au ON au.id = posts.post_user_id LEFT JOIN (SELECT post_id, count(*) lc FROM likes GROUP BY post_id) ltc ON ltc.post_id = posts.id;")
    List<PostMetadataEntity> getAllPostMeta();

    /**
     * Select and build an API representation of a post entity based on the username of the author
     * @param username the username to search on
     * @return API representations of the user's post entities
     */
    @Query(value = "SELECT posts.id, posts.post_content, au.username as authorUsername, COALESCE(lc, 0) as likeCount FROM posts LEFT JOIN (SELECT id, username FROM users GROUP BY id) au ON au.id = posts.post_user_id LEFT JOIN (SELECT post_id, count(*) lc FROM likes GROUP BY post_id) ltc ON ltc.post_id = posts.id WHERE au.username = :username;")
    List<PostMetadataEntity> getPostMetaByUsername(String username);

}
