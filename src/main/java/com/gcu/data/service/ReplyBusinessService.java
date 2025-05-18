package com.gcu.data.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gcu.data.entities.ReplyEntity;
import com.gcu.data.repository.ReplyRepo;
import com.gcu.model.db.PostModel;
import com.gcu.model.db.ReplyModel;
import com.gcu.model.db.UserModel;
import com.gcu.model.db.metadata.ReplyMetadata;
/**
 * Business logic class to help replies and the db interract
 */
@Service
public class ReplyBusinessService {
 
    @Autowired
    private ReplyRepo replyService;
    @Autowired
    private UserBusinessService userBusinessService;
/**
 * Method to get the replies for a specific post
 * @param p the post to get the replies for
 * @return a list of type ReplyModel that contails all the replies
 */
    public List<ReplyModel> getRepliesForPost(PostModel p) {
        List<ReplyModel> repliesDomain = new ArrayList<>();
        if (p != null) {
            List<ReplyEntity> replies = replyService.findByPost(p.getId());
            for (ReplyEntity re : replies) {
                UserModel ue = userBusinessService.getFromId(re.getUserId());
                if (ue != null) {
                    repliesDomain.add(new ReplyModel(ue, re.getContent(), re.getId()));
                }
            }
        }
        return repliesDomain;
    }
    /**
     * Method to delete all the replies for a specific post
     * @param postId the id of the post to delete all the replies for
     */
    public void deleteRepliesForPost(long postId) {
        for (ReplyEntity e : replyService.findByPost(postId)) {
            replyService.delete(e);
        }
    }
    /**
     * gets a specific reply by id
     * @param id the id of the reply to get
     * @return a ReplyModel of the reply
     */
    public ReplyModel getById(long id) {
        ReplyEntity re = replyService.findById(id).orElse(null);
        if (re != null) {
            UserModel u = userBusinessService.getFromId(re.getUserId());
            ReplyModel rm = new ReplyModel(u, re.getContent(), re.getId());
            return rm;
        }
        return null;
    }
    /**
     * Method to change the content of a reply
     * @param m ReplyModel of the reply to change the content for
     * @param content the new content of the reply
     */
    public void setContent(ReplyModel m, String content) {
        replyService.updateContent(content, m.getId());
    }
    /**
     * Method to delete a reply
     * @param m ReplyModel of reply to delete
     */
    public void delete(ReplyModel m) {
        replyService.delete(m.getId());
    }
    /**
     * Method to get the ReplyMetaDatas for all replies for a specific post
     * @param postId id of the post to get the replies for
     * @return List of type ReplyMetaData that contains all the replies for the post
     */
    public List<ReplyMetadata> getForPost(long postId) {
        return replyService.getForPost(postId).stream().map(re -> new ReplyMetadata(re.id, re.replyAuthorUsername, re.postId, re.replyContent)).collect(Collectors.toList());
    }
    /**
     * Method to get the ReplyMetaDatas for a specific user
     * @param userId the id of the user to get the replies for
     * @return List of type ReplyMetaData that contains all the replies for the user
     */
    public List<ReplyMetadata> getForUser(long userId) {
        return replyService.getForUser(userId).stream().map(re -> new ReplyMetadata(re.id, re.replyAuthorUsername, re.postId, re.replyContent)).collect(Collectors.toList());
    }

//    public ReplyModel addReplyToPost(PostModel p, UserModel u, String content) {
//        ReplyEntity e = new ReplyEntity(u.getId(), content);
//        e = replyService.save(e);
//        return new ReplyModel(u, content, e.getId());
//    }


}
