package com.gcu.data.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.gcu.model.db.metadata.ReplyMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gcu.data.entities.ReplyEntity;
import com.gcu.data.repository.ReplyRepo;
import com.gcu.model.db.PostModel;
import com.gcu.model.db.ReplyModel;
import com.gcu.model.db.UserModel;

@Service
public class ReplyBusinessService {
 
    @Autowired
    private ReplyRepo replyService;
    @Autowired
    private UserBusinessService userBusinessService;

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
    public void deleteRepliesForPost(long postId) {
        for (ReplyEntity e : replyService.findByPost(postId)) {
            replyService.delete(e);
        }
    }
    public ReplyModel getById(long id) {
        ReplyEntity re = replyService.findById(id).orElse(null);
        if (re != null) {
            UserModel u = userBusinessService.getFromId(re.getUserId());
            ReplyModel rm = new ReplyModel(u, re.getContent(), re.getId());
            return rm;
        }
        return null;
    }
    public void setContent(ReplyModel m, String content) {
        replyService.updateContent(content, m.getId());
    }
    public void delete(ReplyModel m) {
        replyService.delete(m.getId());
    }

    public List<ReplyMetadata> getForPost(long postId) {
        return replyService.getForPost(postId).stream().map(re -> new ReplyMetadata(re.id, re.replyAuthorUsername, re.postId, re.replyContent)).collect(Collectors.toList());
    }
    public List<ReplyMetadata> getForUser(long userId) {
        return replyService.getForUser(userId).stream().map(re -> new ReplyMetadata(re.id, re.replyAuthorUsername, re.postId, re.replyContent)).collect(Collectors.toList());
    }

//    public ReplyModel addReplyToPost(PostModel p, UserModel u, String content) {
//        ReplyEntity e = new ReplyEntity(u.getId(), content);
//        e = replyService.save(e);
//        return new ReplyModel(u, content, e.getId());
//    }


}
