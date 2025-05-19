package com.blodged.business;


import java.util.ArrayList;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import com.blodged.data.entities.LikeEntity;
import com.blodged.data.entities.PostEntity;
import com.blodged.data.entities.ReplyEntity;
import com.blodged.data.repository.LikesRepository;
import com.blodged.data.service.PostsBusinessService;
import com.blodged.data.service.ReplyBusinessService;

import java.util.stream.Collectors;
import java.util.List;

import com.blodged.model.db.PostModel;
import com.blodged.model.db.ReplyModel;
import com.blodged.model.db.UserModel;
/**
 * Business logic class to handle a bunch of the logic for posts
 */
@Service
@SessionScope
public class PostService {
	@Autowired
	private LikesRepository likesRepository;
	@Autowired
	private PostsBusinessService postService;
	@Autowired
	private ReplyBusinessService replyService;
	public ArrayList<PostModel> posts = new ArrayList<PostModel>();
	

	public PostService(PostsBusinessService postService, ReplyBusinessService replyService) {
		this.postService=postService;
		this.replyService=replyService;
		try {
			for (PostModel e : postService.getPosts()) {
				posts.add(e);
				e.setReplies(replyService.getRepliesForPost(e));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(int i = 0; i < posts.size(); i++) {
			List<ReplyModel> replies = posts.get(i).getReplies();
			for(int j = 0; j < replies.size(); j++) {
			}
		}

	}
	/**
	 * Method that gets all posts. 
	 * @return an ArrayList of type PostModel that contains all posts
	 */
	public ArrayList<PostModel> getPosts(){
		ArrayList<PostModel> reversedList = new ArrayList<PostModel>(posts);
		Collections.reverse(reversedList);
		for (PostModel p : reversedList) {
			p.setLikeCount(likesRepository.likesForPost(p.getId()).size());
		}
		return reversedList;
	}
	
	
	
	/**
	 * Method to get the replies for a specific post
	 * @param id the id of the post to get the replies for
	 * @return a List of type ReplyModel that contains all the replies for the post
	 */
	public List<ReplyModel> getReplies(long id){
		PostModel model = getPostById(id);
		List<ReplyModel> returnPosts = replyService.getRepliesForPost(model);
		Collections.reverse(returnPosts);
		return returnPosts;
	}
	
	
	
	/**
	 * Allows you to get a specific post by id.
	 * @param id id of the post you want to get
	 * @return PostModel of the post
	 */
	
	public PostModel getPostById(long id) {
		for(PostModel post : posts) {
			if(post.getId() == id) {
				post.setLikeCount(likesRepository.likesForPost(post.getId()).size());
				return post;
			}
		}
		return new PostModel();
	}
	
	/**
	 * refreshes the memory to match the db
	 */
	public void refresh() {
		posts.clear();
		for (PostModel e : postService.getPosts()) {
			posts.add(e);
		}
	}
/**
 * Method that allows you to like a post
 * @param userId the id of the user trying to like the post
 * @param postId the id of the post the user is trying to like
 * @return boolean based on the outcome
 */
	public boolean likePost(long userId, long postId) {
		LikeEntity e = new LikeEntity(userId, postId);

		LikeEntity ee = likesRepository.save(e);
		return ee != null;
	}
	public boolean unlikePost(long userId, long postId) {
		LikeEntity e = likesRepository.userHasLiked(userId, postId);
		if (e != null) {
			likesRepository.delete(e);
			return true;
		} else {
			return false;
		}
	}
/**
 * Method to search for posts that contain a certain term
 * @param searchTerm the term to search for
 * @return the posts that contain the term
 */
	public List<PostModel> search(String searchTerm) {
		List<PostModel> post = posts.stream().filter(p -> p.getPostContent().toLowerCase().contains(searchTerm.toLowerCase())).collect(Collectors.toList());
		return post;
	}
/**
 * Method to create a new post
 * @param user the user that is creating the post
 * @param postContent the content of the post
 */
	public void createPost(UserModel user, String postContent) {
		PostEntity pe = postService.saveNew(user, postContent);
		if (pe != null) {
			PostModel m = new PostModel(user, postContent, pe.getId());
			posts.add(m);
		}
	}
	/**
	 * Method to update the content of a post
	 * @param post the post that you want to update
	 * @param content the new content of the post
	 */
	public void updateContent(PostModel post, String content) {
		postService.updateContent(post, content);
	}
	/**
	 * Method to create a reply for a post
	 * @param id id of the post
	 * @param user the user that is creating the reply
	 * @param replyContent the content of the reply
	 * @return
	 */
	public ReplyModel createReply(long id, UserModel user, String replyContent) {
		PostModel p = getPostById(id);
		ReplyEntity re = postService.saveNewReply(user, p, replyContent);
		if(re != null) {
			ReplyModel r = new ReplyModel(user, replyContent, re.getId());
			p.createReply(r);
			return r;
		}
		return null;
	}
	/**
	 * Method to delete a post
	 * @param id the id of the post to delete
	 */
	public void deletePost(long id) {
		for (LikeEntity le : likesRepository.likesForPost(id)) {
			likesRepository.delete(le);
		}
		replyService.deleteRepliesForPost(id);
		postService.delete(id);
	}
}
