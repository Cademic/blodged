package com.blodged.data.repository;

import java.util.List;

import com.blodged.data.entities.metadata.ReplyMetadataEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.blodged.data.entities.ReplyEntity;
/**
 * Database interaction object for the replies table
 */
public interface ReplyRepo extends CrudRepository<ReplyEntity, Long>{
    /**
     * find all the replies of a specific post
     * @param postId the id of the post to search
     * @return all the replies of the post
     */
    @Query("SELECT * FROM replies WHERE replies.post_id = :postId")
    public List<ReplyEntity> findByPost(long postId);

    /**
     * update the content of a specific reply
     * @param content the new content of the reply
     * @param replyId the id of the reply to update
     */
    @Modifying
    @Query("UPDATE replies SET content = :content WHERE id = :replyId")
    public void updateContent(String content, long replyId);

    /**
     * delete a reply by its ID
     * @param id the ID of the reply to delete
     */
    @Modifying
    @Query("DELETE FROM replies WHERE id = :id")
    public void delete(long id);

    /**
     * Get the API representations of all the comments on a specific post
     * @param id the id of the post to search
     * @return the API representations of that post's comments
     */
    @Query("SELECT replies.id, u.username as replyAuthorUsername, post_id, content FROM replies LEFT JOIN (select id, username FROM users GROUP BY id) u ON u.id = replies.user_id WHERE post_id = :id")
    List<ReplyMetadataEntity> getForPost(long id);

    /**
     * Get the API representations of all the comments made by a specific user
     * @param id the id of the user to search
     * @return the API representations of that user's comments
     */
    @Query("SELECT replies.id, u.username as replyAuthorUsername, post_id, content FROM replies LEFT JOIN (select id, username FROM users GROUP BY id) u ON u.id = replies.user_id WHERE replies.user_id = :id")
    List<ReplyMetadataEntity> getForUser(long id);

}
