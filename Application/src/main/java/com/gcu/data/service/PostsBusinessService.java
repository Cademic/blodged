package com.gcu.data.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gcu.data.entities.LikeEntity;
import com.gcu.data.entities.metadata.PostMetadataEntity;
import com.gcu.data.repository.LikesRepository;
import com.gcu.model.db.metadata.PostMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gcu.data.entities.PostEntity;
import com.gcu.data.entities.ReplyEntity;
import com.gcu.data.repository.PostRepo;
import com.gcu.data.repository.ReplyRepo;
import com.gcu.model.db.PostModel;
import com.gcu.model.db.UserModel;

@Service
public class PostsBusinessService  {
    
    @Autowired
    private PostRepo service;
    @Autowired
    private ReplyRepo replyService;
    @Autowired
    private UserBusinessService userService;
    @Autowired
    private LikesRepository likesRepository;


    public PostsBusinessService(PostRepo repo, UserBusinessService service) {
        this.service=repo;
        this.userService=service;
    }

    public List<PostModel> getPosts() {
        try {
            List<PostEntity> postEntities = new ArrayList<>();
            service.findAll().forEach(postEntities::add);
            List<PostModel> postDomain = new ArrayList<>();
            for (PostEntity e : postEntities) {
                UserModel m = userService.getFromId(e.getPostUserId());
                postDomain.add(new PostModel(m, e.getPostContent(), e.getId()));
            }
            return postDomain;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
    public long save(PostModel post) {
        PostEntity p = new PostEntity(post.getPostContent(), post.getPostUser().getId());
        return service.save(p).getId();
    }
    public PostEntity saveNew(UserModel user, String content) {
        PostEntity p = new PostEntity(content, user.getId());
        // Ensure likeCount is initialized
        return service.save(p);
    }
    public ReplyEntity saveNewReply(UserModel user, PostModel post, String content) {
    	ReplyEntity r = new ReplyEntity(user.getId(), post.getId(), content);
    	return replyService.save(r);
    }
    public PostModel getFromId(long id) {
        PostEntity pe = service.findById(id).orElse(null);
        if (pe != null) {
            UserModel user = userService.getFromId(pe.getPostUserId());
            return new PostModel(user, pe.getPostContent(), pe.getId());
        } else return null;

    }
    public PostMetadata getMetaById(long id) {
        PostMetadataEntity pe = service.getPostMetaById(id);
        return fromEntity(pe);
    }
    public List<PostMetadata> getAllPostMeta() {
        List<PostMetadataEntity> pel = service.getAllPostMeta();
        List<PostMetadata> list = new ArrayList<>();
        for (PostMetadataEntity pe : pel) {
            list.add(fromEntity(pe));
        }
        return list;
    }

    public List<PostMetadata> getMetaByUsername(String username) {
        List<PostMetadataEntity> pel = service.getPostMetaByUsername(username);
        List<PostMetadata> list = new ArrayList<>();
        for (PostMetadataEntity pe : pel) {
            list.add(fromEntity(pe));
        }
        return list;
    }
    private PostMetadata fromEntity(PostMetadataEntity pe) {
        List<ReplyEntity> replies = replyService.findByPost(pe.id);
        long[] ids = new long[replies.size()];
        for (int i = 0; i < replies.size(); i++) {
            ids[i] = replies.get(i).getId();
        }
        return new PostMetadata(pe.id, pe.authorUsername, pe.postContent, ids, pe.likeCount);
    }
    public void delete(long id) {
        service.deleteId(id);
    }
    public void updateContent(PostModel post, String content) {
        service.updateContent(post.getId(), content);
    }

}
