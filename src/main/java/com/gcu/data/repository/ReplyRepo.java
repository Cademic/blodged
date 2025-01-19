package com.gcu.data.repository;

import java.util.List;

import com.gcu.data.entities.metadata.ReplyMetadataEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.gcu.data.entities.ReplyEntity;

public interface ReplyRepo extends CrudRepository<ReplyEntity, Long>{
    
    @Query("SELECT * FROM replies WHERE replies.post_id = :postId")
    public List<ReplyEntity> findByPost(long postId);

    @Modifying
    @Query("UPDATE replies SET content = :content WHERE id = :replyId")
    public void updateContent(String content, long replyId);

    @Modifying
    @Query("DELETE FROM replies WHERE id = :id")
    public void delete(long id);

    @Query("SELECT replies.id, u.username as replyAuthorUsername, post_id, content FROM replies LEFT JOIN (select id, username FROM users GROUP BY id) u ON u.id = replies.user_id WHERE post_id = :id")
    List<ReplyMetadataEntity> getForPost(long id);

    @Query("SELECT replies.id, u.username as replyAuthorUsername, post_id, content FROM replies LEFT JOIN (select id, username FROM users GROUP BY id) u ON u.id = replies.user_id WHERE replies.user_id = :id")
    List<ReplyMetadataEntity> getForUser(long id);

}
