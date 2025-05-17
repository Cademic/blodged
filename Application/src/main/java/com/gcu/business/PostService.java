package com.gcu.business;


import java.util.ArrayList;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import com.gcu.data.entities.LikeEntity;
import com.gcu.data.entities.PostEntity;
import com.gcu.data.entities.ReplyEntity;
import com.gcu.data.repository.LikesRepository;
import com.gcu.data.service.PostsBusinessService;
import com.gcu.data.service.ReplyBusinessService;

import java.util.stream.Collectors;
import java.util.List;

import com.gcu.model.db.PostModel;
import com.gcu.model.db.ReplyModel;
import com.gcu.model.db.UserModel;

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
	
	public ArrayList<PostModel> getPosts(){
		ArrayList<PostModel> reversedList = new ArrayList<PostModel>(posts);
		Collections.reverse(reversedList);
		for (PostModel p : reversedList) {
			p.setLikeCount(likesRepository.likesForPost(p.getId()).size());
		}
		return reversedList;
	}
	
	
	
	
	public List<ReplyModel> getReplies(long id){
		PostModel model = getPostById(id);
		List<ReplyModel> returnPosts = replyService.getRepliesForPost(model);
		Collections.reverse(returnPosts);
		return returnPosts;
	}
	
	
	
	
	
	public PostModel getPostById(long id) {
		for(PostModel post : posts) {
			if(post.getId() == id) {
				post.setLikeCount(likesRepository.likesForPost(post.getId()).size());
				return post;
			}
		}
		return new PostModel();
	}
	
	public void refresh() {
		posts.clear();
		for (PostModel e : postService.getPosts()) {
			posts.add(e);
		}
	}

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

	public List<PostModel> search(String searchTerm) {
		List<PostModel> post = posts.stream().filter(p -> p.getPostContent().toLowerCase().contains(searchTerm.toLowerCase())).collect(Collectors.toList());
		return post;
	}

	public void createPost(UserModel user, String postContent) {
		PostEntity pe = postService.saveNew(user, postContent);
		if (pe != null) {
			PostModel m = new PostModel(user, postContent, pe.getId());
			posts.add(m);
		}
	}
	public void updateContent(PostModel post, String content) {
		postService.updateContent(post, content);
	}
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
	public void deletePost(long id) {
		for (LikeEntity le : likesRepository.likesForPost(id)) {
			likesRepository.delete(le);
		}
		replyService.deleteRepliesForPost(id);
		postService.delete(id);
	}
}
