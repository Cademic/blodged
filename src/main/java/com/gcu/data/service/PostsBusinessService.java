package com.gcu.data.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gcu.data.entities.PostEntity;
import com.gcu.data.entities.ReplyEntity;
import com.gcu.data.entities.metadata.PostMetadataEntity;
import com.gcu.data.repository.PostRepo;
import com.gcu.data.repository.ReplyRepo;
import com.gcu.model.db.PostModel;
import com.gcu.model.db.UserModel;
import com.gcu.model.db.metadata.PostMetadata;
/**
 * Class to help posts and the db interract
 */
@Service
public class PostsBusinessService  {
    
    @Autowired
    private PostRepo service;
    @Autowired
    private ReplyRepo replyService;
    @Autowired
    private UserBusinessService userService;
    // Exists but was never used...
    // @Autowired
    // private LikesRepository likesRepository;


    public PostsBusinessService(PostRepo repo, UserBusinessService service) {
        this.service=repo;
        this.userService=service;
    }
/**
 * Gets all the posts from the db
 * @return a list of type PostModel that contains all the posts
 */
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
    /**
     * Method to save a new post to the db
     * @param post the post to save
     * @return the id of the saved post 
     */
    public long save(PostModel post) {
        PostEntity p = new PostEntity(post.getPostContent(), post.getPostUser().getId());
        return service.save(p).getId();
    }
    /**
     * Method to save a new post to the db again...
     * @param user user who the post belongs to
     * @param content content of the post
     * @return PostEntity of post
     */
    public PostEntity saveNew(UserModel user, String content) {
        PostEntity p = new PostEntity(content, user.getId());
        // Ensure likeCount is initialized
        return service.save(p);
    }
    /**
     * Method to save a new reply (again?)
     * @param user user who the reply belongs to
     * @param post parent post
     * @param content content of reply
     * @return ReplyEntity of reply
     */
    public ReplyEntity saveNewReply(UserModel user, PostModel post, String content) {
    	ReplyEntity r = new ReplyEntity(user.getId(), post.getId(), content);
    	return replyService.save(r);
    }
    /**
     * gets a post by Id
     * @param id id of the post to get
     * @return PostModel of the post
     */
    public PostModel getFromId(long id) {
        PostEntity pe = service.findById(id).orElse(null);
        if (pe != null) {
            UserModel user = userService.getFromId(pe.getPostUserId());
            return new PostModel(user, pe.getPostContent(), pe.getId());
        } else return null;

    }
    /**
     * get a PostMetaData by id
     * @param id if of the post to get
     * @return PostMetaData of post
     */
    public PostMetadata getMetaById(long id) {
        PostMetadataEntity pe = service.getPostMetaById(id);
        return fromEntity(pe);
    }
    /**
     * get all the posts in meta data form
     * @return list of type PostMetaData that contains all the posts
     */
    public List<PostMetadata> getAllPostMeta() {
        List<PostMetadataEntity> pel = service.getAllPostMeta();
        List<PostMetadata> list = new ArrayList<>();
        for (PostMetadataEntity pe : pel) {
            list.add(fromEntity(pe));
        }
        return list;
    }
    /**
     * method to get all the posts in meta data form by a specific user
     * @param username username of user to get posts for
     * @return list of type PostMetaData of all the posts for a specific user
     */
    public List<PostMetadata> getMetaByUsername(String username) {
        List<PostMetadataEntity> pel = service.getPostMetaByUsername(username);
        List<PostMetadata> list = new ArrayList<>();
        for (PostMetadataEntity pe : pel) {
            list.add(fromEntity(pe));
        }
        return list;
    }
    /**
     * Method to get a PostMetaData from a PostMetaDataEntity
     * @param pe PostMetaDataEntity to convert
     * @return new PostMetaData from the entity
     */
    private PostMetadata fromEntity(PostMetadataEntity pe) {
        List<ReplyEntity> replies = replyService.findByPost(pe.id);
        long[] ids = new long[replies.size()];
        for (int i = 0; i < replies.size(); i++) {
            ids[i] = replies.get(i).getId();
        }
        return new PostMetadata(pe.id, pe.authorUsername, pe.postContent, ids, pe.likeCount);
    }
    /**
     * method to delete a post by id
     * @param id if of the post to delete
     */
    public void delete(long id) {
        service.deleteId(id);
    }
    /**
     * method to update the content for a specific post
     * @param post the post to update
     * @param content the new content
     */
    public void updateContent(PostModel post, String content) {
        service.updateContent(post.getId(), content);
    }

}
